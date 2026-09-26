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
import com.example.agent.data.remote.ChatRequest
import com.example.agent.data.remote.Content
import com.example.agent.data.remote.GeminiApiService
import com.example.agent.data.remote.Part
import kotlinx.coroutines.flow.Flow

class AgentRepository(context: Context) {
    private val database = AgentDatabase.getDatabase(context)
    private val messageDao = database.messageDao()
    private val profileDao = database.profileDao()
    private val taskDao = database.taskDao()
    private val callLogDao = database.callLogDao()
    private val skillDao = database.skillDao()
    private val knowledgeDao = database.knowledgeDao()
    private val proactiveActionDao = database.proactiveActionDao()
    val preferencesManager = PreferencesManager(context)

    val messages: Flow<List<MessageEntity>> = messageDao.getAllMessages()
    val profile: Flow<ProfileEntity?> = profileDao.getProfile()
    val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val callLogs: Flow<List<CallLogEntity>> = callLogDao.getAllCallLogs()
    val skills: Flow<List<SkillEntity>> = skillDao.getAllSkills()
    val knowledgeList: Flow<List<KnowledgeEntity>> = knowledgeDao.getAllKnowledge()
    val proactiveActions: Flow<List<ProactiveActionEntity>> = proactiveActionDao.getAllProactiveActions()
    val pendingProactiveActions: Flow<List<ProactiveActionEntity>> = proactiveActionDao.getPendingProactiveActions()

    suspend fun insertMessage(message: MessageEntity) = messageDao.insertMessage(message)
    suspend fun clearMessages() = messageDao.clearAllMessages()

    suspend fun updateProfile(profile: ProfileEntity) = profileDao.insertOrUpdateProfile(profile)
    suspend fun getProfileSync() = profileDao.getProfileSync()

    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun updateTaskStatus(taskId: Long, status: String) = taskDao.updateTaskStatus(taskId, status)

    suspend fun insertCallLog(call: CallLogEntity) = callLogDao.insertCallLog(call)
    suspend fun clearCallLogs() = callLogDao.clearCallLogs()

    suspend fun insertSkill(skill: SkillEntity) = skillDao.insertSkill(skill)
    suspend fun clearSkills() = skillDao.clearSkills()

    // Knowledge Base APIs
    suspend fun insertKnowledge(item: KnowledgeEntity): Long = knowledgeDao.insertKnowledge(item)
    suspend fun insertKnowledgeBatch(items: List<KnowledgeEntity>) = knowledgeDao.insertAll(items)
    suspend fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>> = knowledgeDao.searchKnowledge(query)
    suspend fun findRelevantKnowledge(query: String, limit: Int = 3): List<KnowledgeEntity> =
        knowledgeDao.findRelevantKnowledge(query, limit)
    suspend fun deleteKnowledge(item: KnowledgeEntity) = knowledgeDao.deleteKnowledge(item)
    suspend fun deleteKnowledgeById(id: Long) = knowledgeDao.deleteById(id)
    suspend fun clearKnowledge() = knowledgeDao.clearAllKnowledge()
    suspend fun getKnowledgeCount(): Int = knowledgeDao.getKnowledgeCount()

    // Proactive Actions APIs
    suspend fun insertProactiveAction(action: ProactiveActionEntity): Long = proactiveActionDao.insertAction(action)
    suspend fun updateProactiveStatus(id: Long, status: String) = proactiveActionDao.updateStatus(id, status)
    suspend fun clearProactiveActions() = proactiveActionDao.clearAll()
    suspend fun getPendingProactiveCount(): Int = proactiveActionDao.getPendingCount()

    suspend fun callGemini(prompt: String, model: String = "gemini-2.5-flash"): String {
        val apiKey = try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: "AIzaSyPlaceholderKey"
        } catch (e: Exception) {
            "AIzaSyPlaceholderKey"
        }.ifEmpty { "AIzaSyPlaceholderKey" }

        val request = ChatRequest(listOf(Content(listOf(Part(prompt)))))
        val response = GeminiApiService.api.generateContent(model, apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "I'm sorry, I couldn't generate a response."
    }
}
