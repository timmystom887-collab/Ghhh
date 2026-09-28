package com.example.agent.domain.thinking

data class SubTask(
    val id: String,
    val description: String,
    val toolRequired: String?,
    val dependencies: List<String> = emptyList(),
    val priority: Int = 1,
    val estimatedDurationSeconds: Int = 5,
    var isCompleted: Boolean = false
)

data class DecompositionPlan(
    val rootGoal: String,
    val subTasks: List<SubTask>,
    val executionType: ExecutionType = ExecutionType.SEQUENTIAL,
    val criticalPath: List<String> = emptyList()
)

enum class ExecutionType {
    SEQUENTIAL,
    PARALLEL,
    CONDITIONAL_BRANCH
}

class TaskDecomposer {

    fun decompose(goal: String): DecompositionPlan {
        val trimmed = goal.trim()
        val lower = trimmed.lowercase()

        val subTasks = mutableListOf<SubTask>()

        when {
            lower.contains("then") || lower.contains(";") || lower.contains("and also") -> {
                val segments = when {
                    trimmed.contains(";") -> trimmed.split(";").map { it.trim() }
                    trimmed.contains(" and then ", ignoreCase = true) -> trimmed.split(Regex("(?i)\\s+and then\\s+")).map { it.trim() }
                    trimmed.contains(" then ", ignoreCase = true) -> trimmed.split(Regex("(?i)\\s+then\\s+")).map { it.trim() }
                    else -> trimmed.lines().map { it.replace(Regex("^\\d+\\.\\s*"), "").trim() }
                }.filter { it.isNotBlank() }

                var previousId: String? = null
                segments.forEachIndexed { index, seg ->
                    val id = "step_${index + 1}"
                    val tool = detectToolForSegment(seg)
                    val deps = if (previousId != null) listOf(previousId!!) else emptyList()
                    subTasks.add(
                        SubTask(
                            id = id,
                            description = seg,
                            toolRequired = tool,
                            dependencies = deps,
                            priority = index + 1
                        )
                    )
                    previousId = id
                }
            }

            lower.startsWith("call") || lower.contains("reservation") || lower.contains("book table") -> {
                subTasks.add(SubTask("step_1", "Extract destination contact and reservation parameters", "entity_extractor"))
                subTasks.add(SubTask("step_2", "Validate telephone network and user authorization", "security_validator", listOf("step_1")))
                subTasks.add(SubTask("step_3", "Execute telecom bridge and negotiate reservation", "make_call", listOf("step_2"), 2, 60))
                subTasks.add(SubTask("step_4", "Archive call outcome to local database and notify user", "archive_log", listOf("step_3")))
            }

            lower.startsWith("alarm") || lower.contains("wake me up") -> {
                subTasks.add(SubTask("step_1", "Parse target time parameters and calculate offset", "time_parser"))
                subTasks.add(SubTask("step_2", "Bind schedule to Android System AlarmClock service", "set_alarm", listOf("step_1")))
                subTasks.add(SubTask("step_3", "Confirm alarm registration with user", "messenger", listOf("step_2")))
            }

            lower.startsWith("sms") || lower.contains("text ") -> {
                subTasks.add(SubTask("step_1", "Isolate recipient phone number and sanitize body content", "entity_extractor"))
                subTasks.add(SubTask("step_2", "Verify telephony carrier permissions", "permission_checker", listOf("step_1")))
                subTasks.add(SubTask("step_3", "Transmit SMS payload via SmsManager", "send_sms", listOf("step_2")))
            }

            else -> {
                subTasks.add(SubTask("step_1", "Parse user inquiry and contextualize with neural memory", "context_assembler"))
                subTasks.add(SubTask("step_2", "Formulate deterministic response using active reasoning framework", "llm_reasoner", listOf("step_1")))
                subTasks.add(SubTask("step_3", "Self-critique and verify response safety", "critic_reflector", listOf("step_2")))
            }
        }

        return DecompositionPlan(
            rootGoal = goal,
            subTasks = subTasks,
            executionType = if (subTasks.size > 1) ExecutionType.SEQUENTIAL else ExecutionType.PARALLEL,
            criticalPath = subTasks.map { it.id }
        )
    }

    private fun detectToolForSegment(segment: String): String? {
        val lower = segment.lowercase()
        return when {
            lower.contains("alarm") || lower.contains("wake") -> "set_alarm"
            lower.contains("sms") || lower.contains("text") -> "send_sms"
            lower.contains("call") || lower.contains("phone") -> "make_call"
            lower.contains("torch") || lower.contains("flashlight") -> "set_torch"
            lower.contains("calc") || lower.contains("+") || lower.contains("*") -> "evaluate_math"
            lower.contains("battery") -> "get_battery"
            lower.contains("app") || lower.contains("open") -> "open_app"
            else -> null
        }
    }
}
