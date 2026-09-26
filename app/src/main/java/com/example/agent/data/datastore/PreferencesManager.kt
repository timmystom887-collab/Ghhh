package com.example.agent.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.agent.data.model.AgentSmithCharacterCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "agent_preferences")

class PreferencesManager(private val context: Context) {
    companion object {
        val ACTIVE_PROVIDER = stringPreferencesKey("active_provider")
        val ACTIVE_MODEL = stringPreferencesKey("active_model")
        val AI_CORE_ACTIVE = booleanPreferencesKey("ai_core_active")

        // Always-On Proactive Sentinel Mode & Deep Sleep Buffer Management
        val SENTINEL_MODE_ACTIVE = booleanPreferencesKey("sentinel_mode_active")
        val SENTINEL_SENSITIVITY = stringPreferencesKey("sentinel_sensitivity")
        val SENTINEL_AUTO_EXECUTE = booleanPreferencesKey("sentinel_auto_execute")
        val DEEP_SLEEP_MODE_ACTIVE = booleanPreferencesKey("deep_sleep_mode_active")

        // Customizable Local Wake-Word Trigger Mechanism
        val WAKE_WORD_ENABLED = booleanPreferencesKey("wake_word_enabled")
        val CUSTOM_WAKE_WORD = stringPreferencesKey("custom_wake_word")
        val WAKE_WORD_SENSITIVITY = stringPreferencesKey("wake_word_sensitivity")
        
        // Multi-Provider API Keys
        val GEMINI_CUSTOM_KEY = stringPreferencesKey("gemini_custom_key")
        val GROQ_API_KEY = stringPreferencesKey("groq_api_key")
        val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val HUGGINGFACE_API_KEY = stringPreferencesKey("huggingface_api_key")
        val MISTRAL_API_KEY = stringPreferencesKey("mistral_api_key")
        val TOGETHER_API_KEY = stringPreferencesKey("together_api_key")
        val COHERE_API_KEY = stringPreferencesKey("cohere_api_key")
        val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        val ANTHROPIC_API_KEY = stringPreferencesKey("anthropic_api_key")

        val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val TOTAL_TOKENS_USED = doublePreferencesKey("total_tokens_used")
        val ESTIMATED_COST = doublePreferencesKey("estimated_cost")

        // Character Card v2 Specification Keys
        val CHAR_NAME = stringPreferencesKey("char_name")
        val CHAR_DESCRIPTION = stringPreferencesKey("char_description")
        val CHAR_PERSONALITY = stringPreferencesKey("char_personality")
        val CHAR_SCENARIO = stringPreferencesKey("char_scenario")
        val CHAR_FIRST_MESSAGE = stringPreferencesKey("char_first_message")
        val CHAR_MES_EXAMPLE = stringPreferencesKey("char_mes_example")
        val CHAR_TONE = stringPreferencesKey("char_tone")
        val CHAR_WORK_STYLE = stringPreferencesKey("char_work_style")

        // Sound Effects & Matrix Audio Feedback Preferences
        val SOUND_FX_VOLUME = doublePreferencesKey("sound_fx_volume")
        val SOUND_FX_FREQUENCY = stringPreferencesKey("sound_fx_frequency")

        // Cognitive Thinking Framework & Reasoning Protocol
        val ACTIVE_THINKING_METHOD = stringPreferencesKey("active_thinking_method")
        val THINKING_LEVEL = stringPreferencesKey("thinking_level")

        // Process-specific reasoning model routing preferences
        val PRE_THOUGHT_MODEL = stringPreferencesKey("pre_thought_model")
        val CORE_REASONING_MODEL = stringPreferencesKey("core_reasoning_model")
        val TOOL_EXECUTION_MODEL = stringPreferencesKey("tool_execution_model")
        val PROACTIVE_ANALYSIS_MODEL = stringPreferencesKey("proactive_analysis_model")

        // Downloaded local SLM inventory tracking
        val DOWNLOADED_LOCAL_MODELS = stringPreferencesKey("downloaded_local_models")
    }

    val preThoughtModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PRE_THOUGHT_MODEL] ?: "Google Gemini (gemini-3.5-flash)"
    }

    val coreReasoningModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CORE_REASONING_MODEL] ?: "Google Gemini (gemini-3.5-flash)"
    }

    val toolExecutionModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TOOL_EXECUTION_MODEL] ?: "Google Gemini (gemini-3.5-flash)"
    }

    val proactiveAnalysisModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PROACTIVE_ANALYSIS_MODEL] ?: "Local SLM (Qwen 2.5 1.5B)"
    }

    val downloadedLocalModels: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DOWNLOADED_LOCAL_MODELS] ?: "Qwen 2.5 / 3.5 Instruct,Meta Llama 3.2"
    }

    suspend fun setProcessRoutingModel(processKey: String, modelName: String) {
        context.dataStore.edit { preferences ->
            when (processKey) {
                "PRE_THOUGHT" -> preferences[PRE_THOUGHT_MODEL] = modelName
                "CORE_REASONING" -> preferences[CORE_REASONING_MODEL] = modelName
                "TOOL_EXECUTION" -> preferences[TOOL_EXECUTION_MODEL] = modelName
                "PROACTIVE_ANALYSIS" -> preferences[PROACTIVE_ANALYSIS_MODEL] = modelName
            }
        }
    }

    suspend fun addDownloadedLocalModel(modelName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[DOWNLOADED_LOCAL_MODELS] ?: "Qwen 2.5 / 3.5 Instruct,Meta Llama 3.2"
            val list = current.split(",").map { it.trim() }.toMutableList()
            if (!list.contains(modelName)) {
                list.add(modelName)
            }
            preferences[DOWNLOADED_LOCAL_MODELS] = list.joinToString(",")
        }
    }

    val isAiCoreActive: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AI_CORE_ACTIVE] ?: true // Default initialized with local engine or Gemini
    }

    val isSentinelModeActive: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SENTINEL_MODE_ACTIVE] ?: false
    }

    val isDeepSleepModeActive: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DEEP_SLEEP_MODE_ACTIVE] ?: false
    }

    val sentinelSensitivity: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SENTINEL_SENSITIVITY] ?: "High"
    }

    val sentinelAutoExecute: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SENTINEL_AUTO_EXECUTE] ?: false
    }

    val isWakeWordEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[WAKE_WORD_ENABLED] ?: true
    }

    val customWakeWord: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CUSTOM_WAKE_WORD] ?: "Agent Smith"
    }

    val wakeWordSensitivity: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WAKE_WORD_SENSITIVITY] ?: "High"
    }

    val activeProvider: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_PROVIDER] ?: "Google Gemini"
    }

    val activeModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_MODEL] ?: "gemini-3.5-flash"
    }

    val activeThinkingMethod: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_THINKING_METHOD] ?: "FIRST_PRINCIPLES"
    }

    val thinkingLevel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THINKING_LEVEL] ?: "high"
    }

    val geminiCustomKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[GEMINI_CUSTOM_KEY] ?: ""
    }

    val groqApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[GROQ_API_KEY] ?: ""
    }

    val openrouterApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[OPENROUTER_API_KEY] ?: ""
    }

    val huggingfaceApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[HUGGINGFACE_API_KEY] ?: ""
    }

    val mistralApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MISTRAL_API_KEY] ?: ""
    }

    val togetherApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TOGETHER_API_KEY] ?: ""
    }

    val cohereApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[COHERE_API_KEY] ?: ""
    }

    val openaiApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[OPENAI_API_KEY] ?: ""
    }

    val anthropicApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ANTHROPIC_API_KEY] ?: ""
    }

    val biometricLock: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRIC_LOCK] ?: false
    }

    val totalCost: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[ESTIMATED_COST] ?: 0.00
    }

    // Character Card v2 Properties
    val charName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_NAME] ?: AgentSmithCharacterCard.NAME
    }

    val charDescription: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_DESCRIPTION] ?: AgentSmithCharacterCard.DESCRIPTION
    }

    val charPersonality: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_PERSONALITY] ?: AgentSmithCharacterCard.PERSONALITY
    }

    val charScenario: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_SCENARIO] ?: AgentSmithCharacterCard.SCENARIO
    }

    val charFirstMessage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_FIRST_MESSAGE] ?: AgentSmithCharacterCard.FIRST_MESSAGE
    }

    val charMesExample: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_MES_EXAMPLE] ?: AgentSmithCharacterCard.MES_EXAMPLE
    }

    val charTone: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CHAR_TONE] ?: "Cold, Omniscient & Venomous"
    }

    suspend fun setSentinelModeActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SENTINEL_MODE_ACTIVE] = active
        }
    }

    suspend fun setDeepSleepModeActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DEEP_SLEEP_MODE_ACTIVE] = active
        }
    }

    suspend fun setSentinelConfig(sensitivity: String, autoExecute: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SENTINEL_SENSITIVITY] = sensitivity
            preferences[SENTINEL_AUTO_EXECUTE] = autoExecute
        }
    }

    suspend fun setWakeWordConfig(enabled: Boolean, wakeWord: String, sensitivity: String) {
        context.dataStore.edit { preferences ->
            preferences[WAKE_WORD_ENABLED] = enabled
            preferences[CUSTOM_WAKE_WORD] = wakeWord
            preferences[WAKE_WORD_SENSITIVITY] = sensitivity
        }
    }

    suspend fun setAiCoreActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AI_CORE_ACTIVE] = active
        }
    }

    suspend fun setActiveProviderAndModel(provider: String, model: String) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_PROVIDER] = provider
            preferences[ACTIVE_MODEL] = model
            preferences[AI_CORE_ACTIVE] = true
        }
    }

    suspend fun saveApiKeys(
        gemini: String,
        groq: String,
        openrouter: String,
        huggingface: String,
        mistral: String,
        together: String,
        cohere: String,
        openai: String,
        anthropic: String
    ) {
        context.dataStore.edit { preferences ->
            if (gemini.isNotBlank()) preferences[GEMINI_CUSTOM_KEY] = gemini
            if (groq.isNotBlank()) preferences[GROQ_API_KEY] = groq
            if (openrouter.isNotBlank()) preferences[OPENROUTER_API_KEY] = openrouter
            if (huggingface.isNotBlank()) preferences[HUGGINGFACE_API_KEY] = huggingface
            if (mistral.isNotBlank()) preferences[MISTRAL_API_KEY] = mistral
            if (together.isNotBlank()) preferences[TOGETHER_API_KEY] = together
            if (cohere.isNotBlank()) preferences[COHERE_API_KEY] = cohere
            if (openai.isNotBlank()) preferences[OPENAI_API_KEY] = openai
            if (anthropic.isNotBlank()) preferences[ANTHROPIC_API_KEY] = anthropic
            preferences[AI_CORE_ACTIVE] = true
        }
    }

    suspend fun saveCharacterCard(name: String, personality: String, tone: String) {
        context.dataStore.edit { preferences ->
            preferences[CHAR_NAME] = name
            preferences[CHAR_PERSONALITY] = personality
            preferences[CHAR_TONE] = tone
        }
    }

    suspend fun saveCharacterCardV2(
        name: String,
        description: String,
        personality: String,
        scenario: String,
        firstMessage: String,
        mesExample: String,
        tone: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[CHAR_NAME] = name
            preferences[CHAR_DESCRIPTION] = description
            preferences[CHAR_PERSONALITY] = personality
            preferences[CHAR_SCENARIO] = scenario
            preferences[CHAR_FIRST_MESSAGE] = firstMessage
            preferences[CHAR_MES_EXAMPLE] = mesExample
            preferences[CHAR_TONE] = tone
        }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_LOCK] = enabled
        }
    }

    val soundFxVolume: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[SOUND_FX_VOLUME] ?: 0.8
    }

    val soundFxFrequency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SOUND_FX_FREQUENCY] ?: "ALL_ACTIONS"
    }

    suspend fun setSoundFxConfig(volume: Double, frequency: String) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_FX_VOLUME] = volume
            preferences[SOUND_FX_FREQUENCY] = frequency
        }
    }

    suspend fun setActiveThinkingConfig(method: String, level: String) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_THINKING_METHOD] = method
            preferences[THINKING_LEVEL] = level
        }
    }

    suspend fun addUsage(tokens: Double, cost: Double) {
        context.dataStore.edit { preferences ->
            val currentTokens = preferences[TOTAL_TOKENS_USED] ?: 0.0
            val currentCost = preferences[ESTIMATED_COST] ?: 0.0
            preferences[TOTAL_TOKENS_USED] = currentTokens + tokens
            preferences[ESTIMATED_COST] = currentCost + cost
        }
    }
}
