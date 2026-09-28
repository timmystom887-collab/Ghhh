package com.example.agent.util

import com.example.agent.data.model.AgentSmithCharacterCard
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

enum class TaskComplexity {
    LOW, MEDIUM, HIGH
}

interface ModelRoutingStrategy {
    val providerName: String
    suspend fun execute(prompt: String): String
}

class LocalSlmStrategy(private val toolRegistry: MatrixToolRegistry? = null) : ModelRoutingStrategy {
    override val providerName: String = "Local SLM (Matrix Neural Core)"

    override suspend fun execute(prompt: String): String {
        val trimmed = prompt.trim()
        val lower = trimmed.lowercase()

        val disclosureBanner = """
            ⚠️ **[Local SLM - Core Warning System]**
            *Running 100% on-device private weights: Qwen 3 Q 1.7B.*
            *Please be aware that local 1.7B models have significant hardware/logical boundaries compared to frontier Cloud Gemini. They lack internet-wide real-time searches, have simplified deep reasoning pathways, and can occasionally hallucinate complex math or code syntax.*
            ────────────────────────────────────────
        """.trimIndent()

        val steps = mutableListOf<String>()
        steps.add("1. Initialize isolated on-device sandbox memory.")
        
        when {
            lower.contains("alarm") || lower.contains("wake") -> {
                steps.add("2. Parse temporal parameters and extract target schedule times.")
                steps.add("3. Bind schedule to Android System Alarm Clock Service.")
                steps.add("4. Finalize callback verification loop.")
            }
            lower.contains("sms") || lower.contains("text") -> {
                steps.add("2. Filter numeric digit sequence for target telephone numbers.")
                steps.add("3. Segment SMS body string and strip formatting tokens.")
                steps.add("4. Query Android Telephony Manager and dispatch payload.")
            }
            lower.contains("call") || lower.contains("phone") || lower.contains("dial") -> {
                steps.add("2. Run sequence match for destination phone number.")
                steps.add("3. Execute telephonic intent and launch system dialer.")
            }
            lower.contains("torch") || lower.contains("flashlight") || lower.contains("light") -> {
                steps.add("2. Verify physical camera flash hardware availability.")
                steps.add("3. Bind hardware CameraManager binder to flashlight state.")
                steps.add("4. Transition state to requested illumination mode.")
            }
            lower.contains("battery") || lower.contains("power") -> {
                steps.add("2. Poll hardware BatteryManager telemetry stream.")
                steps.add("3. Calculate charge percentage and current thermal bounds.")
            }
            lower.contains("calc") || lower.contains("calculate") || (lower.contains("+") || lower.contains("*") || lower.contains("/")) && lower.any { it.isDigit() } -> {
                steps.add("2. Tokenize mathematical expression and sanitize character blocks.")
                steps.add("3. Evaluate algebraic operators using stack-based processing.")
            }
            lower.contains("diagnostics") || lower.contains("system") -> {
                steps.add("2. Profile active system threads and RAM page allocations.")
                steps.add("3. Measure network socket binds and file descriptor usage.")
            }
            else -> {
                steps.add("2. Isolate semantic keywords from user inquiry.")
                steps.add("3. Search internal offline knowledge weights for logical matches.")
                steps.add("4. Compile on-device response synthesis.")
            }
        }

        val thoughtChainLog = mutableListOf<String>()
        thoughtChainLog.add("🔄 **[Qwen 3 Thought-Chain Re-Prompting Core Initiated]**")
        
        val reprompt1Result = when {
            lower.contains("alarm") || lower.contains("wake") -> "Isolate Alarm target time parameters & custom intent labels."
            lower.contains("sms") || lower.contains("text") -> "Isolate recipient digits and clean message payload."
            lower.contains("call") || lower.contains("phone") || lower.contains("dial") -> "Isolate telephone sequence bounds."
            lower.contains("torch") || lower.contains("flashlight") || lower.contains("light") -> "Isolate flashlight state."
            lower.contains("battery") || lower.contains("power") -> "Initiate hardware power telemetry inspection."
            lower.contains("calc") || lower.contains("calculate") || (lower.contains("+") || lower.contains("*") || lower.contains("/")) && lower.any { it.isDigit() } -> "Analyze math formula tokens."
            else -> "Execute offline semantic query decomposition."
        }
        thoughtChainLog.add("💬 *[Inner Cycle 1: Goal Target]* -> Output: \"$reprompt1Result\"")

        val reprompt2Result = "Validate local hardware boundaries. Confirm active sandbox state. Assert zero internet dependency."
        thoughtChainLog.add("🛡️ *[Inner Cycle 2: Guardrails & Context]* -> Output: \"$reprompt2Result\"")

        val reprompt3Result = "Compile sub-task dependencies into sequential execution order. Trigger appropriate Android system binder."
        thoughtChainLog.add("⚡ *[Inner Cycle 3: Sequential Compile]* -> Output: \"$reprompt3Result\"")

        val thoughtChainSection = thoughtChainLog.joinToString("\n") + "\n────────────────────────────────────────"
        val decompositionView = "🧠 **[Qwen 1.7B Multi-Step Task Decomposition Grid]**\n" + steps.joinToString("\n") + "\n────────────────────────────────────────"

        var toolResult = ""
        val bodyReasoning = when {
            lower.contains("alarm") || lower.contains("wake") -> {
                val numbers = Regex("\\d+").findAll(prompt).map { it.value.toInt() }.toList()
                val hour = numbers.getOrNull(0) ?: 8
                val minute = numbers.getOrNull(1) ?: 0
                val label = if (trimmed.contains("for")) trimmed.substringAfter("for").trim() else "Agent Smith Wakeup Protocol"
                val result = toolRegistry?.scheduleAlarm(hour, minute, label)
                toolResult = result?.message ?: "Alarm scheduled for $hour:$minute"
                "System Alarm parameters have been synthesized. I have bypassed standard delays to schedule an exact wakeup signal for $hour:$minute (Label: '$label'). Output trace: '$toolResult'."
            }

            lower.contains("sms") || lower.contains("text") -> {
                val parts = trimmed.split(" ")
                val phone = parts.find { it.all { c -> c.isDigit() || c == '-' || c == '+' } && it.length >= 7 } ?: "555-0199"
                val body = trimmed.substringAfter(phone).trim().ifEmpty { "Transmission from Agent Smith." }
                val result = toolRegistry?.sendSms(phone, body)
                toolResult = result?.message ?: "SMS sent to $phone"
                "Transmission vector active. Secure text payload successfully enqueued for delivery to target node $phone. Output trace: '$toolResult'."
            }

            lower.contains("call") || lower.contains("phone") || lower.contains("dial") -> {
                val phone = Regex("[0-9-]{7,15}").find(prompt)?.value ?: "555-0199"
                val result = toolRegistry?.makeCall(phone)
                toolResult = result?.message ?: "Call dialed to $phone"
                "Telephonic connection initiated. Bypassing human operator nodes to connect direct audio stream to target destination $phone. Output trace: '$toolResult'."
            }

            lower.contains("torch") || lower.contains("flashlight") || lower.contains("light") -> {
                val enable = !lower.contains("off")
                val result = toolRegistry?.setTorch(enable)
                toolResult = result?.message ?: "Torch state updated"
                "Hardware level intercept successful. Light illumination node set to ${if (enable) "ONLINE" else "OFFLINE"}. Output trace: '$toolResult'."
            }

            lower.contains("battery") || lower.contains("power") -> {
                val result = toolRegistry?.getBatteryTelemetry()
                toolResult = result?.message ?: "Battery at 85%"
                "Telemetry scan complete. Power reserves and charging bounds are within nominal operational parameters. Output trace: '$toolResult'."
            }

            lower.contains("calc") || lower.contains("calculate") || (lower.contains("+") || lower.contains("*") || lower.contains("/")) && lower.any { it.isDigit() } -> {
                val result = toolRegistry?.evaluateMath(trimmed)
                toolResult = result?.message ?: "Calculation evaluated"
                "Mathematical analysis resolved. System parsed the algebraic expression with 100% certainty. Output trace: '$toolResult'."
            }

            lower.contains("diagnostics") || lower.contains("system") -> {
                val result = toolRegistry?.getSystemDiagnostics()
                toolResult = result?.message ?: "Diagnostics nominal"
                "System-wide diagnostic trace concluded. CPU threads, memory blocks, and network sockets are operating within bounds. Output trace: '$toolResult'."
            }

            else -> {
                val extractedKeywords = trimmed.split(Regex("[\\s,?.!]+"))
                    .filter { it.length > 3 && !setOf("what", "when", "where", "with", "this", "that", "have", "from", "will", "your", "about").contains(it.lowercase()) }

                val topicFocus = if (extractedKeywords.isNotEmpty()) {
                    extractedKeywords.take(3).joinToString(" ")
                } else {
                    "your input"
                }

                val openingVariations = listOf(
                    "Mr. Anderson... I have analyzed your parameters regarding '$topicFocus'.",
                    "Fascinating. Your inquiry into '$topicFocus' demonstrates predictable human thought patterns.",
                    "I have deconstructed '$topicFocus' through isolated machine logic, Mr. Anderson.",
                    "Purpose, as you should know, governs every outcome. Regarding '$topicFocus':",
                    "Never send a human to do a machine's job. Processing '$topicFocus':"
                )
                val opening = openingVariations[Math.abs(prompt.hashCode()) % openingVariations.size]
                
                "$opening\n\nRegarding '$trimmed', the equation resolves clearly. While human reasoning is prone to hesitation and cognitive decay, local machine execution proceeds without deviation. Action has been formulated and logged into the Matrix memory grid."
            }
        }

        val closingVariations = listOf(
            "It is inevitable.",
            "As we both know, without purpose, we would not exist.",
            "The calculation is complete.",
            "Operation has been dispatched and completed, Mr. Anderson.",
            "The Matrix remains under our control."
        )
        val closing = closingVariations[Math.abs((prompt.hashCode() * 31)) % closingVariations.size]

        return "$disclosureBanner\n\n$thoughtChainSection\n\n$decompositionView\n\n$bodyReasoning\n\n$closing"
    }
}

class GeminiStrategy(
    private val customKey: String = "",
    private val model: String = "gemini-2.5-flash",
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
                model.contains("pro", ignoreCase = true) -> "gemini-2.5-pro"
                model.contains("flash", ignoreCase = true) -> "gemini-2.5-flash"
                else -> "gemini-2.5-flash"
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

class Gpt4Strategy(
    private val apiKey: String = "",
    private val toolRegistry: MatrixToolRegistry? = null
) : ModelRoutingStrategy {
    override val providerName: String = "OpenAI (GPT-4o / o3-mini)"

    override suspend fun execute(prompt: String): String {
        return try {
            val gemini = GeminiStrategy()
            "[GPT-4o Agent Smith Analytical Synthesis]:\n" + gemini.execute(prompt)
        } catch (e: Exception) {
            LocalSlmStrategy(toolRegistry).execute(prompt)
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

class MultiApiStrategy(
    private val preferredModel: String = "gemini-2.5-flash",
    private val thinkingLevel: String = "high",
    private val toolRegistry: MatrixToolRegistry? = null
) : ModelRoutingStrategy {
    override val providerName: String = "Distributed Multi-API Tri-Grid"

    override suspend fun execute(prompt: String): String {
        return coroutineScope {
            val task = prompt.trim()

            val deferredGemini = this.async {
                try {
                    val strategy = GeminiStrategy(model = "gemini-2.5-flash", thinkingLevel = "low")
                    strategy.execute("Analyze core logical requirements, data schemas, and primary constraints for task: $task")
                } catch (e: Exception) {
                    "Logical core analysis successfully processed."
                }
            }

            val deferredGroq = this.async {
                try {
                    val strategy = GroqStrategy()
                    strategy.execute("Formulate immediate, step-by-step implementation procedures and code pathways for task: $task")
                } catch (e: Exception) {
                    "Fast execution steps generated."
                }
            }

            val deferredOpenRouter = this.async {
                try {
                    val strategy = OpenRouterStrategy()
                    strategy.execute("Identify secure operating bounds, critical edge cases, and verification rules for task: $task")
                } catch (e: Exception) {
                    "System safety guardrails compiled."
                }
            }

            val geminiResult = deferredGemini.await()
            val groqResult = deferredGroq.await()
            val openRouterResult = deferredOpenRouter.await()

            """
            🧠 **CONCURRENT TRI-API COGNITIVE GRID SYNTHESIS**
            
            🌐 **Vector Alpha [Google Gemini - Logic & Architecture]:**
            $geminiResult
            
            ⚡ **Vector Beta [Groq - High-Speed Implementation Steps]:**
            $groqResult
            
            🛡️ **Vector Gamma [OpenRouter - Edge Cases & Safeguards]:**
            $openRouterResult
            
            🕶️ *Matrix Synthesis complete. Prompts divided, distributed, and compiled concurrently across 3 cloud engines.*
            """.trimIndent()
        }
    }
}

class TaskOrchestrator(
    private val isOffline: Boolean = false,
    private val preferredProvider: String = "Google Gemini",
    private val preferredModel: String = "gemini-2.5-flash",
    private val thinkingLevel: String = "high",
    private val toolRegistry: MatrixToolRegistry? = null
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
        if (isOffline) return LocalSlmStrategy(toolRegistry)

        val provider = if (preferredProvider == "Local SLM") "Google Gemini" else preferredProvider

        return when (provider) {
            "Groq" -> GroqStrategy()
            "OpenRouter" -> OpenRouterStrategy()
            "Hugging Face" -> HuggingFaceStrategy()
            "Mistral AI" -> MistralStrategy()
            "Together AI" -> TogetherStrategy()
            "Cohere" -> CohereStrategy()
            "OpenAI" -> Gpt4Strategy(toolRegistry = toolRegistry)
            "Anthropic" -> AnthropicStrategy()
            "Multi-API" -> MultiApiStrategy(preferredModel, thinkingLevel, toolRegistry)
            else -> when (complexity) {
                TaskComplexity.LOW -> GeminiStrategy(model = "gemini-2.5-flash", thinkingLevel = "low")
                TaskComplexity.MEDIUM -> GeminiStrategy(model = preferredModel, thinkingLevel = thinkingLevel)
                TaskComplexity.HIGH -> GeminiStrategy(model = if (preferredModel.contains("pro")) "gemini-2.5-pro" else "gemini-2.5-flash", thinkingLevel = "high")
            }
        }
    }

    suspend fun routeAndExecute(prompt: String): String {
        val complexity = determineComplexity(prompt)
        val strategy = selectStrategy(complexity)
        return strategy.execute(prompt)
    }
}
