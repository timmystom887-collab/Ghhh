package com.example.agent.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

        // Explicit Non-Destructive Room Migrations
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `memories` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `category` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `call_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `phoneNumber` TEXT NOT NULL,
                        `contactName` TEXT NOT NULL,
                        `direction` TEXT NOT NULL,
                        `callType` TEXT NOT NULL,
                        `objective` TEXT NOT NULL,
                        `summary` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `status` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `skills` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `skillName` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `triggerKeywords` TEXT NOT NULL,
                        `parametersSchema` TEXT NOT NULL,
                        `executionLogic` TEXT NOT NULL,
                        `isAutoCreated` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `usageCount` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `knowledge_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `tags` TEXT NOT NULL,
                        `sourceUri` TEXT NOT NULL,
                        `docType` TEXT NOT NULL,
                        `summary` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `isPinned` INTEGER NOT NULL,
                        `accessCount` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `proactive_actions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `triggerPhrase` TEXT NOT NULL,
                        `correlatedContext` TEXT NOT NULL,
                        `suggestedTitle` TEXT NOT NULL,
                        `suggestedExplanation` TEXT NOT NULL,
                        `actionPayload` TEXT NOT NULL,
                        `actionType` TEXT NOT NULL,
                        `confidenceScore` REAL NOT NULL,
                        `status` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val ALL_MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5
        )

        fun getDatabase(context: Context): AgentDatabase {
            val currentInstance = INSTANCE
            if (currentInstance != null && currentInstance.isOpen) {
                return currentInstance
            }
            return synchronized(this) {
                val currentInstance2 = INSTANCE
                if (currentInstance2 != null && currentInstance2.isOpen) {
                    currentInstance2
                } else {
                    val isUnderTest = try {
                        Thread.currentThread().stackTrace.any {
                            it.className.contains("org.junit") || it.className.contains("robolectric")
                        }
                    } catch (e: Exception) {
                        false
                    }

                    val instance = if (isUnderTest) {
                        Room.inMemoryDatabaseBuilder(
                            context.applicationContext,
                            AgentDatabase::class.java
                        ).allowMainThreadQueries()
                         .addMigrations(*ALL_MIGRATIONS)
                         .build()
                    } else {
                        Room.databaseBuilder(
                            context.applicationContext,
                            AgentDatabase::class.java,
                            "agent_database"
                        )
                         .addMigrations(*ALL_MIGRATIONS)
                         .build()
                    }
                    INSTANCE = instance
                    instance
                }
            }
        }

        fun resetDatabaseForTesting() {
            synchronized(this) {
                INSTANCE?.let {
                    if (it.isOpen) {
                        it.close()
                    }
                }
                INSTANCE = null
            }
        }
    }
}
