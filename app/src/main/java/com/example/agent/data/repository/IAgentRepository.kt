package com.example.agent.data.repository

import com.example.agent.data.datastore.PreferencesManager
import com.example.agent.data.local.entity.CallLogEntity
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.local.entity.ProactiveActionEntity
import com.example.agent.data.local.entity.ProfileEntity
import com.example.agent.data.local.entity.SkillEntity
import com.example.agent.data.local.entity.TaskEntity
import com.example.agent.domain.model.LlmResult
import kotlinx.coroutines.flow.Flow

interface IAgentRepository {
    val preferencesManager: PreferencesManager
    val messages: Flow<List<MessageEntity>>
    val profile: Flow<ProfileEntity?>
    val tasks: Flow<List<TaskEntity>>
    val callLogs: Flow<List<CallLogEntity>>
    val skills: Flow<List<SkillEntity>>
    val knowledgeList: Flow<List<KnowledgeEntity>>
    val proactiveActions: Flow<List<ProactiveActionEntity>>
    val pendingProactiveActions: Flow<List<ProactiveActionEntity>>

    suspend fun insertMessage(message: MessageEntity): Long
    suspend fun clearMessages()
    suspend fun updateProfile(profile: ProfileEntity)
    suspend fun getProfileSync(): ProfileEntity?
    suspend fun insertTask(task: TaskEntity): Long
    suspend fun deleteTask(task: TaskEntity)
    suspend fun updateTaskStatus(taskId: Long, status: String)
    suspend fun insertCallLog(call: CallLogEntity): Long
    suspend fun clearCallLogs()
    suspend fun insertSkill(skill: SkillEntity): Long
    suspend fun clearSkills()
    suspend fun insertKnowledge(item: KnowledgeEntity): Long
    suspend fun insertKnowledgeBatch(items: List<KnowledgeEntity>)
    suspend fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>>
    suspend fun findRelevantKnowledge(query: String, limit: Int = 3): List<KnowledgeEntity>
    suspend fun deleteKnowledge(item: KnowledgeEntity)
    suspend fun deleteKnowledgeById(id: Long)
    suspend fun clearKnowledge()
    suspend fun getKnowledgeCount(): Int
    suspend fun insertProactiveAction(action: ProactiveActionEntity): Long
    suspend fun updateProactiveStatus(id: Long, status: String)
    suspend fun clearProactiveActions()
    suspend fun getPendingProactiveCount(): Int
    suspend fun pruneOldData(retentionDays: Int)
    suspend fun callGemini(prompt: String, model: String = "gemini-2.5-flash"): String
    suspend fun callGeminiWithResult(prompt: String, model: String = "gemini-2.5-flash", systemInstruction: String? = null): LlmResult
}
