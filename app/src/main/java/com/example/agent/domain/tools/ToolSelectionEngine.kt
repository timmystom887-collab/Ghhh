package com.example.agent.domain.tools

import com.example.agent.domain.model.ToolResult
import com.example.agent.util.MatrixToolRegistry

enum class IntentRouteCategory {
    SYSTEM_DEVICE_TOOL,
    TELEPHONY_VOICE_MISSION,
    MCP_EXTERNAL_SKILL,
    LLM_REASONING_SYNTHESIS
}

data class ClassifiedToolCall(
    val toolName: String,
    val category: IntentRouteCategory,
    val confidence: Float,
    val arguments: Map<String, String>,
    val isValid: Boolean,
    val validationErrors: List<String> = emptyList()
)

class ToolSelectionEngine(
    private val toolRegistry: MatrixToolRegistry
) {

    /**
     * 1. Intent Routing & Tool Classifier
     */
    fun classifyAndBind(input: String): ClassifiedToolCall {
        val trimmed = input.trim()
        val lower = trimmed.lowercase()

        when {
            // Alarm & Schedule
            lower.startsWith("alarm") || lower.contains("set alarm") || lower.contains("wake me up") -> {
                val numbers = Regex("\\d+").findAll(trimmed).map { it.value.toInt() }.toList()
                val hour = numbers.getOrNull(0) ?: 8
                val minute = numbers.getOrNull(1) ?: 0
                val label = if (trimmed.contains("for", ignoreCase = true)) {
                    trimmed.substringAfter("for").trim()
                } else "Agent Smith Protocol"

                val errors = mutableListOf<String>()
                if (hour !in 0..23) errors.add("Hour must be between 0 and 23.")
                if (minute !in 0..59) errors.add("Minute must be between 0 and 59.")

                return ClassifiedToolCall(
                    toolName = "set_alarm",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.95f,
                    arguments = mapOf(
                        "hour" to hour.toString(),
                        "minute" to minute.toString(),
                        "message" to label
                    ),
                    isValid = errors.isEmpty(),
                    validationErrors = errors
                )
            }

            // SMS Dispatch
            lower.startsWith("sms") || lower.contains("send sms") || lower.contains("send text") -> {
                val phoneMatch = Regex("[0-9-]{7,15}").find(trimmed)
                val phone = phoneMatch?.value ?: ""
                val body = if (phone.isNotBlank()) {
                    trimmed.substringAfter(phone).trim().ifEmpty { "Transmission from Agent Smith." }
                } else {
                    trimmed.removePrefix("sms").trim()
                }

                val errors = mutableListOf<String>()
                if (phone.isBlank()) errors.add("Missing target phone number.")

                return ClassifiedToolCall(
                    toolName = "send_sms",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.92f,
                    arguments = mapOf(
                        "phone_number" to phone,
                        "message" to body
                    ),
                    isValid = errors.isEmpty(),
                    validationErrors = errors
                )
            }

            // Phone Call / Telephony
            lower.startsWith("call ") || lower.contains("make a call") || lower.contains("dial ") -> {
                val phoneMatch = Regex("[0-9-]{7,15}").find(trimmed)
                val phone = phoneMatch?.value ?: ""
                val isReservation = lower.contains("reservation") || lower.contains("appointment")

                return ClassifiedToolCall(
                    toolName = "make_call",
                    category = if (isReservation) IntentRouteCategory.TELEPHONY_VOICE_MISSION else IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.90f,
                    arguments = mapOf(
                        "phone_number" to phone,
                        "mission_type" to if (isReservation) "RESERVATION" else "DIRECT"
                    ),
                    isValid = phone.isNotBlank(),
                    validationErrors = if (phone.isBlank()) listOf("Destination telephone number required.") else emptyList()
                )
            }

            // Math Calculation
            lower.startsWith("calc") || lower.startsWith("calculate") || ((lower.contains("+") || lower.contains("*") || lower.contains("/")) && lower.any { it.isDigit() }) -> {
                val expr = trimmed.removePrefix("calc ").removePrefix("calculate ").trim()
                return ClassifiedToolCall(
                    toolName = "evaluate_math",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.98f,
                    arguments = mapOf("expression" to expr),
                    isValid = expr.isNotBlank()
                )
            }

            // Torch / Flashlight
            lower.contains("torch") || lower.contains("flashlight") -> {
                val enable = !lower.contains("off")
                return ClassifiedToolCall(
                    toolName = "set_torch",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.96f,
                    arguments = mapOf("enabled" to enable.toString()),
                    isValid = true
                )
            }

            // Battery Telemetry
            lower.contains("battery") || lower == "get_battery" -> {
                return ClassifiedToolCall(
                    toolName = "get_battery",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.99f,
                    arguments = emptyMap(),
                    isValid = true
                )
            }

            // App Launcher
            lower.startsWith("open ") || lower.startsWith("app ") -> {
                val appName = trimmed.removePrefix("open ").removePrefix("app ").trim()
                return ClassifiedToolCall(
                    toolName = "open_app",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.93f,
                    arguments = mapOf("app_name" to appName),
                    isValid = appName.isNotBlank()
                )
            }

            // System Diagnostics
            lower == "diagnostics" || lower.contains("system status") -> {
                return ClassifiedToolCall(
                    toolName = "system_diagnostics",
                    category = IntentRouteCategory.SYSTEM_DEVICE_TOOL,
                    confidence = 0.99f,
                    arguments = emptyMap(),
                    isValid = true
                )
            }

            // Default: LLM Reasoning
            else -> {
                return ClassifiedToolCall(
                    toolName = "llm_reasoning",
                    category = IntentRouteCategory.LLM_REASONING_SYNTHESIS,
                    confidence = 1.0f,
                    arguments = mapOf("prompt" to input),
                    isValid = true
                )
            }
        }
    }

    /**
     * 2. Execution Handoff with Fallback & Retry
     */
    suspend fun executeWithRetry(
        classifiedCall: ClassifiedToolCall,
        maxRetries: Int = 2
    ): ToolResult {
        if (!classifiedCall.isValid) {
            return ToolResult.Error(
                code = "INVALID_ARGUMENTS",
                message = "Validation failed: ${classifiedCall.validationErrors.joinToString("; ")}"
            )
        }

        var attempt = 0
        var lastError: Exception? = null

        while (attempt <= maxRetries) {
            try {
                val output = toolRegistry.executeTool(classifiedCall.toolName, classifiedCall.arguments)
                return ToolResult.Success(
                    output = output,
                    metadata = classifiedCall.arguments + ("attempt" to (attempt + 1).toString())
                )
            } catch (e: SecurityException) {
                return ToolResult.PermissionRequired(
                    permission = classifiedCall.toolName,
                    rationale = e.message ?: "Permission denied for ${classifiedCall.toolName}"
                )
            } catch (e: Exception) {
                lastError = e
                attempt++
            }
        }

        return ToolResult.Error(
            code = "EXECUTION_RETRY_EXHAUSTED",
            message = "Failed to execute ${classifiedCall.toolName} after $maxRetries attempts: ${lastError?.localizedMessage}",
            cause = lastError
        )
    }
}
