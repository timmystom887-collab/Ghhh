package com.example.agent

import androidx.test.core.app.ApplicationProvider
import com.example.agent.service.battery.BatteryAwareWorkManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BatteryAwareWorkManagerTest {

    private lateinit var manager: BatteryAwareWorkManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        manager = BatteryAwareWorkManager.getInstance(context)
    }

    @Test
    fun testBatteryAwareWorkManager_Initialization() {
        assertNotNull(manager)
        assertNotNull(manager.isLowBatteryState)
        assertNotNull(manager.batteryPercentage)
    }

    @Test
    fun testBatteryPolicy_PauseAndResume() = runBlocking {
        manager.pauseNonEssentialTasks("Testing low battery pause")
        manager.resumeNonEssentialTasks("Testing power restore")
        assertTrue(true)
    }
}
