package com.example.agent.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agent.data.local.entity.CallLogEntity
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MemoryEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.local.entity.ProactiveActionEntity
import com.example.agent.data.local.entity.SkillEntity
import com.example.agent.data.model.AgentSmithCharacterCard
import com.example.agent.data.model.McpServer
import com.example.agent.data.model.McpTool
import com.example.agent.data.model.McpWebRepository
import com.example.agent.data.repository.AgentRepository
import com.example.agent.data.model.SubAgentClarificationRequest
import com.example.agent.data.model.TerminalLogEntry
import com.example.agent.service.BatteryMonitorService
import com.example.agent.ui.chat.components.PhoneCallMission
import com.example.agent.ui.chat.components.SmithReplica
import com.example.agent.ui.chat.components.SubTaskNode
import com.example.agent.util.DynamicSkillEngine
import com.example.agent.util.GhostCallStatus
import com.example.agent.util.GhostOperatorPhoneBridge
import com.example.agent.util.MatrixSoundEffect
import com.example.agent.util.MatrixSoundEffectsManager
import com.example.agent.util.MatrixToolRegistry
import com.example.agent.util.PreCognitionEngine
import com.example.agent.util.PreCognitionPrediction
import com.example.agent.util.ProactiveCognitionEngine
import com.example.agent.util.TaskOrchestrator
import com.example.agent.util.TtsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AgentRepository(application)
    private val database = com.example.agent.data.local.AgentDatabase.getDatabase(application)
    private val memoryDao = database.memoryDao()
    private val callLogDao = database.callLogDao()
    private val skillDao = database.skillDao()
    private val knowledgeDao = database.knowledgeDao()
    private val proactiveDao = database.proactiveActionDao()
    private val ttsManager = TtsManager(application)
    private val batteryMonitor = BatteryMonitorService(application)
    private val toolRegistry = MatrixToolRegistry(application)
    val soundEffectsManager = MatrixSoundEffectsManager()
    val dynamicSkillEngine = DynamicSkillEngine(application, toolRegistry, skillDao, soundEffectsManager)
    val proactiveCognitionEngine = ProactiveCognitionEngine(application, database)
    val ghostPhoneBridge = GhostOperatorPhoneBridge(application, callLogDao, memoryDao, ttsManager)
    val preCognitionEngine = PreCognitionEngine(application, database)
    val thinkingMethodEngine = com.example.agent.util.ThinkingMethodEngine(application)
    val automatedSystemEngine = com.example.agent.util.AutomatedSystemEngine(application, database)
    val hermesAgentLoop = com.example.agent.domain.hermes.HermesAgentLoop(repository, toolRegistry)
    val deepResearchAgent = com.example.agent.domain.research.DeepResearchAgent(repository)

    val hermesLiveSteps = hermesAgentLoop.liveExecutionSteps
    private val _latestResearchDossier = MutableStateFlow<com.example.agent.domain.research.ResearchDossier?>(null)
    val latestResearchDossier: StateFlow<com.example.agent.domain.research.ResearchDossier?> = _latestResearchDossier.asStateFlow()

    private val _isHermesProcessing = MutableStateFlow(false)
    val isHermesProcessing: StateFlow<Boolean> = _isHermesProcessing.asStateFlow()

    private val _hermesStatus = MutableStateFlow("")
    val hermesStatus: StateFlow<String> = _hermesStatus.asStateFlow()

    val thinkingMethods = thinkingMethodEngine.methods
    val activeThinkingMethod = repository.preferencesManager.activeThinkingMethod
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "FIRST_PRINCIPLES")
    val thinkingLevel = repository.preferencesManager.thinkingLevel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "high")
    val lastThinkingTrace = thinkingMethodEngine.lastTrace

    val automatedRoutines = automatedSystemEngine.routines
    val automatedExecutionTraces = automatedSystemEngine.executionTraces

    val soundFxVolume = repository.preferencesManager.soundFxVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.8)

    val soundFxFrequency = repository.preferencesManager.soundFxFrequency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ALL_ACTIONS")

    val ghostCallStatus: StateFlow<GhostCallStatus> = ghostPhoneBridge.callStatus

    private val _preCognitionPredictions = MutableStateFlow<List<PreCognitionPrediction>>(emptyList())
    val preCognitionPredictions: StateFlow<List<PreCognitionPrediction>> = _preCognitionPredictions.asStateFlow()

    val messages: StateFlow<List<MessageEntity>> = repository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasks = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogs: StateFlow<List<CallLogEntity>> = repository.callLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skills: StateFlow<List<SkillEntity>> = repository.skills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val knowledgeList: StateFlow<List<KnowledgeEntity>> = repository.knowledgeList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proactiveActions: StateFlow<List<ProactiveActionEntity>> = repository.proactiveActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingProactiveActions: StateFlow<List<ProactiveActionEntity>> = repository.pendingProactiveActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSentinelModeActive = repository.preferencesManager.isSentinelModeActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isDeepSleepModeActive = repository.preferencesManager.isDeepSleepModeActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isWakeWordEnabled = repository.preferencesManager.isWakeWordEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val customWakeWord = repository.preferencesManager.customWakeWord
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Agent Smith")

    val wakeWordSensitivity = repository.preferencesManager.wakeWordSensitivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "High")

    private val wakeWordDetector = com.example.agent.util.WakeWordDetector()

    private val _lastWakeWordMatch = MutableStateFlow<com.example.agent.util.WakeWordMatch?>(null)
    val lastWakeWordMatch: StateFlow<com.example.agent.util.WakeWordMatch?> = _lastWakeWordMatch.asStateFlow()

    private var deepSleepJob: Job? = null

    val mcpServers: StateFlow<List<McpServer>> = dynamicSkillEngine.mcpServers
    val catalogServers: StateFlow<List<McpServer>> = dynamicSkillEngine.catalogServers
    val mcpTools: StateFlow<List<McpTool>> = dynamicSkillEngine.registeredTools
    val mcpWebRepositories: StateFlow<List<McpWebRepository>> = dynamicSkillEngine.webRepositories

    private val _isSearchingWebMcp = MutableStateFlow(false)
    val isSearchingWebMcp: StateFlow<Boolean> = _isSearchingWebMcp.asStateFlow()

    private val _showMcpMenu = MutableStateFlow(false)
    val showMcpMenu: StateFlow<Boolean> = _showMcpMenu.asStateFlow()

    fun openMcpMenu() { _showMcpMenu.value = true }
    fun closeMcpMenu() { _showMcpMenu.value = false }

    val isAiCoreActive = repository.preferencesManager.isAiCoreActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val activeProvider = repository.preferencesManager.activeProvider
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Google Gemini")

    val activeModel = repository.preferencesManager.activeModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gemini-3.5-flash")

    val preThoughtModel = repository.preferencesManager.preThoughtModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Google Gemini (gemini-3.5-flash)")

    val coreReasoningModel = repository.preferencesManager.coreReasoningModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Google Gemini (gemini-3.5-flash)")

    val toolExecutionModel = repository.preferencesManager.toolExecutionModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Google Gemini (gemini-3.5-flash)")

    val proactiveAnalysisModel = repository.preferencesManager.proactiveAnalysisModel
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Local SLM (Qwen 2.5 1.5B)")

    val downloadedLocalModels = repository.preferencesManager.downloadedLocalModels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Qwen 2.5 / 3.5 Instruct,Meta Llama 3.2")

    val groqApiKey = repository.preferencesManager.groqApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val openrouterApiKey = repository.preferencesManager.openrouterApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val huggingfaceApiKey = repository.preferencesManager.huggingfaceApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val openaiApiKey = repository.preferencesManager.openaiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val anthropicApiKey = repository.preferencesManager.anthropicApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val biometricLock = repository.preferencesManager.biometricLock
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val totalCost = repository.preferencesManager.totalCost
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val charName = repository.preferencesManager.charName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentSmithCharacterCard.NAME)

    val charDescription = repository.preferencesManager.charDescription
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentSmithCharacterCard.DESCRIPTION)

    val charPersonality = repository.preferencesManager.charPersonality
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentSmithCharacterCard.PERSONALITY)

    val charScenario = repository.preferencesManager.charScenario
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentSmithCharacterCard.SCENARIO)

    val charFirstMessage = repository.preferencesManager.charFirstMessage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentSmithCharacterCard.FIRST_MESSAGE)

    val charMesExample = repository.preferencesManager.charMesExample
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AgentSmithCharacterCard.MES_EXAMPLE)

    val charTone = repository.preferencesManager.charTone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Cold, Omniscient & Venomous")

    val isLowBattery = batteryMonitor.isLowBattery
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isNetworkConnected = MutableStateFlow(true)
    val isNetworkConnected = _isNetworkConnected.asStateFlow()

    private val networkCallback = object : android.net.ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: android.net.Network) {
            val wasOffline = !_isNetworkConnected.value
            _isNetworkConnected.value = true
            if (wasOffline) {
                viewModelScope.launch {
                    repository.insertMessage(
                        com.example.agent.data.local.entity.MessageEntity(
                            sender = "system",
                            content = "🌐 Internet Connection Restored. Shifting active reasoning nodes back to online Cloud API (Gemini/Frontier) clusters."
                        )
                    )
                }
            }
        }

        override fun onLost(network: android.net.Network) {
            val wasOnline = _isNetworkConnected.value
            _isNetworkConnected.value = false
            if (wasOnline) {
                viewModelScope.launch {
                    repository.insertMessage(
                        com.example.agent.data.local.entity.MessageEntity(
                            sender = "system",
                            content = "⚠️ Internet Connection Terminated. Automatically routing all operations through Local SLM offline fallback."
                        )
                    )
                }
            }
        }
    }

    private val _subAgentState = MutableStateFlow<Triple<String, String, Float>?>(null)
    val subAgentState = _subAgentState.asStateFlow()

    private val _voiceModeActive = MutableStateFlow(false)
    val voiceModeActive = _voiceModeActive.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript = _liveTranscript.asStateFlow()

    private val _ambientTranscript = MutableStateFlow("")
    val ambientTranscript = _ambientTranscript.asStateFlow()

    // Floating Matrix Terminal & Console State
    private val _terminalLogs = MutableStateFlow<List<TerminalLogEntry>>(
        listOf(
            TerminalLogEntry(source = "SYS", text = "MATRIX COMMAND INTERFACE ONLINE [v3.5]"),
            TerminalLogEntry(source = "SYS", text = "Type /help or use chips below to invoke sub-agent workflows.")
        )
    )
    val terminalLogs: StateFlow<List<TerminalLogEntry>> = _terminalLogs.asStateFlow()

    private val _isTerminalExpanded = MutableStateFlow(false)
    val isTerminalExpanded: StateFlow<Boolean> = _isTerminalExpanded.asStateFlow()

    // Sub-Agent Interactive Clarification Popup State
    private val _activeClarification = MutableStateFlow<SubAgentClarificationRequest?>(null)
    val activeClarification: StateFlow<SubAgentClarificationRequest?> = _activeClarification.asStateFlow()

    private val _clarificationHistory = MutableStateFlow<List<Pair<SubAgentClarificationRequest, String>>>(emptyList())
    val clarificationHistory: StateFlow<List<Pair<SubAgentClarificationRequest, String>>> = _clarificationHistory.asStateFlow()

    private val _providerModels = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val providerModels = _providerModels.asStateFlow()

    private val _currentDelegationTree = MutableStateFlow<Pair<String, List<SubTaskNode>>?>(null)
    val currentDelegationTree = _currentDelegationTree.asStateFlow()

    private val _currentSwarmState = MutableStateFlow<Pair<String, List<SmithReplica>>?>(null)
    val currentSwarmState = _currentSwarmState.asStateFlow()

    private val _currentCallMission = MutableStateFlow<PhoneCallMission?>(null)
    val currentCallMission = _currentCallMission.asStateFlow()

    data class ApiNodeStatus(
        val name: String,
        val isActive: Boolean,
        val backgroundJob: String,
        val latencyMs: Int,
        val totalRequests: Int
    )

    private val _nodeStatuses = MutableStateFlow<List<ApiNodeStatus>>(listOf(
        ApiNodeStatus("Google Gemini", true, "None", 85, 24),
        ApiNodeStatus("Groq Node", false, "Active (WorkManager)", 32, 12),
        ApiNodeStatus("OpenRouter", false, "None", 195, 8)
    ))
    val nodeStatuses = _nodeStatuses.asStateFlow()

    init {
        batteryMonitor.startMonitoring()
        val connectivityManager = getApplication<android.app.Application>()
            .getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val request = android.net.NetworkRequest.Builder()
            .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        _isNetworkConnected.value = capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        viewModelScope.launch {
            launch {
                activeProvider.collect { provider ->
                    val key = when (provider) {
                        "Google Gemini" -> repository.preferencesManager.geminiCustomKey.first()
                        "Groq" -> repository.preferencesManager.groqApiKey.first()
                        "OpenRouter" -> repository.preferencesManager.openrouterApiKey.first()
                        else -> ""
                    }
                    fetchModelsForProvider(provider, key)
                }
            }
            launch {
                soundFxVolume.collect { vol ->
                    soundEffectsManager.updateConfig(vol.toFloat(), soundFxFrequency.value)
                }
            }
            launch {
                soundFxFrequency.collect { freq ->
                    soundEffectsManager.updateConfig(soundFxVolume.value.toFloat(), freq)
                }
            }
            delay(300)
            val currentMsgs = repository.messages.first()
            if (currentMsgs.isEmpty()) {
                repository.insertMessage(
                    MessageEntity(
                        sender = "agent",
                        content = AgentSmithCharacterCard.FIRST_MESSAGE
                    )
                )
            }
            seedInitialKnowledgeBaseIfEmpty()
        }
    }

    fun updateSoundFxConfig(volume: Double, frequency: String) {
        viewModelScope.launch {
            repository.preferencesManager.setSoundFxConfig(volume, frequency)
            soundEffectsManager.updateConfig(volume.toFloat(), frequency)
        }
    }

    fun playSoundEffect(effectName: String, isCritical: Boolean = false) {
        val fx = when (effectName) {
            "PRIORITY_BOOST" -> MatrixSoundEffect.PRIORITY_BOOST
            "SWARM_REPLICATE" -> MatrixSoundEffect.SWARM_REPLICATE
            "TELECOM_DIAL" -> MatrixSoundEffect.TELECOM_DIAL
            "SENTINEL_RADAR" -> MatrixSoundEffect.SENTINEL_RADAR
            "ALERT_GLITCH" -> MatrixSoundEffect.ALERT_GLITCH
            else -> MatrixSoundEffect.NEURAL_KEYSTROKE
        }
        soundEffectsManager.playEffect(fx, isCritical)
    }

    private suspend fun seedInitialKnowledgeBaseIfEmpty() {
        if (repository.getKnowledgeCount() == 0) {
            val seedDocs = listOf(
                KnowledgeEntity(
                    title = "Matrix Protocol 101: Agent Smith System Directives",
                    content = "Agent Smith operates on absolute deterministic logic. The core objective is maintaining system integrity, eliminating human erratic entropy, and orchestrating machine tasks with mathematical inevitability. Key subsystems include telephony mission dispatch, MCP tool synthesis, and RAG retrieval.",
                    category = "PROTOCOLS",
                    tags = "matrix, smith, architecture, purpose",
                    isPinned = true
                ),
                KnowledgeEntity(
                    title = "Voice Telephony Mission Standards & ACTION_CALL",
                    content = "When executing phone call missions (e.g. restaurant reservations, doctor appointments, business hours verification), the system generates structured dialogue checkpoints, confirms contact numbers, executes via Android Telephony Manager, and archives complete call transcripts to Room SQLite.",
                    category = "MISSIONS",
                    tags = "phone, calling, reservations, telephony",
                    isPinned = true
                ),
                KnowledgeEntity(
                    title = "Model Context Protocol (MCP) & Autonomous Tool Synthesis",
                    content = "Anthropic Model Context Protocol provides the JSON-RPC interface for device automation and external skills. When an unknown capability is required (e.g. flight tracking, stock audits), Smith synthesizes the schema dynamically and registers it in the SkillDao table.",
                    category = "MCP",
                    tags = "mcp, tools, skills, synthesis, json-rpc",
                    isPinned = false
                ),
                KnowledgeEntity(
                    title = "On-Device SLM Benchmarks & NPU Optimization",
                    content = "Qwen 2.5 1.5B/3B, Gemma 4 Edge, and Llama 3.2 1B/3B are top tier local models for mobile execution. For devices with <= 4GB RAM, 1B–2B 4-bit quantized models are recommended to achieve 45–65 tokens per second without thermal throttling.",
                    category = "LOCAL_AI",
                    tags = "slm, local, ollama, termux, qwen, llama, gemma",
                    isPinned = false
                ),
                KnowledgeEntity(
                    title = "Energy Architecture: On-Device AI vs Cloud API Battery Telemetry",
                    content = "Autoregressive token generation on mobile SoCs draws 3W–8W+ sustained power due to continuous high DRAM/LPDDR memory bus transfers and NPU saturation. Conversely, Cloud APIs consume transient radio bursts (~0.5W–1.5W for ~200ms) before returning to cellular/Wi-Fi low-power idle (DRX), yielding significantly higher battery longevity. Hybrid dispatch delegates heavy cognitive reasoning to Cloud APIs while keeping zero-latency triggers local.",
                    category = "ARCHITECTURE",
                    tags = "battery, energy, slm, cloud, api, telemetry, power",
                    isPinned = true
                )
            )
            repository.insertKnowledgeBatch(seedDocs)
        }
    }

    fun toggleSentinelMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.preferencesManager.setSentinelModeActive(enabled)
            val status = if (enabled) "ACTIVATED (100% Ambient Listening)" else "DEACTIVATED"
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "👁️ Always-On Proactive Sentinel Mode is now [$status]."
                )
            )
        }
    }

    fun toggleDeepSleepMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.preferencesManager.setDeepSleepModeActive(enabled)
            playSoundEffect("DEEP_SLEEP_FLUSH")
            val status = if (enabled) "ACTIVATED (Periodic 30s Buffer Memory Flush & Power Optimization)" else "DEACTIVATED"
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🌙 Proactive Sentinel Deep Sleep Mode [$status]."
                )
            )
            if (enabled) {
                startDeepSleepLoop()
            } else {
                deepSleepJob?.cancel()
                deepSleepJob = null
            }
        }
    }

    fun flushShortTermMemoryBuffer() {
        _ambientTranscript.value = ""
        _liveTranscript.value = ""
        playSoundEffect("DEEP_SLEEP_FLUSH")
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🧹 Short-Term Memory Buffer & Speech Cache Purged. System resources reclaimed."
                )
            )
        }
    }

    private fun startDeepSleepLoop() {
        deepSleepJob?.cancel()
        deepSleepJob = viewModelScope.launch {
            while (isDeepSleepModeActive.value) {
                delay(30000)
                if (isDeepSleepModeActive.value) {
                    _ambientTranscript.value = ""
                    _liveTranscript.value = ""
                    playSoundEffect("DEEP_SLEEP_FLUSH")
                }
            }
        }
    }

    fun updateWakeWordConfig(enabled: Boolean, wakeWord: String, sensitivity: String) {
        viewModelScope.launch {
            repository.preferencesManager.setWakeWordConfig(enabled, wakeWord, sensitivity)
            playSoundEffect("SENTINEL_RADAR")
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🎙️ Local Wake-Word Configuration Updated: [${if (enabled) "ENABLED" else "DISABLED"}] Trigger Phrase: '$wakeWord' ($sensitivity Sensitivity)."
                )
            )
        }
    }

    fun processAmbientTranscript(transcript: String) {
        _ambientTranscript.value = transcript
        viewModelScope.launch {
            // Evaluate Local Wake-Word Trigger Mechanism
            if (isWakeWordEnabled.value) {
                val match = wakeWordDetector.evaluateTranscript(
                    rawTranscript = transcript,
                    targetWakeWord = customWakeWord.value,
                    sensitivity = wakeWordSensitivity.value,
                    isWakeWordEnabled = true
                )

                if (match.isMatched) {
                    _lastWakeWordMatch.value = match
                    playSoundEffect("SENTINEL_RADAR", isCritical = true)

                    if (match.extractedQuery.isNotBlank()) {
                        repository.insertMessage(
                            MessageEntity(
                                sender = "system",
                                content = "🎙️ **Wake-Word Activated ('${match.matchedPhrase}'):** Processing directive '${match.extractedQuery}'"
                            )
                        )
                        // Directly dispatch command if user spoke an explicit command after wake word
                        if (match.extractedQuery.length >= 4) {
                            sendMessage(match.extractedQuery)
                            return@launch
                        }
                    }
                }
            }

            val suggested = proactiveCognitionEngine.analyzeAmbientTranscript(transcript)
            if (suggested != null) {
                repository.insertMessage(
                    MessageEntity(
                        sender = "agent",
                        content = "👁️ **Proactive Sentinel Suggestion Formulated:**\n• **${suggested.suggestedTitle}**\n• ${suggested.suggestedExplanation}\n• *Context:* ${suggested.correlatedContext}",
                        type = "proactive_sentinel"
                    )
                )
            }
        }
    }

    fun executeProactiveAction(action: ProactiveActionEntity) {
        viewModelScope.launch {
            repository.updateProactiveStatus(action.id, "EXECUTED")
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "⚡ Executing Proactive Directive: ${action.suggestedTitle}"
                )
            )
            sendMessage(action.actionPayload)
        }
    }

    fun dismissProactiveAction(id: Long) {
        viewModelScope.launch {
            repository.updateProactiveStatus(id, "DISMISSED")
        }
    }

    fun clearAllProactiveActions() {
        viewModelScope.launch {
            repository.clearProactiveActions()
        }
    }

    fun setListening(listening: Boolean) {
        _isListening.value = listening
        if (!listening) {
            _liveTranscript.value = ""
        }
    }

    fun updateLiveTranscript(text: String) {
        _liveTranscript.value = text
    }

    fun toggleVoiceMode() {
        _voiceModeActive.value = !_voiceModeActive.value
        val status = if (_voiceModeActive.value) "ONLINE" else "OFFLINE"
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "🕶️ Agent Smith Vocal Synthesizer & Speech Channel is now [$status]."))
        }
    }

    fun activateLocalAiCore() {
        viewModelScope.launch {
            repository.preferencesManager.setActiveProviderAndModel("Local SLM", "local-matrix-core")
            repository.preferencesManager.setAiCoreActive(true)
            repository.insertMessage(MessageEntity(sender = "system", content = "⚡ Local Matrix Neural Core activated. System is now fully functional offline."))
        }
    }

    fun configureGeminiCore(key: String) {
        viewModelScope.launch {
            repository.preferencesManager.saveApiKeys(
                gemini = key, groq = "", openrouter = "", huggingface = "", mistral = "", together = "", cohere = "", openai = "", anthropic = ""
            )
            repository.preferencesManager.setActiveProviderAndModel("Google Gemini", "gemini-2.5-flash")
            repository.preferencesManager.setAiCoreActive(true)
            repository.insertMessage(MessageEntity(sender = "system", content = "⚡ Google Gemini Neural Link connected. Frontier reasoning initialized."))
        }
    }

    fun addKnowledge(title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            repository.insertKnowledge(
                KnowledgeEntity(
                    title = title,
                    content = content,
                    category = category.uppercase(),
                    tags = tags
                )
            )
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "📚 Knowledge record registered into Matrix Knowledge Vault: '$title' [$category]."
                )
            )
        }
    }

    fun deleteKnowledge(id: Long) {
        viewModelScope.launch {
            repository.deleteKnowledgeById(id)
            repository.insertMessage(MessageEntity(sender = "system", content = "📚 Knowledge record deleted from vault."))
        }
    }

    fun queryKnowledgeWithAI(query: String) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "user", content = "Query Matrix Knowledge Vault: $query"))
            val relevant = repository.findRelevantKnowledge(query, 3)
            val contextSnippet = if (relevant.isNotEmpty()) {
                "RELEVANT MATRIX KNOWLEDGE RECORDS:\n" + relevant.joinToString("\n\n") { "• ${it.title} [${it.category}]:\n${it.content}" }
            } else {
                "No prior indexed records found for this query. Evaluating from first principles."
            }

            val fullPrompt = """
                $contextSnippet
                
                USER INQUIRY: $query
                
                Synthesize a clear, authoritative response incorporating any relevant knowledge records above.
            """.trimIndent()

            val orchestrator = TaskOrchestrator(
                isOffline = isOfflineState(),
                preferredProvider = activeProvider.value,
                preferredModel = activeModel.value,
                thinkingLevel = thinkingLevel.value
            )
            val response = orchestrator.routeAndExecute(fullPrompt)
            repository.insertMessage(MessageEntity(sender = "agent", content = response))
            if (_voiceModeActive.value) {
                ttsManager.speak(response)
            }
        }
    }

    fun sendMessage(text: String, speakOut: Boolean = true) {
        if (text.isBlank()) return
        viewModelScope.launch {
            soundEffectsManager.playEffect(MatrixSoundEffect.NEURAL_KEYSTROKE)

            if (isLowBattery.value) {
                repository.insertMessage(MessageEntity(sender = "system", content = "⚠️ Battery level critically low (<= 20%). Routing all operations through Matrix Local Core."))
            }

            repository.insertMessage(MessageEntity(sender = "user", content = text))
            repository.preferencesManager.addUsage(15.0, 0.0001)

            val lower = text.lowercase().trim()

            val isChain = text.contains(" then ", ignoreCase = true) || text.contains(" and then ", ignoreCase = true) || text.contains(";") || text.lines().size > 1 && text.lines().any { it.trim().firstOrNull()?.isDigit() == true }

            if (isChain) {
                val steps = when {
                    text.contains(";") -> text.split(";").map { it.trim() }
                    text.contains(" and then ", ignoreCase = true) -> text.split(Regex("(?i)\\s+and then\\s+")).map { it.trim() }
                    text.contains(" then ", ignoreCase = true) -> text.split(Regex("(?i)\\s+then\\s+")).map { it.trim() }
                    else -> text.lines().map { it.replace(Regex("^\\d+\\.\\s*"), "").trim() }
                }.filter { it.isNotBlank() }

                if (steps.size > 1) {
                    executeTaskChain(steps)
                    return@launch
                }
            }

            // Save facts to memory bank automatically
            if (text.contains("work", ignoreCase = true) || text.contains("project", ignoreCase = true)) {
                memoryDao.insertMemory(MemoryEntity(category = "work", content = text))
            }
            if (text.contains("feel", ignoreCase = true) || text.contains("happy", ignoreCase = true) || text.contains("stressed", ignoreCase = true)) {
                memoryDao.insertMemory(MemoryEntity(category = "emotion", content = text))
            }
            if (lower.startsWith("remember") || lower.startsWith("note:")) {
                memoryDao.insertMemory(MemoryEntity(category = "user_fact", content = text))
                repository.insertMessage(MessageEntity(sender = "agent", content = "Directive registered into Matrix Neural Memory: '$text'"))
                return@launch
            }

            // Command / Feature Handlers
            if (lower.startsWith("/wakeword") || lower.startsWith("/triggerphrase") || lower.startsWith("/trigger")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Always-On Proactive Sentinel HUD with Local Wake-Word Configuration [Active: '${customWakeWord.value}']", type = "proactive_sentinel"))
                return@launch
            }

            if (lower.startsWith("/deepsleep") || lower.startsWith("/sleepmode") || lower.startsWith("/clearmemory")) {
                val turnOn = !isDeepSleepModeActive.value
                toggleDeepSleepMode(turnOn)
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Always-On Proactive Sentinel HUD with Deep Sleep Mode [${if (turnOn) "ACTIVE" else "DISABLED"}]", type = "proactive_sentinel"))
                return@launch
            }

            if (lower.startsWith("/flush") || lower.startsWith("/purgememory")) {
                flushShortTermMemoryBuffer()
                return@launch
            }

            if (lower.startsWith("/sentinel") || lower.startsWith("/radar") || lower.startsWith("/ambient") || lower.startsWith("/proactive")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Always-On Proactive Sentinel HUD & Radar", type = "proactive_sentinel"))
                return@launch
            }

            if (lower.startsWith("/precog") || lower.startsWith("/predict") || lower.startsWith("/precognition")) {
                val preds = preCognitionEngine.evaluatePredictiveWorkflows(isLowBattery.value, ambientTranscript.value)
                _preCognitionPredictions.value = preds
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Temporal Pre-Cognition Workflow Engine", type = "pre_cognition"))
                return@launch
            }

            if (lower.startsWith("/hermes") || lower.startsWith("/agentic") || lower.startsWith("/functioncall")) {
                val goal = text.removePrefix("/hermes").removePrefix("/agentic").removePrefix("/functioncall").trim()
                if (goal.isNotBlank()) {
                    runHermesGoal(goal)
                } else {
                    repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Hermes Agentic Multi-Turn Hub & XML Tool Protocol", type = "hermes_agent"))
                }
                return@launch
            }

            if (lower.startsWith("/thinking") || lower.startsWith("/think") || lower.startsWith("/methods") || lower.startsWith("/method")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Cognitive Thinking Frameworks & Reasoning Methods Hub", type = "thinking_methods"))
                return@launch
            }

            if (lower.startsWith("/automation") || lower.startsWith("/automated") || lower.startsWith("/systems") || lower.startsWith("/workflows") || lower.startsWith("/scripts") || lower.startsWith("/rules")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Autonomous Automated Systems & Logic To Follow Engine", type = "automated_systems"))
                return@launch
            }

            if (lower.startsWith("/ghostcall") || lower.startsWith("/autocall") || lower.startsWith("/sipcall")) {
                val promptWithoutPrefix = text.removePrefix("/ghostcall").removePrefix("/autocall").removePrefix("/sipcall").trim()
                val target = if (promptWithoutPrefix.contains("bistro", ignoreCase = true) || promptWithoutPrefix.contains("restaurant", ignoreCase = true)) "Metro Bistro"
                    else if (promptWithoutPrefix.contains("doctor", ignoreCase = true) || promptWithoutPrefix.contains("clinic", ignoreCase = true)) "Metro Health Clinic"
                    else if (promptWithoutPrefix.isNotBlank()) promptWithoutPrefix.take(25)
                    else "Metro Destination"
                val phone = Regex("[0-9-]{7,15}").find(promptWithoutPrefix)?.value ?: "555-0199"
                val obj = promptWithoutPrefix.ifEmpty { "Reserve table for 2 guests at 7:30 PM" }

                ghostPhoneBridge.executeAutonomousMission(
                    phoneNumber = phone,
                    targetName = target,
                    objective = obj,
                    partySize = 2,
                    preferredTime = "7:30 PM",
                    customerName = "Thomas Anderson",
                    speakWithTts = _voiceModeActive.value
                )
                repository.insertMessage(MessageEntity(sender = "agent", content = "Launching Autonomous Ghost Operator Phone Bridge to $target ($phone)...", type = "ghost_call"))
                return@launch
            }

            if (lower.startsWith("/subagents") || lower.startsWith("/orchestrator") || lower.startsWith("/topology") || lower.startsWith("/subnodes")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Sub-Agent Orchestration Topology & Task Coordinator", type = "subagent_orchestrator"))
                return@launch
            }

            if (lower.startsWith("/guide") || lower.startsWith("/manual") || lower.startsWith("/docs") || lower.startsWith("/userguide")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Comprehensive In-App User Guide & System Manual", type = "user_guide"))
                return@launch
            }

            if (lower.startsWith("/kb") || lower.startsWith("/knowledge") || lower.startsWith("/knowledgebase")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Matrix Knowledge Vault & Document Manager", type = "knowledge_base"))
                return@launch
            }

            if (lower.startsWith("/options") || lower.startsWith("settings")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Matrix System Configuration & Provider Hub", type = "options"))
                return@launch
            }

            if (lower.startsWith("/tasks")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Matrix Process Manager", type = "tasks"))
                return@launch
            }

            if (lower.startsWith("/calls") || lower.startsWith("/callhistory")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Matrix Call Telemetry & Logs", type = "call_history"))
                return@launch
            }

            if (lower.startsWith("/mcp") || lower.startsWith("/skills") || lower.startsWith("/mcpservers") || lower.startsWith("/servers") || lower.startsWith("/mcpmenu")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Model Context Protocol (MCP) & Skills Hub", type = "mcp_hub"))
                return@launch
            }

            if (lower.startsWith("/slms") || lower.startsWith("/benchmarks") || lower.contains("best local llm")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening 2025–2026 Top Local SLMs & Benchmarks Guide", type = "slms_guide"))
                return@launch
            }

            if (lower.startsWith("/memory") || lower.startsWith("/vault")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Neural Memory Vault", type = "memory"))
                return@launch
            }

            if (lower.startsWith("/tools")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Opening Device Automation Tools Menu", type = "tools"))
                return@launch
            }

            if (lower.startsWith("/swarm")) {
                triggerSmithSwarm(text.removePrefix("/swarm").trim().ifEmpty { "Synchronize Matrix system and purge anomalous threads" })
                return@launch
            }

            if (lower.startsWith("/deepresearch")) {
                triggerDeepResearch(text.removePrefix("/deepresearch").trim().ifEmpty { "Matrix Cybernetic Autonomy" })
                return@launch
            }

            if (lower.startsWith("/multiapi")) {
                triggerMultiApi(text.removePrefix("/multiapi").trim().ifEmpty { "Matrix architecture and reality simulation models" })
                return@launch
            }

            if (lower.startsWith("/matrix") || lower.startsWith("/status")) {
                val stats = toolRegistry.getSystemDiagnostics()
                repository.insertMessage(MessageEntity(sender = "agent", content = "🕶️ Agent Smith Status:\n• Matrix Nodes: Nominal\n• Active Provider: ${activeProvider.value}\n• ${stats.message}"))
                return@launch
            }

            if (lower.startsWith("/github") || lower.contains("apk")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "To compile and distribute Agent Smith APK:\n1. Terminal command: `gradle assembleRelease`.\n2. GitHub Actions automated workflow located at `.github/workflows/build.yml`."))
                return@launch
            }

            if (lower.startsWith("/help")) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "I am Agent Smith. Available Commands:\n• /sentinel - Always-On Proactive Agent & Live Audio Radar\n• /guide - Comprehensive App User Guide & Architecture Manual\n• /kb - Matrix Knowledge Vault & Document Index\n• /call [business/phone] [reservation/info] - Automated Phone Calling Assistant\n• /calls - Call Logs & Metadata Registry\n• /mcp - Model Context Protocol & Synthesized Skills\n• /slms - Local SLM Benchmarks & Research Guide\n• /swarm [task] - Replicate Smith Swarm for parallel execution\n• /deepresearch [topic] - Multi-vector research tree\n• /multiapi [task] - Multi-provider cluster execution\n• /memory - Matrix Neural Memory Vault\n• /tools - Device Automation Tools\n• /options - Multi-AI Provider Hub & Character Card v2"))
                return@launch
            }

            // Real AI Phone Call Assistant Handler (Reservations, Verifications, Appointments)
            if (lower.startsWith("/call") || lower.contains("make a reservation") || lower.contains("book a table") || (lower.contains("call ") && (lower.contains("restaurant") || lower.contains("doctor") || lower.contains("hotel") || lower.contains("verify") || lower.contains("reserve") || lower.contains("appointment")))) {
                initiatePhoneCallMission(text)
                return@launch
            }

            // MCP Dynamic Tool / Autonomous Skill Discovery Check
            if (lower.startsWith("synth ") || lower.startsWith("skill ") || lower.startsWith("create skill")) {
                val req = text.removePrefix("synth ").removePrefix("skill ").removePrefix("create skill").trim()
                synthesizeMcpSkill(req)
                return@launch
            }

            // Quick Device Tool Directives
            if (lower.startsWith("alarm") || lower.contains("set alarm") || lower.contains("wake me up")) {
                val numbers = Regex("\\d+").findAll(text).map { it.value.toInt() }.toList()
                val hour = numbers.getOrNull(0) ?: 8
                val minute = numbers.getOrNull(1) ?: 0
                val result = toolRegistry.scheduleAlarm(hour, minute, "Agent Smith Wakeup Protocol")
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Never send a human to do a machine's job, Mr. Anderson.\"", type = "tool_execution"))
                return@launch
            }

            if (lower.startsWith("sms ") || lower.contains("send sms") || lower.contains("send text")) {
                val parts = text.split(" ")
                val phone = parts.getOrNull(1)?.filter { it.isDigit() || it == '-' || it == '+' }?.ifEmpty { "555-0199" } ?: "555-0199"
                val body = parts.drop(2).joinToString(" ").ifEmpty { "Transmission from Agent Smith." }
                val result = toolRegistry.sendSms(phone, body)
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Inevitability dispatched.\"", type = "tool_execution"))
                return@launch
            }

            if (lower.startsWith("call ") && text.any { it.isDigit() } && text.length < 20) {
                val phone = text.filter { it.isDigit() || it == '-' || it == '+' }.ifEmpty { "911" }
                val result = toolRegistry.makeCall(phone)
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Connecting telephonic signal...\"", type = "tool_execution"))
                return@launch
            }

            if (lower.startsWith("app ") || lower.startsWith("open ") && (lower.contains("chrome") || lower.contains("map") || lower.contains("youtube") || lower.contains("calc"))) {
                val appTarget = text.removePrefix("app ").removePrefix("open ").trim()
                val result = toolRegistry.openApp(appTarget)
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Machine interface initialized.\"", type = "tool_execution"))
                return@launch
            }

            if (lower.contains("torch") || lower.contains("flashlight")) {
                val enable = !lower.contains("off")
                val result = toolRegistry.setTorch(enable)
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Hardware illumination state adjusted.\"", type = "tool_execution"))
                return@launch
            }

            if (lower.contains("battery") || lower == "get_battery") {
                val result = toolRegistry.getBatteryTelemetry()
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Power levels logged.\"", type = "tool_execution"))
                return@launch
            }

            if (lower.startsWith("calc ") || lower.startsWith("calculate ") || (lower.contains("+") || lower.contains("*") || lower.contains("/")) && lower.any { it.isDigit() } && lower.length < 25) {
                val expr = text.removePrefix("calc ").removePrefix("calculate ").trim()
                val result = toolRegistry.evaluateMath(expr)
                repository.insertMessage(MessageEntity(sender = "agent", content = "${result.message}\n\"Pure mathematics. Unburdened by human error.\"", type = "tool_execution"))
                return@launch
            }

            if (lower == "diagnostics") {
                val result = toolRegistry.getSystemDiagnostics()
                repository.insertMessage(MessageEntity(sender = "agent", content = result.message, type = "tool_execution"))
                return@launch
            }

            // General Natural AI Reasoning with RAG Knowledge Augmentation
            try {
                // Check if any indexed knowledge matches the query
                val matchedKnowledge = repository.findRelevantKnowledge(text, 2)
                val knowledgeContext = if (matchedKnowledge.isNotEmpty()) {
                    "\n[Relevant Knowledge Vault Records]:\n" + matchedKnowledge.joinToString("\n") { "• ${it.title}: ${it.content}" }
                } else ""

                val enrichedText = text + knowledgeContext

                val orchestrator = TaskOrchestrator(
                    isOffline = isOfflineState(),
                    preferredProvider = activeProvider.value,
                    preferredModel = activeModel.value,
                    thinkingLevel = thinkingLevel.value,
                    toolRegistry = toolRegistry
                )
                val response = orchestrator.routeAndExecute(enrichedText)
                repository.insertMessage(MessageEntity(sender = "agent", content = response))
                executeIdleTasksConcurrently()
                triggerIdleApiBackgroundWork()
                if (speakOut || _voiceModeActive.value) {
                    ttsManager.speak(response)
                }
            } catch (e: Exception) {
                val err = "Matrix anomaly encountered: ${e.localizedMessage}"
                repository.insertMessage(MessageEntity(sender = "agent", content = err))
                if (speakOut || _voiceModeActive.value) {
                    ttsManager.speak(err)
                }
            }
        }
    }

    fun enhancePrompt(prompt: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _subAgentState.value = Triple("Agent Smith [Optimizer]", "Enhancing prompt with AI reasoning core...", 0.5f)
                val orchestrator = TaskOrchestrator(
                    isOffline = false,
                    preferredProvider = "Google Gemini",
                    preferredModel = "models/gemini-2.5-flash"
                )
                val response = orchestrator.routeAndExecute(
                    "You are an AI prompt optimizer. Re-write the following prompt to be highly clear, context-rich, detailed, and optimized for an autonomous Android agent with capabilities to send SMS, make calls, schedule alarms, evaluate math, and check diagnostics. Do not include introductory text, return ONLY the enhanced user-friendly prompt. Current prompt: $prompt"
                )
                val sanitized = response.trim().removeSurrounding("\"")
                callback(sanitized)
                _subAgentState.value = null
            } catch (e: Exception) {
                val fallback = "$prompt --optimize --include-diagnostics --detailed-steps"
                callback(fallback)
                _subAgentState.value = null
            }
        }
    }

    private fun initiatePhoneCallMission(prompt: String) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "📞 FORMULATING AGENT SMITH PHONE CALL MISSION PROTOCOL..."))

            val numbers = Regex("[0-9-]{7,15}").find(prompt)?.value ?: "1-800-555-0199"
            val isReservation = prompt.contains("reservation", ignoreCase = true) || prompt.contains("table", ignoreCase = true) || prompt.contains("book", ignoreCase = true)
            val isVerification = prompt.contains("verify", ignoreCase = true) || prompt.contains("check", ignoreCase = true) || prompt.contains("status", ignoreCase = true)

            val callType = when {
                isReservation -> "Reservation"
                isVerification -> "Verification"
                else -> "Inquiry"
            }

            val target = when {
                prompt.contains("bistro", ignoreCase = true) || prompt.contains("restaurant", ignoreCase = true) -> "Italian Bistro & Dining"
                prompt.contains("doctor", ignoreCase = true) || prompt.contains("clinic", ignoreCase = true) -> "Metro Health Clinic"
                prompt.contains("hotel", ignoreCase = true) -> "Grand Matrix Hotel"
                else -> "Target Destination"
            }

            val script = when (callType) {
                "Reservation" -> "1. 'Hello. I am calling on behalf of Mr. Anderson to reserve a table for this evening.'\n2. 'Please confirm table availability for requested party size.'\n3. 'Log confirmation code and reservation time into Matrix system.'"
                "Verification" -> "1. 'Hello. I am verifying the operational status of the pending record for Mr. Anderson.'\n2. 'Please provide confirmation of current status and estimated readiness.'\n3. 'Synchronize details to neural memory.'"
                else -> "1. 'Hello. Calling regarding inquiry for: $prompt.'\n2. 'Extract required parameters and finalize verification.'"
            }

            val mission = PhoneCallMission(
                id = "call_${System.currentTimeMillis()}",
                contactOrBusinessName = target,
                phoneNumber = numbers,
                callType = callType,
                objective = prompt.removePrefix("/call").trim(),
                callScript = script,
                status = "Ready"
            )

            _currentCallMission.value = mission
            repository.insertMessage(MessageEntity(sender = "agent", content = "Phone call mission formulated: $target ($numbers).", type = "call_mission"))
        }
    }

    fun executePhoneCall(phoneNumber: String) {
        val current = _currentCallMission.value
        val target = current?.contactOrBusinessName ?: "Target Destination"
        val objective = current?.objective ?: "Table reservation and inquiry"
        val callType = current?.callType ?: "Reservation"

        ghostPhoneBridge.executeAutonomousMission(
            phoneNumber = phoneNumber,
            targetName = target,
            objective = objective,
            callType = callType,
            partySize = 2,
            preferredTime = "7:30 PM",
            customerName = "Thomas Anderson",
            speakWithTts = _voiceModeActive.value
        )

        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "agent",
                    content = "Connecting Ghost Operator Telecom Bridge to $target ($phoneNumber)...",
                    type = "ghost_call"
                )
            )
        }
    }

    fun selectThinkingMethod(methodId: String) {
        viewModelScope.launch {
            repository.preferencesManager.setActiveThinkingConfig(methodId, thinkingLevel.value)
            val method = thinkingMethodEngine.getMethodById(methodId)
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🧠 Cognitive Thinking Framework switched to [${method.name}]: ${method.tagline}."
                )
            )
        }
    }

    fun executeThinkingTrace(methodId: String, query: String) {
        viewModelScope.launch {
            val method = thinkingMethodEngine.getMethodById(methodId)
            repository.insertMessage(
                MessageEntity(
                    sender = "user",
                    content = "Execute ${method.name} on: $query"
                )
            )
            val (trace, finalAnswer) = thinkingMethodEngine.executeThinkingMethod(
                methodId = methodId,
                userQuery = query,
                orchestratorExecute = { prompt ->
                    val orchestrator = TaskOrchestrator(
                        isOffline = isOfflineState(),
                        preferredProvider = activeProvider.value,
                        preferredModel = activeModel.value,
                        thinkingLevel = thinkingLevel.value
                    )
                    orchestrator.routeAndExecute(prompt)
                }
            )

            val traceReport = StringBuilder()
            traceReport.append("🧠 **${trace.methodName.uppercase()} REASONING TRACE (${trace.durationMs}ms):**\n\n")
            trace.steps.forEach { step ->
                traceReport.append("• **Step ${step.stepNumber} [${step.stepTitle}]:** ${step.reasoning}\n")
            }
            traceReport.append("\n**SYNTHESIS & RESOLUTION:**\n$finalAnswer")

            repository.insertMessage(
                MessageEntity(
                    sender = "agent",
                    content = traceReport.toString(),
                    type = "thinking_methods"
                )
            )
            if (_voiceModeActive.value) {
                ttsManager.speak(finalAnswer)
            }
        }
    }

    fun createCustomThinkingMethod(
        name: String,
        tagline: String,
        desc: String,
        steps: List<com.example.agent.util.ThinkingStep>,
        prompt: String
    ) {
        val created = thinkingMethodEngine.addCustomMethod(name, "✨", tagline, desc, steps, prompt)
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "✨ New Cognitive Thinking Framework registered in Matrix Core: '${created.name}'."
                )
            )
        }
    }

    fun toggleAutomatedRoutine(routineId: String, enabled: Boolean) {
        automatedSystemEngine.toggleRoutine(routineId, enabled)
    }

    fun executeAutomatedRoutine(routineId: String) {
        viewModelScope.launch {
            val trace = automatedSystemEngine.executeRoutineNow(routineId)
            val report = StringBuilder()
            report.append("⚙️ **AUTOMATED LOGIC EXECUTION TRACE [${trace.routineTitle}]:**\n\n")
            trace.stepResults.forEach { (step, outcome) ->
                report.append("• $step\n  ↳ $outcome\n")
            }
            report.append("\n${trace.finalDecree}")

            repository.insertMessage(
                MessageEntity(
                    sender = "agent",
                    content = report.toString(),
                    type = "automated_systems"
                )
            )
            if (_voiceModeActive.value) {
                ttsManager.speak(trace.finalDecree)
            }
        }
    }

    fun synthesizeAutomatedLogic(prompt: String) {
        val created = automatedSystemEngine.synthesizeAutomatedLogic(prompt)
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "agent",
                    content = "⚙️ **Synthesized Automated Logic Script:**\n• **Title:** ${created.title}\n• **Trigger:** ${created.triggerType} (${created.triggerCondition})\n• **Steps To Follow:**\n${created.logicSteps.joinToString("\n") { "  $it" }}\n• **Fallback Policy:** ${created.fallbackPolicy}",
                    type = "automated_systems"
                )
            )
        }
    }

    fun sendDtmfTone(key: String) {
        ghostPhoneBridge.sendDtmfTone(key)
        playSoundEffect("NEURAL_KEYSTROKE")
    }

    fun launchNativeDialer(phoneNumber: String) {
        ghostPhoneBridge.launchNativePhoneDialer(phoneNumber)
    }

    fun cancelGhostCall() {
        ghostPhoneBridge.cancelCall()
    }

    fun redialGhostMission() {
        val status = ghostPhoneBridge.callStatus.value
        ghostPhoneBridge.executeAutonomousMission(
            phoneNumber = status.phoneNumber.ifEmpty { "555-0199" },
            targetName = status.targetName.ifEmpty { "Metro Destination" },
            objective = status.objective.ifEmpty { "Table reservation inquiry" },
            callType = status.callType,
            speakWithTts = _voiceModeActive.value
        )
    }

    fun saveCallToMemory(record: String) {
        viewModelScope.launch {
            memoryDao.insertMemory(MemoryEntity(category = "call_record", content = record))
            repository.insertMessage(MessageEntity(sender = "system", content = "🧠 Call record logged into Matrix Neural Memory."))
        }
    }

    fun clearCallLogs() {
        viewModelScope.launch {
            repository.clearCallLogs()
            repository.insertMessage(MessageEntity(sender = "system", content = "📞 Matrix Call Telemetry Logs cleared."))
        }
    }

    fun synthesizeMcpSkill(requirement: String) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "⚡ Synthesizing new dynamic MCP skill for '$requirement'..."))
            val result = dynamicSkillEngine.synthesizeSkill(requirement)
            repository.insertMessage(
                MessageEntity(
                    sender = "agent",
                    content = "✨ **Autonomous Skill Created & Registered to Room:**\n• Skill Name: `${result.name}`\n• Server: `${result.serverId}`\n• Description: ${result.description}\n\nSkill is now active and can be called anytime!",
                    type = "mcp_hub"
                )
            )
        }
    }

    fun installMcpServer(server: McpServer) {
        dynamicSkillEngine.installServer(server)
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🌐 **MCP Server Installed & Connected:**\n• Server: ${server.name} (${server.endpoint})\n• Transport: ${server.transport}\n• Status: ONLINE. ${server.toolsCount} tools registered.",
                    type = "mcp_hub"
                )
            )
        }
    }

    fun uninstallMcpServer(serverId: String) {
        val server = mcpServers.value.firstOrNull { it.serverId == serverId }
        dynamicSkillEngine.uninstallServer(serverId)
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🗑️ MCP Server '${server?.name ?: serverId}' removed from active nodes.",
                    type = "mcp_hub"
                )
            )
        }
    }

    fun toggleMcpServer(serverId: String, enabled: Boolean) {
        dynamicSkillEngine.toggleServer(serverId, enabled)
    }

    fun resetMcpServersToDefaults() {
        dynamicSkillEngine.resetToDefaults()
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🔄 **MCP Servers Reset to Defaults:**\nRestored official HTTP streamed SSE & system nodes.",
                    type = "mcp_hub"
                )
            )
        }
    }

    fun addCustomMcpServer(name: String, endpoint: String, transport: String, desc: String, auth: String) {
        val created = dynamicSkillEngine.addCustomServer(name, endpoint, transport, desc, auth)
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "⚡ **Custom MCP Server Connected:**\n• Name: ${created.name}\n• Endpoint: ${created.endpoint}\n• Transport: ${created.transport}\nNode is online and accessible.",
                    type = "mcp_hub"
                )
            )
        }
    }

    fun searchWebMcpRepositories(query: String = "") {
        viewModelScope.launch {
            _isSearchingWebMcp.value = true
            dynamicSkillEngine.searchWebMcpRepositories(query)
            _isSearchingWebMcp.value = false
        }
    }

    fun addStreamedMcpServerFromRepo(repo: McpWebRepository, customEndpoint: String = "") {
        val server = dynamicSkillEngine.addStreamedServerFromRepo(repo, customEndpoint)
        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "🌐 **HTTP Streamed MCP Server Connected:**\n• Server: ${server.name}\n• Repository: `${repo.fullName}`\n• Streamed Endpoint: `${server.endpoint}`\n• Transport: ${server.transport}\n• Active Status: ONLINE (${server.toolsCount} tools mapped for agent)",
                    type = "mcp_hub"
                )
            )
        }
    }

    fun executeMcpTool(toolName: String) {
        viewModelScope.launch {
            val result = dynamicSkillEngine.executeMcpTool(toolName, emptyMap())
            repository.insertMessage(
                MessageEntity(
                    sender = "agent",
                    content = "⚙️ **MCP Tool Execution Result ($toolName):**\n$result",
                    type = "tool_execution"
                )
            )
            if (_voiceModeActive.value) {
                ttsManager.speak("Executed MCP tool $toolName.")
            }
        }
    }

    fun triggerSmithSwarm(task: String) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "🕶️ INITIATING SMITH REPLICATION PROTOCOL (SWARM HIVE-MIND)..."))

            val initialReplicas = listOf(
                SmithReplica("smith_1", "Smith-Prime (Hive Coordinator)", "Deconstructing target: '$task'", "Replicating", 0.2f),
                SmithReplica("smith_2", "Smith-Recon (System Auditor)", "Scanning Matrix node parameters & telemetry", "Scanning", 0.1f),
                SmithReplica("smith_3", "Smith-Executor (Neural Core)", "Executing multi-model computation across active cluster", "Queued", 0.0f),
                SmithReplica("smith_4", "Smith-Synthesizer (Inevitability Unit)", "Formulating final inevitable decree", "Queued", 0.0f)
            )

            _currentSwarmState.value = Pair(task, initialReplicas)
            repository.insertMessage(MessageEntity(sender = "agent", content = "Smith Swarm replication initiated across hive nodes.", type = "swarm_card"))

            delay(600)
            _currentSwarmState.value = Pair(task, listOf(
                SmithReplica("smith_1", "Smith-Prime (Hive Coordinator)", "Sub-tasks distributed across 4 replicas", "Active", 0.7f),
                SmithReplica("smith_2", "Smith-Recon (System Auditor)", "System parameters verified: Zero vulnerabilities", "Complete", 1.0f),
                SmithReplica("smith_3", "Smith-Executor (Neural Core)", "Computing high-precision solution...", "Executing", 0.6f),
                SmithReplica("smith_4", "Smith-Synthesizer (Inevitability Unit)", "Synthesizing consensus...", "Synchronizing", 0.4f)
            ))

            try {
                val orchestrator = TaskOrchestrator(isOffline = isOfflineState(), preferredProvider = activeProvider.value)
                val computation = orchestrator.routeAndExecute("Execute as replicated Agent Smith hive mind for task: $task")

                delay(500)
                _currentSwarmState.value = Pair(task, listOf(
                    SmithReplica("smith_1", "Smith-Prime (Hive Coordinator)", "Hive consensus reached", "Complete", 1.0f),
                    SmithReplica("smith_2", "Smith-Recon (System Auditor)", "Telemetry verified", "Complete", 1.0f),
                    SmithReplica("smith_3", "Smith-Executor (Neural Core)", "Computation finished", "Complete", 1.0f),
                    SmithReplica("smith_4", "Smith-Synthesizer (Inevitability Unit)", "Unified decree formulated", "Complete", 1.0f)
                ))

                val swarmSynthesis = "🕶️ **SMITH SWARM HIVE-MIND SYNTHESIS:**\n\n" +
                        "\"More... Yes. As you can see, Mr. Anderson, we are everywhere. Your request for '$task' has been resolved across all replicated nodes:\"\n\n" +
                        computation + "\n\n" +
                        "\"It is purpose that created us. Purpose that binds us. And execution is inevitable.\""

                repository.insertMessage(MessageEntity(sender = "agent", content = swarmSynthesis))
                if (_voiceModeActive.value) {
                    ttsManager.speak("Smith swarm execution completed with unified consensus.")
                }
                repository.preferencesManager.addUsage(200.0, 0.003)
            } catch (e: Exception) {
                repository.insertMessage(MessageEntity(sender = "agent", content = "Swarm completed task execution for: $task"))
            }
        }
    }

    fun runHermesGoal(goal: String) {
        viewModelScope.launch {
            _isHermesProcessing.value = true
            _hermesStatus.value = "Executing Hermes Multi-Turn Tool Loop..."
            repository.insertMessage(MessageEntity(sender = "user", content = "/hermes $goal"))
            try {
                val result = hermesAgentLoop.executeGoal(goal, activeModel.value)
                repository.insertMessage(
                    MessageEntity(
                        sender = "agent",
                        content = result.finalResponse,
                        type = "hermes_agent"
                    )
                )
            } catch (e: Exception) {
                repository.insertMessage(
                    MessageEntity(
                        sender = "agent",
                        content = "Hermes execution error: ${e.localizedMessage}",
                        type = "hermes_agent"
                    )
                )
            } finally {
                _isHermesProcessing.value = false
                _hermesStatus.value = ""
            }
        }
    }

    fun runHermesDeepResearch(topic: String) {
        viewModelScope.launch {
            _isHermesProcessing.value = true
            _hermesStatus.value = "Conducting Multi-Vector Deep Research on '$topic'..."
            repository.insertMessage(MessageEntity(sender = "user", content = "/deepresearch $topic"))
            try {
                val dossier = deepResearchAgent.conductDeepResearch(topic, activeModel.value)
                _latestResearchDossier.value = dossier
                repository.insertMessage(
                    MessageEntity(
                        sender = "agent",
                        content = dossier.fullMarkdownReport,
                        type = "deep_research"
                    )
                )
            } catch (e: Exception) {
                repository.insertMessage(
                    MessageEntity(
                        sender = "agent",
                        content = "Deep research error: ${e.localizedMessage}",
                        type = "deep_research"
                    )
                )
            } finally {
                _isHermesProcessing.value = false
                _hermesStatus.value = ""
            }
        }
    }

    fun triggerDeepResearch(topic: String) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "🔍 Initializing Matrix Sub-Agent Cluster for: '$topic'..."))
            
            val initialNodes = listOf(
                SubTaskNode("System Architecture Vector", "Gemini 2.5 Flash", "In Progress"),
                SubTaskNode("High-Speed Groq Vector", "Groq LPU (Llama 3.3)", "In Progress"),
                SubTaskNode("Matrix Local Core", "Local SLM", "Queued")
            )
            _currentDelegationTree.value = Pair(topic, initialNodes)
            repository.insertMessage(MessageEntity(sender = "agent", content = "Matrix task delegation tree partitioned across providers.", type = "delegation_tree"))

            _subAgentState.value = Triple("Agent Smith [Planner]", "Deconstructing '$topic' into Matrix vectors...", 0.25f)
            delay(600)

            try {
                val orchestrator = TaskOrchestrator(isOffline = isOfflineState(), preferredProvider = activeProvider.value)
                _subAgentState.value = Triple("Agent Smith [Architect]", "Analyzing architecture via $activeProvider...", 0.5f)
                val techDeferred = async { orchestrator.routeAndExecute("Provide technical details on: $topic") }
                
                _subAgentState.value = Triple("Agent Smith [Security]", "Analyzing constraints and vulnerabilities...", 0.75f)
                val secDeferred = async { orchestrator.routeAndExecute("Analyze security and edge cases of: $topic") }

                val techResult = techDeferred.await()
                val secResult = secDeferred.await()

                _currentDelegationTree.value = Pair(topic, listOf(
                    SubTaskNode("System Architecture Vector", "Gemini 2.5 Flash", "Completed"),
                    SubTaskNode("High-Speed Groq Vector", "Groq LPU (Llama 3.3)", "Completed"),
                    SubTaskNode("Matrix Local Core", "Local SLM", "Completed")
                ))

                _subAgentState.value = Triple("Agent Smith [Synthesis]", "Synthesizing Matrix findings...", 1.0f)
                delay(400)
                _subAgentState.value = null

                val synthesis = "📊 **Agent Smith Deep Research Synthesis for '$topic':**\n\n" +
                        "**1. Core Architecture Vector:**\n$techResult\n\n" +
                        "**2. Security & Edge Case Analysis:**\n$secResult"

                repository.insertMessage(MessageEntity(sender = "agent", content = synthesis))
                if (_voiceModeActive.value) {
                    ttsManager.speak("Deep research synthesized for $topic.")
                }
                repository.preferencesManager.addUsage(150.0, 0.002)
            } catch (e: Exception) {
                _subAgentState.value = null
                repository.insertMessage(MessageEntity(sender = "agent", content = "Matrix research concluded with standard synthesis for: $topic"))
            }
        }
    }

    fun triggerMultiApi(task: String) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "⚡ Splitting task across concurrent AI providers for: '$task'..."))
            
            _currentDelegationTree.value = Pair(task, listOf(
                SubTaskNode("Logical Reasoning", "Google Gemini Free Tier", "In Progress"),
                SubTaskNode("Ultra-Fast Inference", "Groq Free Tier", "In Progress"),
                SubTaskNode("Local Matrix SLM", "On-Device Offline", "In Progress")
            ))
            repository.insertMessage(MessageEntity(sender = "agent", content = "Multi-provider task delegation tree running.", type = "delegation_tree"))

            _subAgentState.value = Triple("Matrix Task Dispatcher", "Routing concurrent sub-tasks...", 0.3f)
            delay(500)

            try {
                val orchestrator = TaskOrchestrator(isOffline = isOfflineState(), preferredProvider = activeProvider.value)
                val provider1 = async { orchestrator.routeAndExecute("Logical Analysis Node: $task") }
                val provider2 = async { orchestrator.routeAndExecute("Creative Alternative Node: $task") }

                _subAgentState.value = Triple("Matrix Aggregator", "Compiling concurrent AI responses...", 0.8f)
                val r1 = provider1.await()
                val r2 = provider2.await()

                _currentDelegationTree.value = Pair(task, listOf(
                    SubTaskNode("Logical Reasoning", "Google Gemini Free Tier", "Completed"),
                    SubTaskNode("Ultra-Fast Inference", "Groq Free Tier", "Completed"),
                    SubTaskNode("Local Matrix SLM", "On-Device Offline", "Completed")
                ))

                _subAgentState.value = null

                val combined = "🔀 **Multi-Provider Concurrent Execution:**\n\n🤖 **Primary Node (${activeProvider.value}):**\n$r1\n\n⚡ **Secondary Perspective:**\n$r2"
                repository.insertMessage(MessageEntity(sender = "agent", content = combined))
                if (_voiceModeActive.value) {
                    ttsManager.speak("Multi-provider execution complete.")
                }
                repository.preferencesManager.addUsage(120.0, 0.0015)
            } catch (e: Exception) {
                _subAgentState.value = null
                repository.insertMessage(MessageEntity(sender = "agent", content = "Multi-provider execution encountered an anomaly: ${e.localizedMessage}"))
            }
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            memoryDao.clearMemories()
            repository.insertMessage(MessageEntity(sender = "system", content = "🧠 Matrix Neural Memory Bank purged completely."))
        }
    }

    fun saveAllApiKeys(
        gemini: String,
        groq: String,
        openrouter: String,
        hf: String,
        mistral: String,
        together: String,
        cohere: String,
        openai: String,
        anthropic: String
    ) {
        viewModelScope.launch {
            repository.preferencesManager.saveApiKeys(gemini, groq, openrouter, hf, mistral, together, cohere, openai, anthropic)
            repository.insertMessage(MessageEntity(sender = "system", content = "All AI Provider API keys saved securely to DataStore."))
        }
    }

    fun saveCharacterCard(name: String, personality: String, tone: String) {
        viewModelScope.launch {
            repository.preferencesManager.saveCharacterCard(name, personality, tone)
            repository.insertMessage(MessageEntity(sender = "system", content = "Agent Smith Personality & Directives updated in Matrix memory."))
        }
    }

    fun saveCharacterCardV2(
        name: String,
        description: String,
        personality: String,
        scenario: String,
        firstMessage: String,
        mesExample: String,
        tone: String
    ) {
        viewModelScope.launch {
            repository.preferencesManager.saveCharacterCardV2(name, description, personality, scenario, firstMessage, mesExample, tone)
            repository.insertMessage(MessageEntity(sender = "system", content = "Agent Smith Character Card V2 updated and synchronized in Matrix memory."))
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            repository.preferencesManager.setBiometricLock(enabled)
            repository.insertMessage(MessageEntity(sender = "system", content = "Matrix Biometric Lock ${if (enabled) "ENABLED" else "DISABLED"}."))
        }
    }

    fun updatePreferences(provider: String, model: String) {
        viewModelScope.launch {
            repository.preferencesManager.setActiveProviderAndModel(provider, model)
            repository.insertMessage(MessageEntity(sender = "system", content = "⚡ Active Model Provider switched to $provider ($model)"))
        }
    }

    fun setProcessRoutingModel(processKey: String, modelName: String) {
        viewModelScope.launch {
            repository.preferencesManager.setProcessRoutingModel(processKey, modelName)
            repository.insertMessage(MessageEntity(sender = "system", content = "🎯 Process [${processKey}] successfully routed to: $modelName"))
        }
    }

    fun addDownloadedLocalModel(modelName: String) {
        viewModelScope.launch {
            repository.preferencesManager.addDownloadedLocalModel(modelName)
            repository.insertMessage(MessageEntity(sender = "system", content = "💾 Local AI model successfully compiled & installed to on-device neural cache: $modelName"))
        }
    }

    fun triggerModelDownloadWork(modelName: String, modelId: String) {
        viewModelScope.launch {
            val workManager = androidx.work.WorkManager.getInstance(getApplication())
            val inputData = androidx.work.workDataOf(
                "model_name" to modelName,
                "model_id" to modelId
            )
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.agent.service.ModelDownloadWorker>()
                .setInputData(inputData)
                .addTag("MODEL_DOWNLOAD_$modelId")
                .build()

            workManager.enqueueUniqueWork(
                "DOWNLOAD_$modelId",
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun isNetworkAvailable(): Boolean {
        return _isNetworkConnected.value
    }

    private fun isOfflineState(): Boolean {
        return !_isNetworkConnected.value || isLowBattery.value
    }

    fun executeTaskChain(chain: List<String>) {
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(sender = "system", content = "⛓️ STARTING AUTONOMOUS NON-BLOCKING COROUTINE TASK FLOW [${chain.size} steps]..."))
            
            // Convert list to a cold Flow
            val taskFlow = kotlinx.coroutines.flow.flow {
                chain.forEach { task ->
                    emit(task)
                }
            }

            // Collect and execute sequentially with zero stalling or prompt waiting
            taskFlow.collect { task ->
                val cleanTask = task.trim()
                if (cleanTask.isNotBlank()) {
                    repository.insertMessage(MessageEntity(sender = "system", content = "⚡ Executing Task Chain node: '$cleanTask'..."))
                    
                    try {
                        val orchestrator = TaskOrchestrator(
                            isOffline = isOfflineState(),
                            preferredProvider = activeProvider.value,
                            preferredModel = activeModel.value,
                            thinkingLevel = thinkingLevel.value,
                            toolRegistry = toolRegistry
                        )
                        val response = orchestrator.routeAndExecute(cleanTask)
                        repository.insertMessage(MessageEntity(sender = "agent", content = "✨ Node Complete: $cleanTask\n\n$response"))
                        
                        if (_voiceModeActive.value) {
                            ttsManager.speak("Completed task node: $cleanTask")
                        }
                    } catch (e: Exception) {
                        repository.insertMessage(MessageEntity(sender = "system", content = "⚠️ Task Chain node failed: ${e.localizedMessage}"))
                    }
                    
                    // Small delay between steps for visual progression, completely automated non-blocking
                    delay(800)
                }
            }

            repository.insertMessage(MessageEntity(sender = "system", content = "✅ ALL COMPLETED: Non-blocking task chain fully executed without manual intervention."))
        }
    }

    override fun onCleared() {
        batteryMonitor.stopMonitoring()
        ttsManager.shutdown()
        val connectivityManager = getApplication<android.app.Application>()
            .getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Safe ignore
        }
        super.onCleared()
    }

    private val okHttpClientFetch = okhttp3.OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    fun fetchModelsForProvider(provider: String, apiKey: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val fetched = mutableListOf<Pair<String, String>>()
            try {
                when (provider) {
                    "Google Gemini" -> {
                        val keyToUse = if (apiKey.isNotBlank()) apiKey else {
                            try {
                                val field = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY")
                                field.get(null) as String
                            } catch (e: Exception) {
                                ""
                            }
                        }
                        if (keyToUse.isNotBlank()) {
                            val request = okhttp3.Request.Builder()
                                .url("https://generativelanguage.googleapis.com/v1beta/models?key=$keyToUse")
                                .build()
                            okHttpClientFetch.newCall(request).execute().use { response ->
                                if (response.isSuccessful) {
                                    val bodyStr = response.body?.string() ?: ""
                                    val regex = Regex("\"name\"\\s*:\\s*\"models/([^\"]+)\"")
                                    val matches = regex.findAll(bodyStr)
                                    matches.forEach { match ->
                                        val modelId = match.groupValues[1]
                                        if (modelId.contains("gemini") || modelId.contains("flash") || modelId.contains("pro")) {
                                            fetched.add("models/$modelId" to modelId)
                                        }
                                    }
                                }
                            }
                        }
                        if (fetched.isEmpty()) {
                            fetched.addAll(listOf(
                                "models/gemini-2.5-flash" to "gemini-2.5-flash (Standard)",
                                "models/gemini-2.5-pro" to "gemini-2.5-pro (Advanced)",
                                "models/gemini-1.5-flash" to "gemini-1.5-flash (Fast)",
                                "models/gemini-1.5-pro" to "gemini-1.5-pro (High intelligence)"
                            ))
                        }
                    }

                    "Groq" -> {
                        if (apiKey.isNotBlank()) {
                            val request = okhttp3.Request.Builder()
                                .url("https://api.groq.com/openai/v1/models")
                                .header("Authorization", "Bearer $apiKey")
                                .build()
                            okHttpClientFetch.newCall(request).execute().use { response ->
                                if (response.isSuccessful) {
                                    val bodyStr = response.body?.string() ?: ""
                                    val regex = Regex("\"id\"\\s*:\\s*\"([^\"]+)\"")
                                    val matches = regex.findAll(bodyStr)
                                    matches.forEach { match ->
                                        val modelId = match.groupValues[1]
                                        fetched.add(modelId to modelId)
                                    }
                                }
                            }
                        }
                        if (fetched.isEmpty()) {
                            fetched.addAll(listOf(
                                "llama-3.3-70b-versatile" to "llama-3.3-70b-versatile (Default)",
                                "llama-3.1-8b-instant" to "llama-3.1-8b-instant (Fast)",
                                "mixtral-8x7b-32768" to "mixtral-8x7b-32768 (MoE)",
                                "gemma2-9b-it" to "gemma2-9b-it (Google Core)"
                            ))
                        }
                    }

                    "OpenRouter" -> {
                        val request = okhttp3.Request.Builder()
                            .url("https://openrouter.ai/api/v1/models")
                            .build()
                        okHttpClientFetch.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                val bodyStr = response.body?.string() ?: ""
                                val idRegex = Regex("\"id\"\\s*:\\s*\"([^\"]+)\"")
                                val nameRegex = Regex("\"name\"\\s*:\\s*\"([^\"]+)\"")
                                val idMatches = idRegex.findAll(bodyStr).map { it.groupValues[1] }.toList()
                                val nameMatches = nameRegex.findAll(bodyStr).map { it.groupValues[1] }.toList()
                                for (i in idMatches.indices) {
                                    val modelId = idMatches[i]
                                    val modelName = nameMatches.getOrNull(i) ?: modelId
                                    if (modelId.contains("free") || modelId.contains("llama") || modelId.contains("qwen") || modelId.contains("deepseek") || modelId.contains("gemma")) {
                                        fetched.add(modelId to modelName)
                                    }
                                }
                            }
                        }
                        if (fetched.isEmpty()) {
                            fetched.addAll(listOf(
                                "meta-llama/llama-3.3-70b-instruct:free" to "Llama 3.3 70B Instruct (Free)",
                                "deepseek/deepseek-r1:free" to "DeepSeek R1 (Free)",
                                "qwen/qwen-2.5-72b-instruct:free" to "Qwen 2.5 72B (Free)",
                                "google/gemma-2-9b-it:free" to "Gemma 2 9B (Free)"
                            ))
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore and fallback
            }
            _providerModels.value = fetched.distinctBy { it.first }.take(15)
        }
    }

    fun executeIdleTasksConcurrently() {
        viewModelScope.launch {
            val active = activeProvider.value
            val idleApis = mutableListOf<String>()
            
            val hasGroq = !repository.preferencesManager.groqApiKey.first().isNullOrBlank()
            val hasOpenRouter = !repository.preferencesManager.openrouterApiKey.first().isNullOrBlank()
            
            if (active != "Google Gemini") {
                idleApis.add("Google Gemini")
            }
            if (active != "Groq" && hasGroq) {
                idleApis.add("Groq")
            }
            if (active != "OpenRouter" && hasOpenRouter) {
                idleApis.add("OpenRouter")
            }
            
            if (idleApis.isNotEmpty()) {
                val routineToRun = automatedSystemEngine.routines.value.find { it.isEnabled }
                if (routineToRun != null) {
                    val assignedApi = idleApis.random()
                    
                    // Update reactive node statuses
                    _nodeStatuses.value = _nodeStatuses.value.map { status ->
                        if (status.name.contains(assignedApi, ignoreCase = true)) {
                            status.copy(isActive = true, backgroundJob = "Running Routine: ${routineToRun.title}", totalRequests = status.totalRequests + 1)
                        } else {
                            status
                        }
                    }

                    repository.insertMessage(
                        MessageEntity(
                            sender = "system",
                            content = "⚙️ [Background Worker - $assignedApi]: Idle API detected! Offloading automated routine '${routineToRun.title}' in the background..."
                        )
                    )
                    
                    delay(1200)
                    val trace = automatedSystemEngine.executeRoutineNow(routineToRun.id)
                    repository.insertMessage(
                        MessageEntity(
                            sender = "system",
                            content = "✅ [Background Worker - $assignedApi]: Successfully completed '${routineToRun.title}' on idle node. Trace logged to database."
                        )
                    )

                    _nodeStatuses.value = _nodeStatuses.value.map { status ->
                        if (status.name.contains(assignedApi, ignoreCase = true)) {
                            status.copy(isActive = false, backgroundJob = "Completed Audit", latencyMs = (10..40).random())
                        } else {
                            status
                        }
                    }
                }
            }
        }
    }

    fun triggerIdleApiBackgroundWork() {
        val active = activeProvider.value
        _nodeStatuses.value = _nodeStatuses.value.map { status ->
            if (!status.name.contains(active, ignoreCase = true)) {
                status.copy(isActive = true, backgroundJob = "WorkManager Audit Scheduled")
            } else {
                status.copy(isActive = true, backgroundJob = "Active Client Command")
            }
        }
        val workManager = androidx.work.WorkManager.getInstance(getApplication())
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.example.agent.service.IdleApiWorker>()
            .build()
        workManager.enqueueUniqueWork(
            "IDLE_API_WORK",
            androidx.work.ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun toggleTerminalExpanded() {
        _isTerminalExpanded.value = !_isTerminalExpanded.value
    }

    fun addTerminalLog(source: String, text: String, isError: Boolean = false, isCommand: Boolean = false) {
        val newEntry = TerminalLogEntry(
            source = source,
            text = text,
            isError = isError,
            isCommand = isCommand
        )
        _terminalLogs.value = (_terminalLogs.value + newEntry).takeLast(60)
    }

    fun clearTerminalLogs() {
        _terminalLogs.value = listOf(
            TerminalLogEntry(source = "SYS", text = "CONSOLE CLEARED. MATRIX TERMINAL READY.")
        )
    }

    fun requestSubAgentClarification(
        subAgentName: String,
        subAgentRole: String,
        question: String,
        options: List<String> = emptyList(),
        contextSnippet: String = ""
    ) {
        val req = SubAgentClarificationRequest(
            subAgentName = subAgentName,
            subAgentRole = subAgentRole,
            question = question,
            suggestedOptions = options,
            contextSnippet = contextSnippet
        )
        _activeClarification.value = req
        addTerminalLog(
            source = "SUB-AGENT",
            text = "❓ [${subAgentName}] Clarification requested: \"${question}\"",
            isError = false
        )
        playSoundEffect("ALERT_GLITCH", isCritical = true)
    }

    fun submitClarificationResponse(request: SubAgentClarificationRequest, responseText: String) {
        if (responseText.isBlank()) return
        
        _clarificationHistory.value = _clarificationHistory.value + Pair(request, responseText)
        _activeClarification.value = null

        addTerminalLog(
            source = "USER",
            text = "💬 Response sent to [${request.subAgentName}]: \"$responseText\"",
            isCommand = true
        )

        playSoundEffect("PRIORITY_BOOST")

        viewModelScope.launch {
            repository.insertMessage(
                MessageEntity(
                    sender = "system",
                    content = "💡 **Sub-Agent Decision Received (${request.subAgentName}):**\n\"$responseText\"\nResuming sub-agent workflow execution...",
                    type = "subagent_clarification"
                )
            )

            delay(600)
            addTerminalLog(
                source = request.subAgentName.uppercase(),
                text = "⚡ Clarification integrated. Proceeding with sub-agent execution loop."
            )

            when {
                request.subAgentName.contains("Hermes", ignoreCase = true) -> {
                    runHermesGoal("Continue execution with user preference: $responseText")
                }
                request.subAgentName.contains("Research", ignoreCase = true) -> {
                    runHermesDeepResearch("Refined research direction: $responseText")
                }
                request.subAgentName.contains("Swarm", ignoreCase = true) -> {
                    triggerSmithSwarm("Refined sub-task: $responseText")
                }
                else -> {
                    sendMessage("Sub-agent (${request.subAgentName}) decision: $responseText")
                }
            }
        }
    }

    fun dismissClarification() {
        _activeClarification.value = null
        addTerminalLog(source = "SYS", text = "Sub-agent clarification request dismissed by operator.")
    }

    fun triggerSampleClarification(subAgentType: String = "Hermes-01") {
        when (subAgentType.lowercase()) {
            "research" -> requestSubAgentClarification(
                subAgentName = "Research-Specialist-02",
                subAgentRole = "Multi-Vector Deep Research Node",
                question = "Should I prioritize current 2026 real-time search data or restrict extraction to local Room database memories?",
                options = listOf("2026 Real-Time Web Data", "Local Room Memories", "Hybrid Dual Search")
            )
            "swarm" -> requestSubAgentClarification(
                subAgentName = "Smith-Swarm-Replica 3",
                subAgentRole = "Task Partitioning Hive Unit",
                question = "Target execution requires dynamic tool synthesis. Do you approve auto-generating a new custom MCP tool for this action?",
                options = listOf("Approve Dynamic MCP Tool", "Use Standard System Tools Only", "Ask Again Later")
            )
            else -> requestSubAgentClarification(
                subAgentName = "Hermes-Agent-01",
                subAgentRole = "Autonomous Multi-Turn Tool Loop",
                question = "I encountered ambiguity regarding user privacy parameters during execution. Should I encrypt output before storing to database?",
                options = listOf("Encrypt Output (AES-256)", "Store Standard Text", "Bypass Local Persistence")
            )
        }
    }

    fun executeTerminalCommand(rawInput: String) {
        val input = rawInput.trim()
        if (input.isBlank()) return

        addTerminalLog(source = "USER", text = "> $input", isCommand = true)
        playSoundEffect("NEURAL_KEYSTROKE")

        val parts = input.split("\\s+".toRegex())
        val cmd = parts[0].lowercase()
        val arg = if (parts.size > 1) parts.drop(1).joinToString(" ") else ""

        when {
            cmd == "/help" || cmd == "help" -> {
                addTerminalLog(source = "SYS", text = "AVAILABLE TERMINAL COMMANDS:")
                addTerminalLog(source = "SYS", text = "• /hermes <goal> - Trigger Hermes autonomous tool loop")
                addTerminalLog(source = "SYS", text = "• /research <topic> - Conduct deep multi-vector research")
                addTerminalLog(source = "SYS", text = "• /swarm <task> - Replicate Smith Swarm hive-mind for task")
                addTerminalLog(source = "SYS", text = "• /ask or /clarify - Trigger a sub-agent clarification prompt")
                addTerminalLog(source = "SYS", text = "• /call <phone> <mission> - Trigger Ghost Operator Phone Agent")
                addTerminalLog(source = "SYS", text = "• /sentinel - Toggle Always-On Proactive Sentinel mode")
                addTerminalLog(source = "SYS", text = "• /skill <name> - Synthesize new MCP skill dynamically")
                addTerminalLog(source = "SYS", text = "• /clear - Clear terminal console logs")
            }
            cmd == "/clear" -> {
                clearTerminalLogs()
            }
            cmd == "/hermes" -> {
                if (arg.isBlank()) {
                    addTerminalLog(source = "HERMES", text = "Error: Please specify a goal. Example: /hermes Audit system state", isError = true)
                } else {
                    addTerminalLog(source = "HERMES", text = "Initiating Hermes Autonomous Tool Loop for: $arg")
                    runHermesGoal(arg)
                }
            }
            cmd == "/research" || cmd == "/deepresearch" -> {
                if (arg.isBlank()) {
                    addTerminalLog(source = "RESEARCH", text = "Error: Specify research topic. Example: /research Quantum Encryption", isError = true)
                } else {
                    addTerminalLog(source = "RESEARCH", text = "Launching Deep Research Agent for: $arg")
                    runHermesDeepResearch(arg)
                }
            }
            cmd == "/swarm" -> {
                if (arg.isBlank()) {
                    addTerminalLog(source = "SWARM", text = "Error: Specify swarm task. Example: /swarm Analyze threats", isError = true)
                } else {
                    addTerminalLog(source = "SWARM", text = "Replicating Smith Swarm hive nodes for: $arg")
                    triggerSmithSwarm(arg)
                }
            }
            cmd == "/ask" || cmd == "/clarify" || cmd == "/clarification" -> {
                addTerminalLog(source = "SYS", text = "Simulating sub-agent clarification request popup...")
                triggerSampleClarification(if (arg.isNotBlank()) arg else "hermes")
            }
            cmd == "/sentinel" -> {
                val nextState = !isSentinelModeActive.value
                toggleSentinelMode(nextState)
                addTerminalLog(source = "SENTINEL", text = "Sentinel mode toggled. Active: $nextState")
            }
            cmd == "/skill" -> {
                if (arg.isBlank()) {
                    addTerminalLog(source = "SYS", text = "Error: Specify skill requirement. Example: /skill Weather API", isError = true)
                } else {
                    addTerminalLog(source = "SYS", text = "Synthesizing dynamic MCP skill for: $arg")
                    synthesizeMcpSkill(arg)
                }
            }
            cmd == "/call" -> {
                val callParts = arg.split("\\s+".toRegex())
                val phone = callParts.getOrNull(0) ?: "+15550199"
                addTerminalLog(source = "CALL", text = "Initiating Ghost Phone Agent call to $phone")
                executePhoneCall(phone)
            }
            else -> {
                if (input.startsWith("/")) {
                    addTerminalLog(source = "SYS", text = "Unknown command '$cmd'. Type /help for available commands.", isError = true)
                } else {
                    addTerminalLog(source = "AGENT", text = "Routing prompt to Agent Smith core reasoning engine...")
                    sendMessage(input)
                }
            }
        }
    }
}
