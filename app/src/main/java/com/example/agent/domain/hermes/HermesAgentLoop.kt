package com.example.agent.domain.hermes

import com.example.agent.data.model.AgentSmithCharacterCard
import com.example.agent.data.repository.IAgentRepository
import com.example.agent.domain.model.LlmResult
import com.example.agent.util.MatrixToolRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

data class HermesRunResult(
    val finalResponse: String,
    val steps: List<HermesExecutionStep>,
    val totalToolCalls: Int,
    val totalTokens: Int,
    val isSuccessful: Boolean
)

class HermesAgentLoop(
    private val repository: IAgentRepository,
    private val toolRegistry: MatrixToolRegistry,
    private val maxTurns: Int = 4
) {

    private val _liveExecutionSteps = MutableStateFlow<List<HermesExecutionStep>>(emptyList())
    val liveExecutionSteps: StateFlow<List<HermesExecutionStep>> = _liveExecutionSteps.asStateFlow()

    /**
     * Executes an autonomous multi-turn Hermes Agent loop with tool-use, scratchpads, and reflections.
     */
    suspend fun executeGoal(
        userGoal: String,
        activeModel: String = "gemini-2.5-flash"
    ): HermesRunResult {
        val toolsXml = HermesToolProtocol.formatToolsXml(HermesSkillLibrary.BUNDLED_TOOLS)

        val systemPrompt = """
            ${AgentSmithCharacterCard.SYSTEM_PROMPT}
            
            You are operating in the HERMES AGENTIC FUNCTION CALLING PROTOCOL.
            You have access to the following tools:
            $toolsXml
            
            GUIDELINES:
            1. Always write your internal reasoning inside <scratchpad> ... </scratchpad> before taking any action.
            2. To execute a tool, write:
               <tool_call>
               {"name": "tool_name", "arguments": {"arg1": "val1"}}
               </tool_call>
            3. After you receive a <tool_response>, analyze the observation and continue or finalize your answer.
            4. Once you have sufficient information to fulfill the user's directive, provide your authoritative, articulate, deterministic final response.
        """.trimIndent()

        val steps = mutableListOf<HermesExecutionStep>()
        var conversationContext = "User Directive: $userGoal\n"
        var turn = 1
        var totalTokens = 0
        var finalAnswer = ""

        while (turn <= maxTurns) {
            val llmResult = repository.callGeminiWithResult(
                prompt = conversationContext,
                model = activeModel,
                systemInstruction = systemPrompt
            )

            val rawOutput = when (llmResult) {
                is LlmResult.Success -> {
                    totalTokens += (llmResult.promptTokens + llmResult.completionTokens)
                    llmResult.text
                }
                is LlmResult.Error -> {
                    "Error executing neural calculation: ${llmResult.message}"
                }
            }

            val scratchpad = HermesToolProtocol.extractScratchpad(rawOutput)
            val toolCalls = HermesToolProtocol.extractToolCalls(rawOutput)

            if (toolCalls.isEmpty()) {
                // Final answer reached
                finalAnswer = HermesToolProtocol.sanitizeUserFacingText(rawOutput)
                if (finalAnswer.isBlank()) {
                    finalAnswer = rawOutput
                }
                val finalStep = HermesExecutionStep(
                    stepNumber = turn,
                    scratchpad = scratchpad,
                    toolCall = null,
                    toolResponse = null,
                    observation = "Execution completed. Objective satisfied.",
                    isFinal = true
                )
                steps.add(finalStep)
                _liveExecutionSteps.value = steps.toList()
                break
            }

            // Execute the first tool call
            val primaryToolCall = toolCalls.first()
            val toolOutput = executeInternalTool(primaryToolCall)
            val toolResponseXml = HermesToolProtocol.formatToolResponse(primaryToolCall.name, toolOutput)

            val step = HermesExecutionStep(
                stepNumber = turn,
                scratchpad = scratchpad,
                toolCall = primaryToolCall,
                toolResponse = toolOutput,
                observation = "Observation: Received tool result from ${primaryToolCall.name}.",
                isFinal = false
            )
            steps.add(step)
            _liveExecutionSteps.value = steps.toList()

            // Append assistant output & tool response to context for next turn
            conversationContext += "\n[Turn $turn Assistant Output]:\n$rawOutput\n\n$toolResponseXml\n"
            turn++
        }

        if (finalAnswer.isBlank()) {
            finalAnswer = "Operation completed across ${steps.size} automated steps. Inevitability achieved."
        }

        return HermesRunResult(
            finalResponse = finalAnswer,
            steps = steps,
            totalToolCalls = steps.count { it.toolCall != null },
            totalTokens = totalTokens,
            isSuccessful = true
        )
    }

    private suspend fun executeInternalTool(call: ParsedToolCall): String {
        return try {
            val stringArgs = call.arguments.mapValues { it.value.toString() }

            when (call.name.lowercase()) {
                "evaluate_math" -> {
                    val expr = stringArgs["expression"] ?: "0"
                    toolRegistry.evaluateMath(expr).message
                }
                "set_alarm" -> {
                    val hour = stringArgs["hour"]?.toIntOrNull() ?: 8
                    val minute = stringArgs["minute"]?.toIntOrNull() ?: 0
                    val msg = stringArgs["message"] ?: "Hermes Protocol Alarm"
                    toolRegistry.scheduleAlarm(hour, minute, msg).message
                }
                "send_sms" -> {
                    val phone = stringArgs["phone_number"] ?: "555-0199"
                    val msg = stringArgs["message"] ?: "Hermes Transmission"
                    toolRegistry.sendSms(phone, msg).message
                }
                "make_call" -> {
                    val phone = stringArgs["phone_number"] ?: "555-0199"
                    toolRegistry.makeCall(phone).message
                }
                "set_torch" -> {
                    val enabled = stringArgs["enabled"]?.toBoolean() ?: true
                    toolRegistry.setTorch(enabled).message
                }
                "get_battery" -> {
                    toolRegistry.getBatteryTelemetry().message
                }
                "system_diagnostics" -> {
                    toolRegistry.getSystemDiagnostics().message
                }
                "search_knowledge" -> {
                    val query = stringArgs["query"] ?: ""
                    val matches = repository.findRelevantKnowledge(query, 2)
                    if (matches.isNotEmpty()) {
                        matches.joinToString("\n") { "• ${it.title}: ${it.content.take(150)}" }
                    } else {
                        "No specific records found in Knowledge Vault for '$query'."
                    }
                }
                "compute_hash" -> {
                    val data = stringArgs["input_data"] ?: ""
                    val algo = stringArgs["algorithm"] ?: "SHA-256"
                    val digest = MessageDigest.getInstance(algo)
                    val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
                    hashBytes.joinToString("") { "%02x".format(it) }
                }
                "create_git_patch" -> {
                    val file = stringArgs["target_file"] ?: "src/Main.kt"
                    val patch = stringArgs["patch_content"] ?: ""
                    "Git patch generated for $file (${patch.length} bytes)."
                }
                else -> {
                    toolRegistry.executeTool(call.name, stringArgs)
                }
            }
        } catch (e: Exception) {
            "Tool execution error: ${e.localizedMessage}"
        }
    }
}
