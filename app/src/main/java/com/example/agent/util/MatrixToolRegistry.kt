package com.example.agent.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.BatteryManager
import android.provider.AlarmClock
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ToolExecutionResult(
    val toolName: String,
    val success: Boolean,
    val message: String,
    val data: Map<String, String> = emptyMap()
)

class MatrixToolRegistry(private val context: Context) {

    fun openApp(appName: String): ToolExecutionResult {
        return try {
            val pm = context.packageManager
            val lowerName = appName.lowercase().trim()

            val targetPackage = when {
                lowerName.contains("chrome") || lowerName.contains("browser") -> "com.android.chrome"
                lowerName.contains("map") -> "com.google.android.apps.maps"
                lowerName.contains("calc") -> "com.google.android.calculator"
                lowerName.contains("clock") || lowerName.contains("alarm") -> "com.google.android.deskclock"
                lowerName.contains("youtube") -> "com.google.android.youtube"
                lowerName.contains("gmail") || lowerName.contains("mail") -> "com.google.android.gm"
                lowerName.contains("contact") -> "com.android.contacts"
                lowerName.contains("photo") || lowerName.contains("gallery") -> "com.google.android.apps.photos"
                lowerName.contains("camera") -> "com.android.camera"
                lowerName.contains("setting") -> "com.android.settings"
                else -> null
            }

            var launchIntent: Intent? = null
            if (targetPackage != null) {
                launchIntent = pm.getLaunchIntentForPackage(targetPackage)
            }

            if (launchIntent == null) {
                // Search installed apps
                val installed = pm.getInstalledApplications(0)
                for (app in installed) {
                    val label = pm.getApplicationLabel(app).toString().lowercase()
                    if (label.contains(lowerName)) {
                        launchIntent = pm.getLaunchIntentForPackage(app.packageName)
                        break
                    }
                }
            }

            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                ToolExecutionResult("open_app", true, "Launched application: $appName")
            } else {
                // Fallback: search in browser or market
                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$appName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(searchIntent)
                ToolExecutionResult("open_app", true, "App not installed locally. Routed to search portal: $appName")
            }
        } catch (e: Exception) {
            ToolExecutionResult("open_app", false, "Failed to launch $appName: ${e.localizedMessage}")
        }
    }

    fun makeCall(phoneNumber: String): ToolExecutionResult {
        return try {
            val cleanPhone = phoneNumber.trim().ifEmpty { "555-0199" }
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", cleanPhone, null)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolExecutionResult("make_call", true, "Matrix dialer opened for destination: $cleanPhone")
        } catch (e: Exception) {
            ToolExecutionResult("make_call", false, "Call failure: ${e.localizedMessage}")
        }
    }

    fun sendSms(phoneNumber: String, text: String): ToolExecutionResult {
        return try {
            val cleanPhone = phoneNumber.trim().ifEmpty { "555-0199" }
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", cleanPhone, null)).apply {
                putExtra("sms_body", text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolExecutionResult("send_sms", true, "Transmitted SMS payload to $cleanPhone: \"$text\"")
        } catch (e: Exception) {
            ToolExecutionResult("send_sms", false, "SMS dispatch error: ${e.localizedMessage}")
        }
    }

    fun scheduleAlarm(hour: Int, minute: Int, message: String): ToolExecutionResult {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, message.ifEmpty { "Agent Smith Matrix Protocol" })
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolExecutionResult("schedule_alarm", true, "Alarm scheduled for ${String.format(Locale.US, "%02d:%02d", hour, minute)} with memo: '$message'")
        } catch (e: Exception) {
            ToolExecutionResult("schedule_alarm", false, "Alarm configuration error: ${e.localizedMessage}")
        }
    }

    fun setTorch(enable: Boolean): ToolExecutionResult {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return ToolExecutionResult("set_torch", false, "Camera hardware not detected.")
            cameraManager.setTorchMode(cameraId, enable)
            ToolExecutionResult("set_torch", true, "Illumination node set to ${if (enable) "ONLINE" else "OFFLINE"}")
        } catch (e: Exception) {
            ToolExecutionResult("set_torch", false, "Torch toggle error: ${e.localizedMessage}")
        }
    }

    fun openSettings(type: String): ToolExecutionResult {
        return try {
            val intent = when (type.lowercase().trim()) {
                "wifi", "wi-fi", "network" -> Intent(Settings.ACTION_WIFI_SETTINGS)
                "bluetooth", "bt" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                "display", "screen" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
                "sound", "volume", "audio" -> Intent(Settings.ACTION_SOUND_SETTINGS)
                "apps", "applications" -> Intent(Settings.ACTION_APPLICATION_SETTINGS)
                "battery", "power" -> Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
                else -> Intent(Settings.ACTION_SETTINGS)
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolExecutionResult("open_settings", true, "Matrix System Settings opened: $type")
        } catch (e: Exception) {
            ToolExecutionResult("open_settings", false, "Settings portal error: ${e.localizedMessage}")
        }
    }

    fun getBatteryTelemetry(): ToolExecutionResult {
        return try {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

            ToolExecutionResult(
                "get_battery",
                true,
                "Battery at $batteryPct% (${if (isCharging) "Charging" else "Discharging"})",
                mapOf("percentage" to "$batteryPct%", "charging" to isCharging.toString())
            )
        } catch (e: Exception) {
            ToolExecutionResult("get_battery", false, "Telemetry read error: ${e.localizedMessage}")
        }
    }

    fun evaluateMath(expression: String): ToolExecutionResult {
        return try {
            val cleaned = expression.replace("x", "*").replace("X", "*")
            // Simple expression evaluator
            val result = evaluateArithmetic(cleaned)
            ToolExecutionResult("evaluate_math", true, "Calculation result: $expression = $result", mapOf("result" to result.toString()))
        } catch (e: Exception) {
            ToolExecutionResult("evaluate_math", false, "Evaluation error: ${e.localizedMessage}")
        }
    }

    private fun evaluateArithmetic(expr: String): Double {
        val tokens = expr.filter { it.isDigit() || it == '.' || it == '+' || it == '-' || it == '*' || it == '/' || it == ' ' }
        val parts = tokens.split("+", "-", "*", "/").map { it.trim().toDoubleOrNull() ?: 0.0 }
        val ops = tokens.filter { it == '+' || it == '-' || it == '*' || it == '/' }
        if (parts.isEmpty()) return 0.0
        var res = parts[0]
        for (i in ops.indices) {
            if (i + 1 < parts.size) {
                when (ops[i]) {
                    '+' -> res += parts[i + 1]
                    '-' -> res -= parts[i + 1]
                    '*' -> res *= parts[i + 1]
                    '/' -> if (parts[i + 1] != 0.0) res /= parts[i + 1]
                }
            }
        }
        return res
    }

    fun getSystemDiagnostics(): ToolExecutionResult {
        val runtime = Runtime.getRuntime()
        val usedMemMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMemMB = runtime.maxMemory() / (1024 * 1024)
        val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val report = "Matrix Node Status: NOMINAL | Time: $timeStr | Memory Used: ${usedMemMB}MB / ${maxMemMB}MB | Active Threads: ${Thread.activeCount()}"
        return ToolExecutionResult("system_diagnostics", true, report)
    }

    fun executeTool(toolName: String, params: Map<String, String> = emptyMap()): String {
        val result = when (toolName.lowercase()) {
            "get_battery", "battery" -> getBatteryTelemetry()
            "system_diagnostics", "diagnostics" -> getSystemDiagnostics()
            "evaluate_math", "math", "calc" -> evaluateMath(params["expression"] ?: params["expr"] ?: "0")
            "set_torch", "torch", "flashlight" -> setTorch(params["enabled"]?.toBoolean() ?: true)
            "open_app", "app" -> openApp(params["app_name"] ?: params["name"] ?: "")
            "send_sms", "sms" -> sendSms(params["phone_number"] ?: params["phone"] ?: "", params["message"] ?: params["text"] ?: "")
            "make_call", "call" -> makeCall(params["phone_number"] ?: params["phone"] ?: "")
            else -> ToolExecutionResult(toolName, false, "Unknown tool: $toolName")
        }
        return result.message
    }
}
