package com.example.agent

import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.repository.AgentRepository
import com.example.agent.domain.client.ILlmProviderClient
import com.example.agent.domain.hermes.HermesAgentLoop
import com.example.agent.domain.hermes.HermesSkillLibrary
import com.example.agent.domain.hermes.HermesToolProtocol
import com.example.agent.domain.model.LlmResult
import com.example.agent.domain.research.DeepResearchAgent
import com.example.agent.util.MatrixToolRegistry
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HermesAgentIntegrationTest {

    private lateinit var database: AgentDatabase
    private lateinit var repository: AgentRepository
    private lateinit var toolRegistry: MatrixToolRegistry

    // Deterministic LLM Test Double for Multi-Turn Hermes Tool Calling
    private val hermesFakeLlmClient = object : ILlmProviderClient {
        var callCount = 0

        override suspend fun generateResponse(
            prompt: String,
            systemInstruction: String?,
            model: String,
            apiKey: String,
            thinkingLevel: String
        ): LlmResult {
            callCount++
            return if (callCount == 1) {
                // Turn 1: Model outputs scratchpad + tool call
                LlmResult.Success(
                    text = """
                        <scratchpad>
                        The user directive requires calculating 144 * 12 before taking action.
                        I must invoke evaluate_math.
                        </scratchpad>
                        <tool_call>
                        {"name": "evaluate_math", "arguments": {"expression": "144 * 12"}}
                        </tool_call>
                    """.trimIndent(),
                    promptTokens = 15,
                    completionTokens = 25
                )
            } else {
                // Turn 2: Model ingests <tool_response> and produces final answer
                LlmResult.Success(
                    text = """
                        <scratchpad>
                        Observation received: 1728. The calculation is complete.
                        </scratchpad>
                        Mr. Anderson... The product resolves to 1728. Purpose guides every calculation.
                    """.trimIndent(),
                    promptTokens = 20,
                    completionTokens = 15
                )
            }
        }
    }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = AgentDatabase.getDatabase(context)
        repository = AgentRepository(
            context = context,
            database = database,
            llmClient = hermesFakeLlmClient
        )
        toolRegistry = MatrixToolRegistry(context)
    }

    @After
    fun tearDown() {
        AgentDatabase.resetDatabaseForTesting()
    }

    @Test
    fun testHermesToolProtocol_XmlGenerationAndExtraction() {
        // 1. Tool XML generation
        val toolsXml = HermesToolProtocol.formatToolsXml(HermesSkillLibrary.BUNDLED_TOOLS)
        assertTrue(toolsXml.startsWith("<tools>"))
        assertTrue(toolsXml.endsWith("</tools>"))
        assertTrue(toolsXml.contains("evaluate_math"))
        assertTrue(toolsXml.contains("set_alarm"))
        assertTrue(toolsXml.contains("make_call"))

        // 2. Scratchpad extraction
        val sampleWithScratchpad = """
            <scratchpad>
            Goal: Confirm destination coordinate and dispatch signal.
            </scratchpad>
            <tool_call>{"name": "get_battery", "arguments": {}}</tool_call>
        """.trimIndent()
        val scratchpad = HermesToolProtocol.extractScratchpad(sampleWithScratchpad)
        assertNotNull(scratchpad)
        assertTrue(scratchpad!!.contains("Confirm destination coordinate"))

        // 3. Tool call extraction
        val toolCalls = HermesToolProtocol.extractToolCalls(sampleWithScratchpad)
        assertEquals(1, toolCalls.size)
        assertEquals("get_battery", toolCalls[0].name)

        // 4. Tool response formatting
        val responseXml = HermesToolProtocol.formatToolResponse("get_battery", "Battery at 88%")
        assertTrue(responseXml.contains("<tool_response>"))
        assertTrue(responseXml.contains("Battery at 88%"))

        // 5. User-facing text sanitization
        val clean = HermesToolProtocol.sanitizeUserFacingText(sampleWithScratchpad)
        assertEquals("", clean)
    }

    @Test
    fun testHermesAgentLoop_MultiTurnAutonomousExecution() = runBlocking {
        val loop = HermesAgentLoop(repository, toolRegistry, maxTurns = 3)
        val result = loop.executeGoal("Calculate 144 * 12")

        assertNotNull(result)
        assertTrue(result.isSuccessful)
        assertEquals(2, result.steps.size)

        // Step 1 had tool call evaluate_math
        assertEquals("evaluate_math", result.steps[0].toolCall?.name)
        assertTrue(result.steps[0].toolResponse!!.contains("1728"))

        // Step 2 was final answer
        assertTrue(result.steps[1].isFinal)
        assertTrue(result.finalResponse.contains("1728") || result.finalResponse.contains("Mr. Anderson"))
    }

    @Test
    fun testDeepResearchAgent_MultiVectorSynthesisAndCitations() = runBlocking {
        val researchAgent = DeepResearchAgent(repository)
        val dossier = researchAgent.conductDeepResearch("Autonomous Git Agent Systems")

        assertNotNull(dossier)
        assertEquals("Autonomous Git Agent Systems", dossier.topic)
        assertEquals(3, dossier.sources.size)

        // Verify citations
        assertTrue(dossier.fullMarkdownReport.contains("[Source 1]"))
        assertTrue(dossier.fullMarkdownReport.contains("[Source 2]"))
        assertTrue(dossier.fullMarkdownReport.contains("[Source 3]"))

        // Verify persistence to Knowledge Database
        assertNotNull(dossier.savedKnowledgeId)
        val savedKnowledge = database.knowledgeDao().getAllKnowledge().first()
        assertTrue(savedKnowledge.any { it.title.contains("Autonomous Git Agent Systems") })
    }

    @Test
    fun testHermesSkillLibrary_Integrity() {
        val math = HermesSkillLibrary.getToolByName("evaluate_math")
        assertNotNull(math)
        assertTrue(math!!.parameters.containsKey("expression"))

        val hash = HermesSkillLibrary.getToolByName("compute_hash")
        assertNotNull(hash)
        assertTrue(hash!!.parameters.containsKey("input_data"))

        val git = HermesSkillLibrary.getToolByName("create_git_patch")
        assertNotNull(git)
        assertTrue(git!!.parameters.containsKey("target_file"))
    }
}
