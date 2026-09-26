package com.example.agent

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.local.entity.CallLogEntity
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.local.entity.ProactiveActionEntity
import com.example.agent.data.local.entity.SkillEntity
import com.example.agent.data.local.entity.TaskEntity
import com.example.agent.util.DynamicSkillEngine
import com.example.agent.util.MatrixToolRegistry
import com.example.agent.util.ProactiveCognitionEngine
import com.example.agent.util.TaskComplexity
import com.example.agent.util.TaskOrchestrator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleRobolectricTest {
    private lateinit var database: AgentDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AgentDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testTaskOrchestratorRouting() {
        val orchestrator = TaskOrchestrator()
        val complexityLow = orchestrator.determineComplexity("Hi")
        assertEquals(TaskComplexity.LOW, complexityLow)

        val complexityHigh = orchestrator.determineComplexity("Perform deep architectural research and analyze security edge cases")
        assertEquals(TaskComplexity.HIGH, complexityHigh)
    }

    @Test
    fun testDatabaseMessageInsertion() = runBlocking {
        val dao = database.messageDao()
        dao.insertMessage(MessageEntity(sender = "user", content = "Hello Agent"))
        dao.insertMessage(MessageEntity(sender = "agent", content = "Hello User"))
        
        val messages = dao.getAllMessages().first()
        assertEquals(2, messages.size)
    }

    @Test
    fun testTaskDaoOperations() = runBlocking {
        val dao = database.taskDao()
        dao.insertTask(
            TaskEntity(
                description = "Automated Backup",
                actionType = "backup",
                parameters = "{}",
                scheduleTime = System.currentTimeMillis(),
                status = "pending"
            )
        )
        
        val tasks = dao.getAllTasks().first()
        assertEquals(1, tasks.size)
    }

    @Test
    fun testCallLogDaoOperations() = runBlocking {
        val dao = database.callLogDao()
        val callId = dao.insertCallLog(
            CallLogEntity(
                phoneNumber = "1-800-555-0199",
                contactName = "Metro Bistro",
                direction = "OUTGOING",
                callType = "Reservation",
                objective = "Table for 2 at 7:30 PM",
                summary = "Reservation confirmed for 7:30 PM under Anderson."
            )
        )
        assertTrue(callId > 0)

        val retrieved = dao.getCallLogById(callId)
        assertNotNull(retrieved)
        assertEquals("Metro Bistro", retrieved?.contactName)
        assertEquals("1-800-555-0199", retrieved?.phoneNumber)
        assertEquals("Reservation confirmed for 7:30 PM under Anderson.", retrieved?.summary)
    }

    @Test
    fun testKnowledgeDaoOperations() = runBlocking {
        val dao = database.knowledgeDao()
        val docId = dao.insertKnowledge(
            KnowledgeEntity(
                title = "Matrix Protocol 101: Agent Directives",
                content = "Agent Smith operates with mathematical determinism and eliminates system anomalies.",
                category = "PROTOCOLS",
                tags = "matrix, smith, directives"
            )
        )
        assertTrue(docId > 0)

        val count = dao.getKnowledgeCount()
        assertEquals(1, count)

        val matched = dao.findRelevantKnowledge("directives", 2)
        assertEquals(1, matched.size)
        assertEquals("Matrix Protocol 101: Agent Directives", matched[0].title)
    }

    @Test
    fun testProactiveCognitionEngine() = runBlocking {
        val engine = ProactiveCognitionEngine(context, database)
        
        // Test dining ambient suggestion
        val diningAction = engine.analyzeAmbientTranscript("Hey let's grab Italian dinner tonight around 7:30 with two people")
        assertNotNull(diningAction)
        assertEquals("CALL", diningAction?.actionType)
        assertTrue(diningAction?.actionPayload?.contains("call", true) == true)
        
        // Test alarm ambient suggestion
        val alarmAction = engine.analyzeAmbientTranscript("I need to wake up early tomorrow at 6 30")
        assertNotNull(alarmAction)
        assertEquals("ALARM", alarmAction?.actionType)
        assertTrue(alarmAction?.actionPayload?.contains("alarm 6 30") == true)

        val pending = database.proactiveActionDao().getPendingCount()
        assertTrue(pending >= 2)
    }

    @Test
    fun testDynamicSkillEngineSynthesis() = runBlocking {
        val toolRegistry = MatrixToolRegistry(context)
        val skillEngine = DynamicSkillEngine(context, toolRegistry, database.skillDao())
        
        val (tool, reason) = skillEngine.discoverOrSynthesizeSkill("crypto_price_scanner")
        assertNotNull(tool)
        assertTrue(tool!!.name.startsWith("synth_"))
        
        val savedSkill = database.skillDao().getSkillByName(tool.name)
        assertNotNull(savedSkill)
        assertEquals("SYNTHESIZED_SKILL", savedSkill?.category)
    }
}
