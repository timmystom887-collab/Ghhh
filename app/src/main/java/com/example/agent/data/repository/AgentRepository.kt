package com.example.agent.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.agent.data.datastore.PreferencesManager
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.local.entity.CallLogEntity
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.local.entity.ProactiveActionEntity
import com.example.agent.data.local.entity.ProfileEntity
import com.example.agent.data.local.entity.SkillEntity
import com.example.agent.data.local.entity.TaskEntity
import com.example.agent.domain.client.GeminiProviderClient
import com.example.agent.domain.client.ILlmProviderClient
import com.example.agent.domain.model.LlmResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AgentRepository(
    context: Context,
    private val database: AgentDatabase = AgentDatabase.getDatabase(context),
    override val preferencesManager: PreferencesManager = PreferencesManager(context),
    private val llmClient: ILlmProviderClient = GeminiProviderClient()
) : IAgentRepository {

    private val messageDao = database.messageDao()
    private val profileDao = database.profileDao()
    private val taskDao = database.taskDao()
    private val callLogDao = database.callLogDao()
    private val skillDao = database.skillDao()
    private val knowledgeDao = database.knowledgeDao()
    private val proactiveActionDao = database.proactiveActionDao()

    override val messages: Flow<List<MessageEntity>> = messageDao.getAllMessages()
    override val profile: Flow<ProfileEntity?> = profileDao.getProfile()
    override val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    override val callLogs: Flow<List<CallLogEntity>> = callLogDao.getAllCallLogs()
    override val skills: Flow<List<SkillEntity>> = skillDao.getAllSkills()
    override val knowledgeList: Flow<List<KnowledgeEntity>> = knowledgeDao.getAllKnowledge()
    override val proactiveActions: Flow<List<ProactiveActionEntity>> = proactiveActionDao.getAllProactiveActions()
    override val pendingProactiveActions: Flow<List<ProactiveActionEntity>> = proactiveActionDao.getPendingProactiveActions()

    override suspend fun insertMessage(message: MessageEntity): Long = messageDao.insertMessage(message)
    override suspend fun clearMessages() = messageDao.clearAllMessages()

    override suspend fun updateProfile(profile: ProfileEntity) = profileDao.insertOrUpdateProfile(profile)
    override suspend fun getProfileSync(): ProfileEntity? = profileDao.getProfileSync()

    override suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    override suspend fun updateTaskStatus(taskId: Long, status: String) = taskDao.updateTaskStatus(taskId, status)

    override suspend fun insertCallLog(call: CallLogEntity): Long = callLogDao.insertCallLog(call)
    override suspend fun clearCallLogs() = callLogDao.clearCallLogs()

    override suspend fun insertSkill(skill: SkillEntity): Long = skillDao.insertSkill(skill)
    override suspend fun clearSkills() = skillDao.clearSkills()

    // Knowledge Base APIs
    override suspend fun insertKnowledge(item: KnowledgeEntity): Long = knowledgeDao.insertKnowledge(item)
    override suspend fun insertKnowledgeBatch(items: List<KnowledgeEntity>) = knowledgeDao.insertAll(items)
    override suspend fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>> = knowledgeDao.searchKnowledge(query)
    override suspend fun findRelevantKnowledge(query: String, limit: Int): List<KnowledgeEntity> =
        knowledgeDao.findRelevantKnowledge(query, limit)
    override suspend fun deleteKnowledge(item: KnowledgeEntity) = knowledgeDao.deleteKnowledge(item)
    override suspend fun deleteKnowledgeById(id: Long) = knowledgeDao.deleteById(id)
    override suspend fun clearKnowledge() = knowledgeDao.clearAllKnowledge()
    override suspend fun getKnowledgeCount(): Int = knowledgeDao.getKnowledgeCount()

    // Proactive Actions APIs
    override suspend fun insertProactiveAction(action: ProactiveActionEntity): Long = proactiveActionDao.insertAction(action)
    override suspend fun updateProactiveStatus(id: Long, status: String) = proactiveActionDao.updateStatus(id, status)
    override suspend fun clearProactiveActions() = proactiveActionDao.clearAll()
    override suspend fun getPendingProactiveCount(): Int = proactiveActionDao.getPendingCount()

    override suspend fun pruneOldData(retentionDays: Int) {
        if (retentionDays <= 0) return
        val cutoffTimestamp = System.currentTimeMillis() - (retentionDays * 24L * 60L * 60L * 1000L)
        // Prune older proactive actions or logs
    }

    private fun resolveApiKey(): String {
        val customKey = preferencesManager.getSecureApiKey("gemini")
        if (customKey.isNotBlank()) return customKey

        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String
            key?.ifEmpty { "AIzaSyPlaceholderKey" } ?: "AIzaSyPlaceholderKey"
        } catch (e: Exception) {
            "AIzaSyPlaceholderKey"
        }
    }

    override suspend fun callGemini(prompt: String, model: String): String {
        val result = callGeminiWithResult(prompt, model)
        return when (result) {
            is LlmResult.Success -> result.text
            is LlmResult.Error -> "Error: ${result.message}"
        }
    }

    override suspend fun callGeminiWithResult(
        prompt: String,
        model: String,
        systemInstruction: String?
    ): LlmResult {
        val apiKey = resolveApiKey()
        val thinkingLevel = preferencesManager.thinkingLevel.first()
        return llmClient.generateResponse(
            prompt = prompt,
            systemInstruction = systemInstruction,
            model = model,
            apiKey = apiKey,
            thinkingLevel = thinkingLevel
        )
    }
}
