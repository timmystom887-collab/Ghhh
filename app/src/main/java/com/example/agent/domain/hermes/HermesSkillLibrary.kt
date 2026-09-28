package com.example.agent.domain.hermes

object HermesSkillLibrary {

    val BUNDLED_TOOLS: List<HermesToolDefinition> = listOf(
        HermesToolDefinition(
            name = "evaluate_math",
            description = "Evaluates arithmetic or algebraic expressions with 100% deterministic precision.",
            parameters = mapOf(
                "expression" to HermesParameterDefinition(
                    type = "string",
                    description = "Mathematical expression to compute, e.g. '144 * 12 / 2'"
                )
            )
        ),
        HermesToolDefinition(
            name = "set_alarm",
            description = "Schedules an exact Android system alarm or reminder.",
            parameters = mapOf(
                "hour" to HermesParameterDefinition(
                    type = "number",
                    description = "Hour in 24-hour format (0-23)"
                ),
                "minute" to HermesParameterDefinition(
                    type = "number",
                    description = "Minute (0-59)"
                ),
                "message" to HermesParameterDefinition(
                    type = "string",
                    description = "Alarm label / reminder text",
                    required = false
                )
            )
        ),
        HermesToolDefinition(
            name = "send_sms",
            description = "Transmits an encrypted SMS text payload to a recipient telephone number.",
            parameters = mapOf(
                "phone_number" to HermesParameterDefinition(
                    type = "string",
                    description = "Destination phone number with country code"
                ),
                "message" to HermesParameterDefinition(
                    type = "string",
                    description = "Message body content"
                )
            )
        ),
        HermesToolDefinition(
            name = "make_call",
            description = "Dispatches an autonomous telephony call mission or connects directly to a destination.",
            parameters = mapOf(
                "phone_number" to HermesParameterDefinition(
                    type = "string",
                    description = "Destination telephone number"
                ),
                "mission_type" to HermesParameterDefinition(
                    type = "string",
                    description = "Type of mission: 'RESERVATION', 'VERIFICATION', 'INQUIRY', or 'DIRECT'",
                    required = false
                )
            )
        ),
        HermesToolDefinition(
            name = "set_torch",
            description = "Toggles the device hardware flashlight illumination state.",
            parameters = mapOf(
                "enabled" to HermesParameterDefinition(
                    type = "boolean",
                    description = "True to illuminate, False to extinguish"
                )
            )
        ),
        HermesToolDefinition(
            name = "get_battery",
            description = "Polls hardware battery percentage, charge status, and power telemetry.",
            parameters = emptyMap()
        ),
        HermesToolDefinition(
            name = "system_diagnostics",
            description = "Returns CPU memory utilization, active thread count, and node status.",
            parameters = emptyMap()
        ),
        HermesToolDefinition(
            name = "search_knowledge",
            description = "Queries the Matrix Neural Knowledge Vault and SQLite memory records.",
            parameters = mapOf(
                "query" to HermesParameterDefinition(
                    type = "string",
                    description = "Search query string or keywords"
                )
            )
        ),
        HermesToolDefinition(
            name = "create_git_patch",
            description = "Generates a unified diff git patch for automated code refactoring or bug fixes.",
            parameters = mapOf(
                "target_file" to HermesParameterDefinition(
                    type = "string",
                    description = "File path to apply the patch to"
                ),
                "patch_content" to HermesParameterDefinition(
                    type = "string",
                    description = "Unified diff patch content"
                )
            )
        ),
        HermesToolDefinition(
            name = "compute_hash",
            description = "Calculates SHA-256 or MD5 cryptographic hashes for data integrity verification.",
            parameters = mapOf(
                "input_data" to HermesParameterDefinition(
                    type = "string",
                    description = "Text or binary string to hash"
                ),
                "algorithm" to HermesParameterDefinition(
                    type = "string",
                    description = "Algorithm: 'SHA-256' or 'MD5'",
                    required = false
                )
            )
        )
    )

    fun getToolByName(name: String): HermesToolDefinition? {
        return BUNDLED_TOOLS.find { it.name.equals(name, ignoreCase = true) }
    }
}
