package com.example.agent.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agent.ui.chat.components.AiCoreGateCard
import com.example.agent.ui.chat.components.CallHistoryCard
import com.example.agent.ui.chat.components.KnowledgeBaseCard
import com.example.agent.ui.chat.components.LocalModelsGuideCard
import com.example.agent.ui.chat.components.MatrixMemoryCard
import com.example.agent.ui.chat.components.MatrixToolsMenuCard
import com.example.agent.ui.chat.components.McpServerManagementDialog
import com.example.agent.ui.chat.components.McpSkillsCard
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.chat.components.MessageCard
import com.example.agent.ui.chat.components.OptionsMenuCard
import com.example.agent.ui.chat.components.PhoneCallAgentCard
import com.example.agent.ui.chat.components.PreCognitionCard
import com.example.agent.ui.chat.components.ProactiveSentinelCard
import com.example.agent.ui.chat.components.QuickActionsRow
import com.example.agent.ui.chat.components.SmithSwarmCard
import com.example.agent.ui.chat.components.SubAgentOrchestratorVisualizer
import com.example.agent.ui.chat.components.SubAgentProgressCard
import com.example.agent.ui.chat.components.TaskDelegationTreeCard
import com.example.agent.ui.chat.components.TaskManagerCard
import com.example.agent.ui.chat.components.UserGuideCard
import com.example.agent.ui.chat.components.VoiceWaveformVisualizer
import com.example.agent.ui.components.MatrixDigitalRainBackground
import com.example.agent.ui.onboarding.OnboardingCard
import com.example.agent.ui.onboarding.OnboardingViewModel
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary
import com.example.agent.util.AmbientListenerManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    onboardingViewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()
    val skills by viewModel.skills.collectAsState()
    val knowledgeList by viewModel.knowledgeList.collectAsState()
    val proactiveActions by viewModel.proactiveActions.collectAsState()
    val pendingProactiveActions by viewModel.pendingProactiveActions.collectAsState()
    val preCognitionPredictions by viewModel.preCognitionPredictions.collectAsState()
    val ghostCallStatus by viewModel.ghostCallStatus.collectAsState()
    val thinkingMethods by viewModel.thinkingMethods.collectAsState()
    val activeThinkingMethod by viewModel.activeThinkingMethod.collectAsState()
    val lastThinkingTrace by viewModel.lastThinkingTrace.collectAsState()
    val automatedRoutines by viewModel.automatedRoutines.collectAsState()
    val automatedExecutionTraces by viewModel.automatedExecutionTraces.collectAsState()
    val isSentinelModeActive by viewModel.isSentinelModeActive.collectAsState()
    val isDeepSleepModeActive by viewModel.isDeepSleepModeActive.collectAsState()
    val isWakeWordEnabled by viewModel.isWakeWordEnabled.collectAsState()
    val customWakeWord by viewModel.customWakeWord.collectAsState()
    val wakeWordSensitivity by viewModel.wakeWordSensitivity.collectAsState()
    val ambientTranscript by viewModel.ambientTranscript.collectAsState()
    val soundFxVolume by viewModel.soundFxVolume.collectAsState()
    val soundFxFrequency by viewModel.soundFxFrequency.collectAsState()

    val mcpServers by viewModel.mcpServers.collectAsState()
    val catalogServers by viewModel.catalogServers.collectAsState()
    val mcpTools by viewModel.mcpTools.collectAsState()

    val profile by onboardingViewModel.profile.collectAsState()
    val isAiCoreActive by viewModel.isAiCoreActive.collectAsState()
    val activeProvider by viewModel.activeProvider.collectAsState()
    val activeModel by viewModel.activeModel.collectAsState()
    val groqKey by viewModel.groqApiKey.collectAsState()
    val openrouterKey by viewModel.openrouterApiKey.collectAsState()
    val hfKey by viewModel.huggingfaceApiKey.collectAsState()
    val openaiKey by viewModel.openaiApiKey.collectAsState()
    val anthropicKey by viewModel.anthropicApiKey.collectAsState()
    val biometricLock by viewModel.biometricLock.collectAsState()
    val totalCost by viewModel.totalCost.collectAsState()
    val subAgentState by viewModel.subAgentState.collectAsState()
    
    // Character Card v2 State
    val charName by viewModel.charName.collectAsState()
    val charDescription by viewModel.charDescription.collectAsState()
    val charPersonality by viewModel.charPersonality.collectAsState()
    val charScenario by viewModel.charScenario.collectAsState()
    val charFirstMessage by viewModel.charFirstMessage.collectAsState()
    val charMesExample by viewModel.charMesExample.collectAsState()
    val charTone by viewModel.charTone.collectAsState()

    val voiceModeActive by viewModel.voiceModeActive.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val liveTranscript by viewModel.liveTranscript.collectAsState()

    val delegationTree by viewModel.currentDelegationTree.collectAsState()
    val swarmState by viewModel.currentSwarmState.collectAsState()
    val callMission by viewModel.currentCallMission.collectAsState()
    val isLowBattery by viewModel.isLowBattery.collectAsState()

    val showMcpMenu by viewModel.showMcpMenu.collectAsState()
    var showOverflowMenu by remember { mutableStateOf(false) }

    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var ambientManager by remember { mutableStateOf<AmbientListenerManager?>(null) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechListening(
                context = context,
                onSpeechStart = { viewModel.setListening(true) },
                onPartial = { transcript -> viewModel.updateLiveTranscript(transcript) },
                onResult = { resultText ->
                    viewModel.setListening(false)
                    if (resultText.isNotBlank()) {
                        viewModel.sendMessage(resultText)
                    }
                },
                onError = { viewModel.setListening(false) },
                onInit = { recognizer -> speechRecognizer = recognizer }
            )
        }
    }

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && callMission != null) {
            viewModel.executePhoneCall(callMission!!.phoneNumber)
        }
    }

    // Manage continuous Always-On Ambient Sentinel Listener Lifecycle
    DisposableEffect(isSentinelModeActive) {
        if (isSentinelModeActive) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                val mgr = AmbientListenerManager(
                    context = context,
                    onPartialTranscript = { partial -> viewModel.processAmbientTranscript(partial) },
                    onFinalTranscript = { finalTranscript -> viewModel.processAmbientTranscript(finalTranscript) },
                    onError = { }
                )
                ambientManager = mgr
                mgr.startListening()
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            ambientManager?.stopListening()
            ambientManager = null
        }

        onDispose {
            ambientManager?.stopListening()
            ambientManager = null
            speechRecognizer?.destroy()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Dynamic activity level for responsive Matrix Digital Rain animation
    val isProcessing = subAgentState != null || isListening || isSentinelModeActive
    val dynamicActivityLevel = when {
        subAgentState != null -> 0.85f
        isListening -> 0.70f
        isSentinelModeActive -> 0.40f
        else -> 0.0f
    }

    // Matrix Digital Rain background layer across the entire UI
    MatrixDigitalRainBackground(
        alpha = 0.45f,
        isProcessing = isProcessing,
        activityLevel = dynamicActivityLevel
    ) {
        if (showMcpMenu) {
            McpServerManagementDialog(
                servers = mcpServers,
                catalogServers = catalogServers,
                tools = mcpTools,
                onDismiss = { viewModel.closeMcpMenu() },
                onInstallServer = { server -> viewModel.installMcpServer(server) },
                onUninstallServer = { id -> viewModel.uninstallMcpServer(id) },
                onToggleServer = { id, enabled -> viewModel.toggleMcpServer(id, enabled) },
                onAddCustomServer = { name, ep, tr, desc, auth -> viewModel.addCustomMcpServer(name, ep, tr, desc, auth) },
                onExecuteTool = { toolName -> viewModel.executeMcpTool(toolName) },
                onResetDefaults = { viewModel.resetMcpServersToDefaults() }
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isSentinelModeActive) "👁️ $charName [SENTINEL]" else "🕶️ $charName",
                            color = MatrixGreenPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    actions = {
                        if (isLowBattery) {
                            Text(
                                text = "🔋 Low Battery",
                                style = MaterialTheme.typography.bodySmall,
                                color = MatrixRedAlert,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        }
                        IconButton(onClick = { viewModel.toggleVoiceMode() }) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = "Voice Synthesizer",
                                tint = if (voiceModeActive) MatrixGreenPrimary else MatrixTextSecondary
                            )
                        }
                        IconButton(onClick = { viewModel.openMcpMenu() }) {
                            Icon(
                                Icons.Default.Hub,
                                contentDescription = "MCP Servers Menu",
                                tint = MatrixGreenPrimary
                            )
                        }
                        Text(
                            text = "$activeProvider",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixTextPrimary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More Menu Options",
                                tint = MatrixGreenPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier
                                .background(MatrixSurface)
                                .border(1.dp, MatrixBorder, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("🌐 MCP Servers Manager", color = MatrixGreenPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.openMcpMenu()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🧠 Thinking Frameworks", color = MatrixTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.sendMessage("/thinking")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚙️ Automated Logic", color = MatrixTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.sendMessage("/automation")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📞 Ghost Telecom Bridge", color = MatrixTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.sendMessage("/ghostcall")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🛠️ Device Tools", color = MatrixTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.sendMessage("/tools")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚙️ System Configuration", color = MatrixTextPrimary) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.sendMessage("/options")
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MatrixSurface.copy(alpha = 0.88f),
                        titleContentColor = MatrixGreenPrimary
                    )
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MatrixSurface.copy(alpha = 0.92f))
                ) {
                    if (isListening) {
                        VoiceWaveformVisualizer(
                            isListening = isListening,
                            liveTranscript = liveTranscript,
                            onStopListening = {
                                speechRecognizer?.stopListening()
                                viewModel.setListening(false)
                            }
                        )
                    }

                    if (subAgentState != null) {
                        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            SubAgentProgressCard(
                                subAgentName = subAgentState!!.first,
                                currentAction = subAgentState!!.second,
                                progress = subAgentState!!.third
                            )
                        }
                    }

                    QuickActionsRow(
                        onTriggerCallAgent = { viewModel.sendMessage("/call Metro Bistro reserve table for 2 at 7:30pm under Anderson") },
                        onOpenCallLogs = { viewModel.sendMessage("/calls") },
                        onOpenKnowledgeBase = { viewModel.sendMessage("/kb") },
                        onOpenUserGuide = { viewModel.sendMessage("/guide") },
                        onOpenSentinel = { viewModel.sendMessage("/sentinel") },
                        onOpenPreCognition = { viewModel.sendMessage("/precog") },
                        onOpenSubAgents = { viewModel.sendMessage("/subagents") },
                        onOpenMcpHub = { viewModel.sendMessage("/mcp") },
                        onOpenSlmBenchmarks = { viewModel.sendMessage("/slms") },
                        onTriggerSwarm = { viewModel.triggerSmithSwarm("Execute autonomous Matrix sub-routine audit and phone control") },
                        onTriggerDeepResearch = { viewModel.triggerDeepResearch("Matrix Autonomous Sub-Agent Protocols") },
                        onTriggerMultiApi = { viewModel.triggerMultiApi("Comparative analysis of free vs frontier AI models") },
                        onOpenMemoryVault = { viewModel.sendMessage("/memory") },
                        onOpenToolsMenu = { viewModel.sendMessage("/tools") },
                        onOpenThinkingMethods = { viewModel.sendMessage("/thinking") },
                        onOpenAutomatedSystems = { viewModel.sendMessage("/automation") },
                        onTriggerGhostCall = { viewModel.sendMessage("/ghostcall") }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    startSpeechListening(
                                        context = context,
                                        onSpeechStart = { viewModel.setListening(true) },
                                        onPartial = { transcript -> viewModel.updateLiveTranscript(transcript) },
                                        onResult = { resultText ->
                                            viewModel.setListening(false)
                                            if (resultText.isNotBlank()) {
                                                viewModel.sendMessage(resultText)
                                            }
                                        },
                                        onError = { viewModel.setListening(false) },
                                        onInit = { recognizer -> speechRecognizer = recognizer }
                                    )
                                } else {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Microphone Voice Input",
                                tint = if (isListening) MatrixRedAlert else MatrixGreenPrimary
                            )
                        }

                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = {
                                Text(
                                    if (isListening) "Acoustic input active..." else if (voiceModeActive) "Listening via voice channel..." else "Command Agent Smith or /sentinel, /guide, /call...",
                                    color = MatrixTextSecondary
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MatrixGreenPrimary,
                                unfocusedBorderColor = MatrixBorder,
                                focusedTextColor = MatrixTextPrimary,
                                unfocusedTextColor = MatrixTextPrimary
                            ),
                            maxLines = 3
                        )

                        IconButton(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    viewModel.sendMessage(inputMessage)
                                    inputMessage = ""
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MatrixGreenPrimary)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (!isAiCoreActive) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        AiCoreGateCard(
                            onActivateLocalAi = { viewModel.activateLocalAiCore() },
                            onSaveGeminiKey = { key -> viewModel.configureGeminiCore(key) },
                            onOpenAllProviders = { viewModel.sendMessage("/options") }
                        )
                    }
                } else if (profile == null || !profile!!.onboardingCompleted) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        OnboardingCard { name, email, phone ->
                            onboardingViewModel.saveProfile(name, email, phone)
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        items(messages) { message ->
                            when (message.type) {
                                "proactive_sentinel" -> ProactiveSentinelCard(
                                    isSentinelActive = isSentinelModeActive,
                                    isDeepSleepActive = isDeepSleepModeActive,
                                    isWakeWordEnabled = isWakeWordEnabled,
                                    customWakeWord = customWakeWord,
                                    wakeWordSensitivity = wakeWordSensitivity,
                                    ambientTranscript = ambientTranscript,
                                    pendingActions = pendingProactiveActions,
                                    onToggleSentinel = { enabled -> viewModel.toggleSentinelMode(enabled) },
                                    onToggleDeepSleep = { enabled -> viewModel.toggleDeepSleepMode(enabled) },
                                    onFlushBuffer = { viewModel.flushShortTermMemoryBuffer() },
                                    onUpdateWakeWordConfig = { enabled, phrase, sens -> viewModel.updateWakeWordConfig(enabled, phrase, sens) },
                                    onExecuteAction = { action -> viewModel.executeProactiveAction(action) },
                                    onDismissAction = { id -> viewModel.dismissProactiveAction(id) },
                                    onClearActions = { viewModel.clearAllProactiveActions() }
                                )
                                "pre_cognition" -> PreCognitionCard(
                                    predictions = preCognitionPredictions,
                                    onExecutePrediction = { pred -> viewModel.sendMessage(pred.executableCommand) },
                                    onDismissPrediction = { id -> }
                                )
                                "user_guide" -> UserGuideCard(
                                    onNavigateSection = { },
                                    onExecuteCommand = { cmd -> viewModel.sendMessage(cmd) }
                                )
                                "knowledge_base" -> KnowledgeBaseCard(
                                    knowledgeList = knowledgeList,
                                    onAddKnowledge = { title, content, cat, tags ->
                                        viewModel.addKnowledge(title, content, cat, tags)
                                    },
                                    onDeleteKnowledge = { id ->
                                        viewModel.deleteKnowledge(id)
                                    },
                                    onQueryKnowledge = { query ->
                                        viewModel.queryKnowledgeWithAI(query)
                                    }
                                )
                                "options" -> OptionsMenuCard(
                                    currentProvider = activeProvider,
                                    currentModel = activeModel,
                                    openaiKey = openaiKey,
                                    anthropicKey = anthropicKey,
                                    groqKey = groqKey,
                                    openrouterKey = openrouterKey,
                                    huggingfaceKey = hfKey,
                                    biometricLock = biometricLock,
                                    totalCost = totalCost,
                                    charName = charName,
                                    charDescription = charDescription,
                                    charPersonality = charPersonality,
                                    charScenario = charScenario,
                                    charFirstMessage = charFirstMessage,
                                    charMesExample = charMesExample,
                                    charTone = charTone,
                                    soundFxVolume = soundFxVolume,
                                    soundFxFrequency = soundFxFrequency,
                                    onSaveKeys = { gemini, groq, openrouter, hf, mistral, together, cohere, openai, anthropic ->
                                        viewModel.saveAllApiKeys(gemini, groq, openrouter, hf, mistral, together, cohere, openai, anthropic)
                                    },
                                    onSaveCharacter = { name, personality, tone ->
                                        viewModel.saveCharacterCard(name, personality, tone)
                                    },
                                    onSaveCharacterV2 = { name, desc, personality, scenario, firstMsg, mesEx, tone ->
                                        viewModel.saveCharacterCardV2(name, desc, personality, scenario, firstMsg, mesEx, tone)
                                    },
                                    onSaveSoundFx = { vol, freq ->
                                        viewModel.updateSoundFxConfig(vol, freq)
                                    },
                                    onTestSoundFx = { effectType ->
                                        viewModel.playSoundEffect(effectType, isCritical = true)
                                    },
                                    onToggleBiometric = { enabled ->
                                        viewModel.toggleBiometric(enabled)
                                    },
                                    onSelectProvider = { prov, mod ->
                                        viewModel.updatePreferences(prov, mod)
                                    }
                                )
                                "tasks" -> TaskManagerCard(tasks = tasks)
                                "memory" -> MatrixMemoryCard(
                                    memories = memories,
                                    onClearAll = { viewModel.clearAllMemories() }
                                )
                                "tools" -> MatrixToolsMenuCard(
                                    onExecuteTool = { cmd -> viewModel.sendMessage(cmd) }
                                )
                                "call_history" -> CallHistoryCard(
                                    callLogs = callLogs,
                                    onRedial = { phone -> viewModel.sendMessage("/call $phone") },
                                    onClearAll = { viewModel.clearCallLogs() }
                                )
                                "mcp_hub", "mcpservers", "mcp_servers" -> McpSkillsCard(
                                    servers = mcpServers,
                                    catalogServers = catalogServers,
                                    tools = mcpTools,
                                    persistedSkills = skills,
                                    onInstallServer = { server -> viewModel.installMcpServer(server) },
                                    onUninstallServer = { id -> viewModel.uninstallMcpServer(id) },
                                    onToggleServer = { id, enabled -> viewModel.toggleMcpServer(id, enabled) },
                                    onAddCustomServer = { name, ep, tr, desc, auth -> viewModel.addCustomMcpServer(name, ep, tr, desc, auth) },
                                    onResetDefaults = { viewModel.resetMcpServersToDefaults() },
                                    onSynthesizeSkill = { prompt -> viewModel.synthesizeMcpSkill(prompt) },
                                    onExecuteTool = { toolName -> viewModel.executeMcpTool(toolName) }
                                )
                                "slms_guide" -> LocalModelsGuideCard(
                                    onSelectModel = { modelName -> viewModel.updatePreferences("Local SLM", modelName) }
                                )
                                "thinking_methods", "cognitive_frameworks" -> com.example.agent.ui.chat.components.ThinkingMethodsCard(
                                    methods = thinkingMethods,
                                    activeMethodId = activeThinkingMethod,
                                    lastTrace = lastThinkingTrace,
                                    onSelectMethod = { methodId -> viewModel.selectThinkingMethod(methodId) },
                                    onExecuteTest = { methodId, query -> viewModel.executeThinkingTrace(methodId, query) },
                                    onCreateCustomMethod = { name, tag, desc, steps, prompt ->
                                        viewModel.createCustomThinkingMethod(name, tag, desc, steps, prompt)
                                    }
                                )
                                "automated_systems", "automation_hub" -> com.example.agent.ui.chat.components.AutomatedSystemsCard(
                                    routines = automatedRoutines,
                                    executionTraces = automatedExecutionTraces,
                                    onToggleRoutine = { id, enabled -> viewModel.toggleAutomatedRoutine(id, enabled) },
                                    onExecuteRoutine = { id -> viewModel.executeAutomatedRoutine(id) },
                                    onSynthesizeLogic = { prompt -> viewModel.synthesizeAutomatedLogic(prompt) }
                                )
                                "ghost_call", "telecom_bridge" -> com.example.agent.ui.chat.components.GhostCallCard(
                                    status = ghostCallStatus,
                                    onSendDtmf = { key -> viewModel.sendDtmfTone(key) },
                                    onLaunchNativeDialer = { phone -> viewModel.launchNativeDialer(phone) },
                                    onCancelCall = { viewModel.cancelGhostCall() },
                                    onRedial = { viewModel.redialGhostMission() }
                                )
                                "call_mission" -> {
                                    if (callMission != null) {
                                        PhoneCallAgentCard(
                                            mission = callMission!!,
                                            onCallNow = { phone ->
                                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                    viewModel.executePhoneCall(phone)
                                                } else {
                                                    callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                                                }
                                            },
                                            onLogToMemory = { record -> viewModel.saveCallToMemory(record) },
                                            onMarkCompleted = { }
                                        )
                                    } else {
                                        MessageCard(message = message)
                                    }
                                }
                                "subagent_orchestrator", "sub_agents", "topology" -> {
                                    SubAgentOrchestratorVisualizer(
                                        isMasterActive = true,
                                        onScaleSwarm = { size -> viewModel.triggerSmithSwarm("Scale active sub-agent swarm to $size nodes") },
                                        onTriggerTask = { taskDesc -> viewModel.sendMessage(taskDesc) },
                                        onToggleAgentState = { agentId -> viewModel.sendMessage("Toggle state for node $agentId") },
                                        onBoostPriority = { agentId -> viewModel.playSoundEffect("PRIORITY_BOOST", isCritical = true) }
                                    )
                                }
                                "swarm_card" -> {
                                    if (swarmState != null) {
                                        SmithSwarmCard(
                                            swarmTask = swarmState!!.first,
                                            replicas = swarmState!!.second
                                        )
                                    } else {
                                        MessageCard(message = message)
                                    }
                                }
                                "delegation_tree" -> {
                                    if (delegationTree != null) {
                                        TaskDelegationTreeCard(
                                            parentTask = delegationTree!!.first,
                                            subTasks = delegationTree!!.second
                                        )
                                    } else {
                                        MessageCard(message = message)
                                    }
                                }
                                else -> MessageCard(message = message)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun startSpeechListening(
    context: android.content.Context,
    onSpeechStart: () -> Unit,
    onPartial: (String) -> Unit,
    onResult: (String) -> Unit,
    onError: () -> Unit,
    onInit: (SpeechRecognizer) -> Unit
) {
    try {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError()
            return
        }
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        onInit(recognizer)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                onSpeechStart()
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                onError()
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                onResult(text)
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    onPartial(text)
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    } catch (e: Exception) {
        onError()
    }
}
