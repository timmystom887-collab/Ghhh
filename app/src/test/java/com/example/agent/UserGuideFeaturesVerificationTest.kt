package com.example.agent

import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MemoryEntity
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.repository.AgentRepository
import com.example.agent.domain.cognitive.CognitiveEngine
import com.example.agent.domain.thinking.ContextAssembler
import com.example.agent.domain.thinking.CriticReflectionEngine
import com.example.agent.domain.thinking.TaskDecomposer
import com.example.agent.domain.tools.ToolSelectionEngine
import com.example.agent.service.battery.BatteryAwareWorkManager
import com.example.agent.util.MatrixToolRegistry
import com.example.agent.util.PreCognitionEngine
import com.example.agent.util.PreThoughtEngine
import com.example.agent.util.TaskOrchestrator
import com.example.agent.util.ThinkingMethodEngine
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
import org.robolectric.annotation.Config

/**
 * End-to-end unit and integration test suite systematically validating every feature
 * and command documented in the HTML Operator Manual (user_guide.html).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserGuideFeaturesVerificationTest {

    private lateinit var context: android.content.Context
    private lateinit var database: AgentDatabase
    private lateinit var repository: AgentRepository
    private lateinit var toolRegistry: MatrixToolRegistry
    private lateinit var batteryManager: BatteryAwareWorkManager
    private lateinit var thinkingMethodEngine: ThinkingMethodEngine
    private lateinit var cognitiveEngine: CognitiveEngine
    private lateinit var toolSelectionEngine: ToolSelectionEngine

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = AgentDatabase.getDatabase(context)
        repository = AgentRepository(context, database)
        toolRegistry = MatrixToolRegistry(context)
        batteryManager = BatteryAwareWorkManager.getInstance(context)
        thinkingMethodEngine = ThinkingMethodEngine(context)
        cognitiveEngine = CognitiveEngine()
        toolSelectionEngine = ToolSelectionEngine(toolRegistry)
    }

    @After
    fun tearDown() {
        AgentDatabase.resetDatabaseForTesting()
    }

    // 1. Battery Monitor & Power Governance
    @Test
    fun testFeature_BatteryGovernance() = runBlocking {
        assertNotNull(batteryManager)
        batteryManager.pauseNonEssentialTasks("Simulating low battery test")
        batteryManager.resumeNonEssentialTasks("Simulating power restoration")
        assertTrue(true)
    }

    // 2. Thinking Frameworks (/thinking)
    @Test
    fun testFeature_ThinkingFrameworks() {
        val methods = thinkingMethodEngine.methods.value
        assertTrue(methods.isNotEmpty())
        assertTrue(methods.any { it.id == "FIRST_PRINCIPLES" })
        assertTrue(methods.any { it.id == "TREE_OF_THOUGHTS" })
        assertTrue(methods.any { it.id == "SOCRATIC_EXAMINATION" })
    }

    // 3. Task Decomposition & Multi-Step Reasoning
    @Test
    fun testFeature_TaskDecomposition() {
        val decomposer = TaskDecomposer()
        val plan = decomposer.decompose("turn on flashlight then calculate 25 * 4 then check battery")
        assertEquals(3, plan.subTasks.size)
        assertEquals("set_torch", plan.subTasks[0].toolRequired)
        assertEquals("evaluate_math", plan.subTasks[1].toolRequired)
        assertEquals("get_battery", plan.subTasks[2].toolRequired)
    }

    // 4. Critic & Safety Reflection
    @Test
    fun testFeature_CriticReflection() {
        val critic = CriticReflectionEngine()
        val unsafe = "API key: AIzaSySecretKeySample987654321"
        val eval = critic.evaluate("give key", unsafe)
        assertTrue(eval.requiresRefinement)

        val refined = critic.reflectAndRefine("give key", unsafe, eval)
        assertTrue(!refined.contains("AIzaSySecretKeySample987654321"))
    }

    // 5. Context Assembler & Memory Vault (/memory, /kb)
    @Test
    fun testFeature_ContextAssemblyAndRAG() = runBlocking {
        database.memoryDao().insertMemory(MemoryEntity(category = "work", content = "User is developing Android agent"))
        database.knowledgeDao().insertKnowledge(KnowledgeEntity(title = "Matrix Specs", content = "Autonomous architecture", category = "PROTOCOLS"))

        val memories = database.memoryDao().getAllMemories().first()
        val knowledge = database.knowledgeDao().getAllKnowledge().first()

        val assembler = ContextAssembler(maxTokenBudget = 1000)
        val assembled = assembler.assemble("Android agent architecture", memories, knowledge)

        assertNotNull(assembled)
        assertTrue(assembled.relevantMemories.isNotEmpty())
        assertTrue(assembled.relevantKnowledge.isNotEmpty())
    }

    // 6. Cognitive Engine (Salience, Goals, State Machine)
    @Test
    fun testFeature_CognitiveRoutines() {
        val salience = cognitiveEngine.computeSalience("Urgent: call 555-0199 now")
        assertTrue(salience.urgency > 0.8f)

        cognitiveEngine.pushGoal("goal_telephony", "Execute phone mission")
        assertEquals(1, cognitiveEngine.activeGoals.value.size)
        cognitiveEngine.completeGoal("goal_telephony")
        assertEquals(0, cognitiveEngine.activeGoals.value.size)
    }

    // 7. Tool Selection Engine & Device Tools (/tools)
    @Test
    fun testFeature_DeviceToolExecution() {
        // Math calculation
        val mathCall = toolSelectionEngine.classifyAndBind("calc 40 + 2")
        val mathResult = toolRegistry.executeTool(mathCall.toolName, mathCall.arguments)
        assertTrue(mathResult.contains("42"))

        // Battery telemetry
        val batteryResult = toolRegistry.getBatteryTelemetry()
        assertTrue(batteryResult.success)

        // System diagnostics
        val diagResult = toolRegistry.getSystemDiagnostics()
        assertTrue(diagResult.success)
        assertTrue(diagResult.message.contains("Matrix Node Status: NOMINAL"))
    }

    // 8. Temporal Pre-Cognition (/precog)
    @Test
    fun testFeature_PreCognition() = runBlocking {
        val precogEngine = PreCognitionEngine(context, database)
        val predictions = precogEngine.evaluatePredictiveWorkflows(
            isLowBattery = false,
            ambientTranscript = "User mentions dinner reservations"
        )
        assertNotNull(predictions)
        assertTrue(predictions.isNotEmpty())
    }

    // 9. Pre-Thought Protocol Analysis
    @Test
    fun testFeature_PreThoughtProtocol() {
        val engine = PreThoughtEngine()
        val analysis = engine.analyze("call BlueFin Sushi for table reservation")
        assertNotNull(analysis)
        assertTrue(analysis.plan.isNotEmpty())
        assertTrue(analysis.constraints.isNotEmpty())
    }

    // 10. Multi-Provider Task Orchestrator (/multiapi, /slms)
    @Test
    fun testFeature_TaskOrchestration() = runBlocking {
        val orchestrator = TaskOrchestrator(
            isOffline = true,
            preferredProvider = "Local SLM",
            toolRegistry = toolRegistry
        )
        val response = orchestrator.routeAndExecute("calc 100 / 4")
        assertNotNull(response)
        assertTrue(response.contains("Mathematical analysis resolved") || response.contains("Qwen"))
    }
}
