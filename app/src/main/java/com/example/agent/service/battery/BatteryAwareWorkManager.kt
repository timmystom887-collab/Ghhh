package com.example.agent.service.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.repository.AgentRepository
import com.example.agent.service.IdleApiWorker
import com.example.agent.service.ModelDownloadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

/**
 * Worker that checks system battery telemetry and enforces power-saving policies:
 * pauses or cancels non-essential background tasks during low battery (<= 20%)
 * to preserve device stability and battery longevity.
 */
class BatteryMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val batteryManager = BatteryAwareWorkManager.getInstance(applicationContext)
        val isLow = batteryManager.checkAndEnforceBatteryPolicy()
        return Result.success()
    }
}

/**
 * WorkManager-based battery status monitor and background job controller.
 * Automatically pauses non-essential background work (idle API audits, model downloads)
 * when battery is <= 20% and resumes when power is restored.
 */
class BatteryAwareWorkManager private constructor(private val context: Context) {

    private fun getRepository(): AgentRepository = AgentRepository(context)

    private val _isLowBatteryState = MutableStateFlow(false)
    val isLowBatteryState: StateFlow<Boolean> = _isLowBatteryState.asStateFlow()

    private val _batteryPercentage = MutableStateFlow(100)
    val batteryPercentage: StateFlow<Int> = _batteryPercentage.asStateFlow()

    private fun getWorkManager(): WorkManager? {
        return try {
            WorkManager.getInstance(context)
        } catch (e: Exception) {
            try {
                val config = Configuration.Builder().build()
                WorkManager.initialize(context, config)
                WorkManager.getInstance(context)
            } catch (e2: Exception) {
                null
            }
        }
    }

    companion object {
        const val UNIQUE_IDLE_WORK = "agent_idle_api_audit_work"
        const val UNIQUE_DOWNLOAD_WORK = "agent_model_download_work"
        const val UNIQUE_BATTERY_MONITOR_WORK = "agent_battery_monitor_periodic"
        const val LOW_BATTERY_THRESHOLD = 20

        @Volatile
        private var INSTANCE: BatteryAwareWorkManager? = null

        fun getInstance(context: Context): BatteryAwareWorkManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BatteryAwareWorkManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    /**
     * Initializes periodic battery state monitoring via WorkManager.
     */
    fun startPeriodicBatteryMonitoring() {
        val periodicRequest = PeriodicWorkRequestBuilder<BatteryMonitorWorker>(
            15, TimeUnit.MINUTES
        ).build()

        getWorkManager()?.enqueueUniquePeriodicWork(
            UNIQUE_BATTERY_MONITOR_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    /**
     * Evaluates current battery telemetry and pauses/resumes non-essential workers.
     * @return true if battery is currently low (<= 20%)
     */
    suspend fun checkAndEnforceBatteryPolicy(): Boolean {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val pct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
        _batteryPercentage.value = pct
        val isLow = pct <= LOW_BATTERY_THRESHOLD && !isCharging
        val wasLow = _isLowBatteryState.value
        _isLowBatteryState.value = isLow

        if (isLow && !wasLow) {
            pauseNonEssentialTasks("Battery level critical ($pct%). Throttling non-essential tasks to conserve power.")
        } else if (!isLow && wasLow) {
            resumeNonEssentialTasks("Power nominal ($pct%${if (isCharging) " Charging" else ""}). Resuming standard background operations.")
        }

        return isLow
    }

    /**
     * Cancels / pauses non-essential background workloads.
     */
    suspend fun pauseNonEssentialTasks(reason: String) {
        getWorkManager()?.cancelUniqueWork(UNIQUE_IDLE_WORK)
        getWorkManager()?.cancelUniqueWork(UNIQUE_DOWNLOAD_WORK)

        try {
            getRepository().insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🔋 [Power Governance System]: $reason\nNon-essential workers (Idle Node Audits & Local SLM Downloads) paused.",
                    type = "text"
                )
            )
        } catch (e: Exception) {
            // Ignored in unit test environments where database may be mocked/transient
        }
    }

    /**
     * Resumes non-essential background jobs with battery-not-low constraints.
     */
    suspend fun resumeNonEssentialTasks(reason: String) {
        val batteryConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val idleWorkRequest = OneTimeWorkRequestBuilder<IdleApiWorker>()
            .setConstraints(batteryConstraints)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        getWorkManager()?.enqueueUniqueWork(
            UNIQUE_IDLE_WORK,
            ExistingWorkPolicy.REPLACE,
            idleWorkRequest
        )

        try {
            getRepository().insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "⚡ [Power Governance System]: $reason\nNon-essential tasks re-enqueued with Battery-Not-Low constraints.",
                    type = "text"
                )
            )
        } catch (e: Exception) {
            // Ignored in unit test environments where database may be mocked/transient
        }
    }

    /**
     * Helper to enqueue background model downloads only if battery is healthy.
     */
    fun enqueueModelDownloadIfSafe(modelId: String): Boolean {
        if (_isLowBatteryState.value) {
            return false
        }

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val request = OneTimeWorkRequestBuilder<ModelDownloadWorker>()
            .setConstraints(constraints)
            .build()

        getWorkManager()?.enqueueUniqueWork(
            UNIQUE_DOWNLOAD_WORK,
            ExistingWorkPolicy.REPLACE,
            request
        )
        return true
    }
}
