package com.example.agent

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MemoryEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.local.entity.ProactiveActionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RoomMigrationTest {

    private lateinit var database: AgentDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, AgentDatabase::class.java)
            .addMigrations(*AgentDatabase.ALL_MIGRATIONS)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testDatabaseTablesAndEntities() = runBlocking {
        // Test message dao
        val message = MessageEntity(
            sender = "user",
            content = "Test migration persistence",
            timestamp = 1000L,
            type = "text"
        )
        val msgId = database.messageDao().insertMessage(message)
        assertNotNull(msgId)

        val messages = database.messageDao().getAllMessages().first()
        assertEquals(1, messages.size)
        assertEquals("Test migration persistence", messages[0].content)

        // Test memory entity (introduced in Migration 1->2)
        val memory = MemoryEntity(
            category = "work",
            content = "Secret neural node location",
            timestamp = 2000L
        )
        val memId = database.memoryDao().insertMemory(memory)
        assertNotNull(memId)

        val memories = database.memoryDao().getAllMemories().first()
        assertEquals(1, memories.size)
        assertEquals("Secret neural node location", memories[0].content)

        // Test knowledge entity (introduced in Migration 3->4)
        val knowledge = KnowledgeEntity(
            title = "Protocol Alpha",
            content = "Direct system call execution",
            category = "PROTOCOLS"
        )
        val kId = database.knowledgeDao().insertKnowledge(knowledge)
        assertNotNull(kId)

        val retrievedKnowledge = database.knowledgeDao().getAllKnowledge().first()
        assertEquals(1, retrievedKnowledge.size)
        assertEquals("Protocol Alpha", retrievedKnowledge[0].title)

        // Test proactive actions (introduced in Migration 4->5)
        val action = ProactiveActionEntity(
            triggerPhrase = "book dinner",
            correlatedContext = "user loves steak",
            suggestedTitle = "Call Steakhouse",
            suggestedExplanation = "Detected restaurant intent",
            actionPayload = "CALL:+15550199"
        )
        val aId = database.proactiveActionDao().insertAction(action)
        assertNotNull(aId)

        val actions = database.proactiveActionDao().getAllProactiveActions().first()
        assertEquals(1, actions.size)
        assertEquals("Call Steakhouse", actions[0].suggestedTitle)
    }
}
