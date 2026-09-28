package com.example.agent.domain.usecase

import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.repository.IAgentRepository
import com.example.agent.domain.model.LlmResult
import com.example.agent.util.PreThoughtEngine
import com.example.agent.util.ThinkingMethodEngine
import kotlinx.coroutines.flow.first

class SendMessageUseCase(
    private val repository: IAgentRepository,
    private val preThoughtEngine: PreThoughtEngine = PreThoughtEngine(),
    private val thinkingMethodEngine: ThinkingMethodEngine
) {
    suspend operator fun invoke(
        userText: String,
        activeModel: String = "gemini-2.5-flash",
        activeThinkingMethod: String = "FIRST_PRINCIPLES"
    ): Result<MessageEntity> {
        if (userText.isBlank()) {
            return Result.failure(IllegalArgumentException("Message content cannot be blank"))
        }

        // 1. Persist User Message
        val userMessage = MessageEntity(
            sender = "user",
            content = userText,
            timestamp = System.currentTimeMillis(),
            type = "text"
        )
        repository.insertMessage(userMessage)

        // 2. Perform Pre-Thought Analysis
        val preThoughtPlan = preThoughtEngine.analyze(userText)

        // 3. Construct System Prompt with Agent Identity & Pre-thought Context
        val charName = repository.preferencesManager.charName.first()
        val charPersonality = repository.preferencesManager.charPersonality.first()
        val systemInstruction = """
            You are $charName.
            Personality: $charPersonality
            Pre-Thought Protocol:
            - Goal: ${preThoughtPlan.goal}
            - Success Criteria: ${preThoughtPlan.successCriteria}
            - Plan Steps: ${preThoughtPlan.plan.joinToString("; ")}
            - Identified Risks: ${preThoughtPlan.risks.joinToString("; ")}
        """.trimIndent()

        // 4. Invoke LLM Provider Client
        val llmResult = repository.callGeminiWithResult(
            prompt = userText,
            model = activeModel,
            systemInstruction = systemInstruction
        )

        return when (llmResult) {
            is LlmResult.Success -> {
                val agentResponse = MessageEntity(
                    sender = "agent",
                    content = llmResult.text,
                    timestamp = System.currentTimeMillis(),
                    type = "text"
                )
                repository.insertMessage(agentResponse)
                repository.preferencesManager.addUsage(
                    tokens = (llmResult.promptTokens + llmResult.completionTokens).toDouble(),
                    cost = (llmResult.promptTokens + llmResult.completionTokens) * 0.000001
                )
                Result.success(agentResponse)
            }
            is LlmResult.Error -> {
                val errorMsg = MessageEntity(
                    sender = "agent",
                    content = "Operational Notice: ${llmResult.message}",
                    timestamp = System.currentTimeMillis(),
                    type = "text"
                )
                repository.insertMessage(errorMsg)
                Result.success(errorMsg)
            }
        }
    }
}
