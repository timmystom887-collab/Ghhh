package com.example.agent.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.agent.data.local.dao.CallLogDao
import com.example.agent.data.local.dao.KnowledgeDao
import com.example.agent.data.local.dao.MemoryDao
import com.example.agent.data.local.dao.MessageDao
import com.example.agent.data.local.dao.ProactiveActionDao
import com.example.agent.data.local.dao.ProfileDao
import com.example.agent.data.local.dao.SkillDao
import com.example.agent.data.local.dao.TaskDao
import com.example.agent.data.local.entity.CallLogEntity
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MemoryEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.local.entity.ProactiveActionEntity
import com.example.agent.data.local.entity.ProfileEntity
import com.example.agent.data.local.entity.SkillEntity
import com.example.agent.data.local.entity.TaskEntity

@Database(
    entities = [
        MessageEntity::class,
        ProfileEntity::class,
        TaskEntity::class,
        MemoryEntity::class,
        CallLogEntity::class,
        SkillEntity::class,
        KnowledgeEntity::class,
        ProactiveActionEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AgentDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun profileDao(): ProfileDao
    abstract fun taskDao(): TaskDao
    abstract fun memoryDao(): MemoryDao
    abstract fun callLogDao(): CallLogDao
    abstract fun skillDao(): SkillDao
    abstract fun knowledgeDao(): KnowledgeDao
    abstract fun proactiveActionDao(): ProactiveActionDao

    companion object {
        @Volatile
        private var INSTANCE: AgentDatabase? = null

        fun getDatabase(context: Context): AgentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgentDatabase::class.java,
                    "agent_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
