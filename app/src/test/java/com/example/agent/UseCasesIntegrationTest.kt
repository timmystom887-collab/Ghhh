package com.example.agent

import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.data.repository.AgentRepository
import com.example.agent.domain.client.ILlmProviderClient
import com.example.agent.domain.model.LlmResult
import com.example.agent.domain.model.ToolResult
import com.example.agent.domain.usecase.ExecuteToolUseCase
import com.example.agent.domain.usecase.ManageTasksUseCase
import com.example.agent.domain.usecase.SendMessageUseCase
import com.example.agent.util.MatrixToolRegistry
import com.example.agent.util.PreThoughtEngine
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UseCasesIntegrationTest {

    private lateinit var database: AgentDatabase
    private lateinit var repository: AgentRepository
    private lateinit var toolRegistry: MatrixToolRegistry
    private lateinit var preThoughtEngine: PreThoughtEngine
    private lateinit var thinkingMethodEngine: ThinkingMethodEngine

    // Fake LLM Client for Deterministic Testing
    private val fakeLlmClient = object : ILlmProviderClient {
        var shouldFail = false
        var responseText = "Calculation resolved with absolute machine certainty."

        override suspend fun generateResponse(
            prompt: String,
            systemInstruction: String?,
            model: String,
            apiKey: String,
            thinkingLevel: String
        ): LlmResult {
            if (shouldFail) {
                return LlmResult.Error(
                    errorType = com.example.agent.domain.model.LlmErrorType.RATE_LIMITED,
                    message = "Rate limit reached in test double"
                )
            }
            return LlmResult.Success(
                text = responseText,
                promptTokens = 10,
                completionTokens = 20
            )
        }
    }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = AgentDatabase.getDatabase(context)
        repository = AgentRepository(
            context = context,
            database = database,
            llmClient = fakeLlmClient
        )
        toolRegistry = MatrixToolRegistry(context)
        preThoughtEngine = PreThoughtEngine()
        thinkingMethodEngine = ThinkingMethodEngine(context)
    }

    @After
    fun tearDown() {
        AgentDatabase.resetDatabaseForTesting()
    }

    @Test
    fun testSendMessageUseCase_Success() = runBlocking {
        val useCase = SendMessageUseCase(repository, preThoughtEngine, thinkingMethodEngine)
        val result = useCase.invoke("What is our objective?")

        assertTrue(result.isSuccess)
        val messages = repository.messages.first()
        assertEquals(2, messages.size) // 1 user + 1 agent response
        assertEquals("user", messages[0].sender)
        assertEquals("agent", messages[1].sender)
        assertEquals("Calculation resolved with absolute machine certainty.", messages[1].content)
    }

    @Test
    fun testExecuteToolUseCase_Success() = runBlocking {
        val useCase = ExecuteToolUseCase(repository, toolRegistry)
        val result = useCase.invoke("evaluate_math", mapOf("expression" to "12 * 12"))

        assertTrue(result is ToolResult.Success)
        val success = result as ToolResult.Success
        assertTrue(success.output.contains("144"))
    }

    @Test
    fun testManageTasksUseCase() = runBlocking {
        val useCase = ManageTasksUseCase(repository)
        val taskId = useCase.scheduleTask(
            description = "Matrix Telemetry Verification",
            actionType = "TELEMETRY",
            parameters = "{\"interval\": 60}",
            scheduleTime = System.currentTimeMillis() + 3600000L
        )

        assertNotNull(taskId)
        var currentTasks = useCase.tasks.first()
        assertEquals(1, currentTasks.size)
        assertEquals("pending", currentTasks[0].status)

        useCase.completeTask(taskId)
        currentTasks = useCase.tasks.first()
        assertEquals("completed", currentTasks[0].status)
    }
}
