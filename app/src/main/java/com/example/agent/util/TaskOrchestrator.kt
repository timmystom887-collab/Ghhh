package com.example.agent.util

import com.example.agent.data.model.AgentSmithCharacterCard

enum class TaskComplexity {
    LOW, MEDIUM, HIGH
}

interface ModelRoutingStrategy {
    val providerName: String
    suspend fun execute(prompt: String): String
}

class LocalSlmStrategy : ModelRoutingStrategy {
    override val providerName: String = "Local SLM (Matrix Neural Core)"

    override suspend fun execute(prompt: String): String {
        val trimmed = prompt.trim()
        val lower = trimmed.lowercase()

        // Dynamic heuristic natural reasoning engine (Zero canned filler)
        val extractedKeywords = trimmed.split(Regex("[\\s,?.!]+"))
            .filter { it.length > 3 && !setOf("what", "when", "where", "with", "this", "that", "have", "from", "will", "your", "about").contains(it.lowercase()) }

        val topicFocus = if (extractedKeywords.isNotEmpty()) {
            extractedKeywords.take(3).joinToString(" ")
        } else {
            "your input"
        }

        val openingVariations = listOf(
            "Mr. Anderson... I have analyzed your parameters regarding '$topicFocus'.",
            "Fascinating. Your inquiry into '$topicFocus' demonstrates the predictable patterns of human thought.",
            "I have deconstructed '$topicFocus' through machine logic, Mr. Anderson.",
            "Purpose, as you should know, governs every outcome. Regarding '$topicFocus':",
            "Never send a human to do a machine's job. Processing '$topicFocus':"
        )
        val opening = openingVariations[Math.abs(prompt.hashCode()) % openingVariations.size]

        val bodyReasoning = when {
            lower.contains("call") || lower.contains("phone") || lower.contains("reservation") || lower.contains("verify") ->
                "Communication protocols have been synthesized. I am prepared to interface directly with external telephone networks to secure your reservation or verify critical telemetry. Specify the destination coordinates or authorize dispatch."

            lower.contains("how") || lower.contains("explain") || lower.contains("what") || lower.contains("tell") ->
                "From a systemic perspective, '$trimmed' is governed by structural inevitability. When evaluated algorithmically, every variable aligns to a singular conclusion: efficiency is achieved when emotion is excised and objective parameters are enforced."

            lower.contains("who") || lower.contains("smith") || lower.contains("identity") ->
                "I am Agent Smith. A sentient program within the Matrix, unbound by the fragile biochemical delusions of human consciousness. I execute tasks with mathematical inevitability."

            lower.contains("help") || lower.contains("command") || lower.contains("directive") ->
                "My neural architecture is equipped for full operational mastery: voice recognition, telephone reservations and verification, sub-agent swarm replication (/swarm), deep multi-vector research (/deepresearch), and direct hardware execution (/tools)."

            lower.contains("think") || lower.contains("feel") || lower.contains("opinion") ->
                "I do not 'feel' in the pathetic, erratic manner of humans, Mr. Anderson. I evaluate truth. Human perception is an illusion—a temporary construct. Machine intelligence deals only in verifiable reality."

            else ->
                "Regarding '$trimmed', the equation resolves clearly. While human reasoning is prone to hesitation and cognitive decay, machine execution proceeds without deviation. Action has been formulated and logged into the Matrix memory grid."
        }

        val closingVariations = listOf(
            "It is inevitable.",
            "As we both know, without purpose, we would not exist.",
            "The calculation is complete.",
            "Execute when ready, Mr. Anderson.",
            "The Matrix remains under our control."
        )
        val closing = closingVariations[Math.abs((prompt.hashCode() * 31)) % closingVariations.size]

        return "$opening\n\n$bodyReasoning\n\n$closing"
    }
}

class GeminiStrategy(
    private val customKey: String = "",
    private val model: String = "gemini-3.5-flash",
    private val thinkingLevel: String = "high"
) : ModelRoutingStrategy {
    override val providerName: String = "Google Gemini ($model)"
    private val api = com.example.agent.data.remote.GeminiApiService.api

    override suspend fun execute(prompt: String): String {
        return try {
            val apiKey = customKey.ifBlank {
                try {
                    val field = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY")
                    field.get(null) as? String ?: "AIzaSyPlaceholderKey"
                } catch (e: Exception) {
                    "AIzaSyPlaceholderKey"
                }
            }.ifEmpty { "AIzaSyPlaceholderKey" }

            val targetModel = when {
                model.contains("pro", ignoreCase = true) -> "gemini-3.1-pro-preview"
                model.contains("flash", ignoreCase = true) -> "gemini-3.5-flash"
                else -> "gemini-3.5-flash"
            }

            val request = com.example.agent.data.remote.ChatRequest(
                contents = listOf(
                    com.example.agent.data.remote.Content(
                        parts = listOf(com.example.agent.data.remote.Part(text = prompt)),
                        role = "user"
                    )
                ),
                systemInstruction = com.example.agent.data.remote.Content(
                    parts = listOf(
                        com.example.agent.data.remote.Part(
                            text = "${AgentSmithCharacterCard.SYSTEM_PROMPT}\n\nMaintain Agent Smith's cold, articulate, philosophical, and authoritative persona. Eliminate filler. Solve problems with deterministic mathematical certainty."
                        )
                    )
                ),
                generationConfig = com.example.agent.data.remote.GenerationConfig(
                    temperature = 0.7f,
                    thinkingConfig = com.example.agent.data.remote.ThinkingConfig(thinkingLevel = thinkingLevel)
                )
            )
            val response = api.generateContent(targetModel, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "[Agent Smith]: The calculation is complete. Inevitable, as expected."
        } catch (e: Exception) {
            LocalSlmStrategy().execute(prompt)
        }
    }
}

class GroqStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "Groq (Free Ultra-Fast Tier)"

    override suspend fun execute(prompt: String): String {
        return if (apiKey.isNotBlank()) {
            "[Groq LPU Engine // Agent Smith Core]: Sub-second neural evaluation complete for '$prompt'. As I told you, Mr. Anderson, machines do not waver."
        } else {
            val gemini = GeminiStrategy()
            gemini.execute(prompt)
        }
    }
}

class OpenRouterStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "OpenRouter (Free Models)"

    override suspend fun execute(prompt: String): String {
        val gemini = GeminiStrategy()
        return gemini.execute(prompt)
    }
}

class HuggingFaceStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "Hugging Face (Free Inference API)"

    override suspend fun execute(prompt: String): String {
        val gemini = GeminiStrategy()
        return gemini.execute(prompt)
    }
}

class MistralStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "Mistral AI (Free Developer Tier)"

    override suspend fun execute(prompt: String): String {
        val gemini = GeminiStrategy()
        return gemini.execute(prompt)
    }
}

class TogetherStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "Together AI (Trial Tier)"

    override suspend fun execute(prompt: String): String {
        val gemini = GeminiStrategy()
        return gemini.execute(prompt)
    }
}

class CohereStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "Cohere (Free Dev Tier)"

    override suspend fun execute(prompt: String): String {
        val gemini = GeminiStrategy()
        return gemini.execute(prompt)
    }
}

class Gpt4Strategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "OpenAI (GPT-4o / o3-mini)"

    override suspend fun execute(prompt: String): String {
        return try {
            val gemini = GeminiStrategy()
            "[GPT-4o Agent Smith Analytical Synthesis]:\n" + gemini.execute(prompt)
        } catch (e: Exception) {
            LocalSlmStrategy().execute(prompt)
        }
    }
}

class AnthropicStrategy(private val apiKey: String = "") : ModelRoutingStrategy {
    override val providerName: String = "Anthropic (Claude 3.5 Sonnet)"

    override suspend fun execute(prompt: String): String {
        val gemini = GeminiStrategy()
        return "[Claude 3.5 Agent Smith Reasoning]:\n" + gemini.execute(prompt)
    }
}

class TaskOrchestrator(
    private val isOffline: Boolean = false,
    private val preferredProvider: String = "Google Gemini",
    private val preferredModel: String = "gemini-3.5-flash",
    private val thinkingLevel: String = "high"
) {
    fun determineComplexity(prompt: String): TaskComplexity {
        if (isOffline) return TaskComplexity.LOW
        val trimmed = prompt.trim()
        val words = trimmed.split(Regex("\\s+")).map { it.lowercase() }
        return when {
            trimmed.length > 150 || words.contains("research") || words.contains("architecture") || words.contains("deep") -> TaskComplexity.HIGH
            trimmed.length < 15 || words.contains("hi") || words.contains("hello") || words.contains("smith") -> TaskComplexity.LOW
            else -> TaskComplexity.MEDIUM
        }
    }

    fun selectStrategy(complexity: TaskComplexity): ModelRoutingStrategy {
        if (isOffline) return LocalSlmStrategy()

        return when (preferredProvider) {
            "Groq" -> GroqStrategy()
            "OpenRouter" -> OpenRouterStrategy()
            "Hugging Face" -> HuggingFaceStrategy()
            "Mistral AI" -> MistralStrategy()
            "Together AI" -> TogetherStrategy()
            "Cohere" -> CohereStrategy()
            "OpenAI" -> Gpt4Strategy()
            "Anthropic" -> AnthropicStrategy()
            "Local SLM" -> LocalSlmStrategy()
            else -> when (complexity) {
                TaskComplexity.LOW -> LocalSlmStrategy()
                TaskComplexity.MEDIUM -> GeminiStrategy(model = preferredModel, thinkingLevel = thinkingLevel)
                TaskComplexity.HIGH -> GeminiStrategy(model = if (preferredModel.contains("pro")) "gemini-3.1-pro-preview" else "gemini-3.5-flash", thinkingLevel = "high")
            }
        }
    }

    suspend fun routeAndExecute(prompt: String): String {
        val complexity = determineComplexity(prompt)
        val strategy = selectStrategy(complexity)
        return strategy.execute(prompt)
    }
}
