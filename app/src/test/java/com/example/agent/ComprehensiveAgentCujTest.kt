package com.example.agent

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.model.McpServer
import com.example.agent.ui.chat.ChatViewModel
import com.example.agent.util.AutomatedSystemEngine
import com.example.agent.util.GhostCallStage
import com.example.agent.util.GhostOperatorPhoneBridge
import com.example.agent.util.MatrixToolRegistry
import com.example.agent.util.PreThoughtEngine
import com.example.agent.util.ThinkingMethodEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ComprehensiveAgentCujTest {

    private lateinit var app: Application
    private lateinit var database: AgentDatabase
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        AgentDatabase.resetDatabaseForTesting()
        app = ApplicationProvider.getApplicationContext()
        database = AgentDatabase.getDatabase(app)
        viewModel = ChatViewModel(app)
    }

    @After
    fun tearDown() {
        AgentDatabase.resetDatabaseForTesting()
    }

    @Test
    fun testThinkingMethodEngine_ExecutionAndCustomMethodCreation() = runTest {
        val engine = ThinkingMethodEngine(app)
        val methods = engine.methods.value
        assertTrue("Should contain at least 4 default thinking methods", methods.size >= 4)

        val firstPrinciples = methods.firstOrNull { it.id == "FIRST_PRINCIPLES" }
        assertNotNull("First principles method should exist", firstPrinciples)

        // Execute thinking trace
        val (trace, answer) = engine.executeThinkingMethod("FIRST_PRINCIPLES", "Build quantum resilient node") { prompt ->
            "Quantum cryptographic node constructed via first principles."
        }
        assertNotNull(trace)
        assertEquals("FIRST_PRINCIPLES", trace.methodId)
        assertTrue(trace.steps.isNotEmpty())
        assertTrue(trace.synthesisSummary.isNotBlank())
        assertTrue(answer.contains("Quantum"))

        // Create custom thinking method
        val custom = engine.addCustomMethod(
            name = "Dialectical Synthesis",
            icon = "✨",
            tagline = "Thesis-Antithesis-Synthesis Triad",
            description = "Resolves internal contradictions through dialectical opposition.",
            steps = listOf(
                com.example.agent.util.ThinkingStep(1, "Formulate Thesis", "Establish thesis", "Axiomatic baseline"),
                com.example.agent.util.ThinkingStep(2, "Develop Antithesis", "Contrast antithesis", "Challenge assumptions"),
                com.example.agent.util.ThinkingStep(3, "Synthesize Resolution", "Converge on synthesis", "Resolve contradiction")
            ),
            guidancePrompt = "Follow Hegelian dialectics to converge on synthesis."
        )
        assertNotNull(custom)
        assertTrue(engine.methods.value.any { it.id == custom.id })
    }

    @Test
    fun testAutomatedSystemEngine_RoutineExecutionAndSynthesis() = runTest {
        val engine = AutomatedSystemEngine(app, database)
        val routines = engine.routines.value
        assertTrue("Should contain pre-configured automated routines", routines.size >= 4)

        val dining = routines.firstOrNull { it.id == "auto_dining_concierge" }
        assertNotNull(dining)
        assertTrue(dining!!.isEnabled)

        // Toggle routine
        engine.toggleRoutine("auto_dining_concierge", false)
        assertFalse(engine.routines.value.first { it.id == "auto_dining_concierge" }.isEnabled)
        engine.toggleRoutine("auto_dining_concierge", true)
        assertTrue(engine.routines.value.first { it.id == "auto_dining_concierge" }.isEnabled)

        // Execute automated routine
        val trace = engine.executeRoutineNow("auto_dining_concierge")
        assertNotNull(trace)
        assertEquals("SUCCESS", trace.status)
        assertTrue(trace.stepResults.isNotEmpty())
        assertTrue(trace.finalDecree.contains("Autonomous Dining"))

        // Synthesize new automated routine
        val synthesized = engine.synthesizeAutomatedLogic(
            naturalLanguagePrompt = "Auto backup daily knowledge records to secure local storage at midnight"
        )
        assertNotNull(synthesized)
        assertTrue(engine.routines.value.any { it.id == synthesized.id })
    }

    @Test
    fun testGhostOperatorPhoneBridge_MissionProgressionAndDtmf() = runTest {
        val bridge = GhostOperatorPhoneBridge(
            context = app,
            callLogDao = database.callLogDao(),
            memoryDao = database.memoryDao()
        )

        assertEquals(GhostCallStage.IDLE, bridge.callStatus.value.stage)

        // Send DTMF key
        bridge.sendDtmfTone("9")
        assertEquals("9", bridge.callStatus.value.activeDtmf)
        assertTrue(bridge.callStatus.value.liveTranscript.any { it.second.contains("[Transmitted Tone: 9]") })

        // Launch native dialer safely
        bridge.launchNativePhoneDialer("(555) 019-9922")
    }

    @Test
    fun testMcpServerManagement_FullLifecycle() = runTest {
        val engine = viewModel.dynamicSkillEngine
        val initialInstalled = engine.mcpServers.value
        assertTrue(initialInstalled.isNotEmpty())

        // Check catalog
        val catalog = engine.catalogServers.value
        assertTrue(catalog.isNotEmpty())

        // Install a server from catalog
        val serverToInstall = catalog.first()
        viewModel.installMcpServer(serverToInstall)

        assertTrue(engine.mcpServers.value.any { it.serverId == serverToInstall.serverId })
        assertFalse(engine.catalogServers.value.any { it.serverId == serverToInstall.serverId })

        // Uninstall server
        viewModel.uninstallMcpServer(serverToInstall.serverId)
        assertFalse(engine.mcpServers.value.any { it.serverId == serverToInstall.serverId })
        assertTrue(engine.catalogServers.value.any { it.serverId == serverToInstall.serverId })

        // Add custom server
        viewModel.addCustomMcpServer(
            name = "Custom Research Hub",
            endpoint = "https://mcp.research.org/sse",
            transport = "HTTP_STREAMED_SSE",
            desc = "Live research paper streamer",
            auth = "Bearer token_xyz"
        )
        assertTrue(engine.mcpServers.value.any { it.name == "Custom Research Hub" })

        // Reset to defaults
        viewModel.resetMcpServersToDefaults()
        assertTrue(engine.mcpServers.value.any { it.serverId == "mcp-brave" })
        assertTrue(engine.mcpServers.value.any { it.serverId == "mcp-soundfx" })

        // Execute download_sound_byte tool
        val downloadResult = engine.executeMcpTool("download_sound_byte", mapOf("query" to "matrix_keystroke_cyber_pulse", "duration_ms" to "250"))
        assertTrue(downloadResult.contains("Downloaded sound byte"))
        assertTrue(downloadResult.contains("PCM"))

        // Test auto execution on sound byte query
        val autoResult = engine.detectAndAutoExecuteMcp("Download the sound byte use an mcp server if needed")
        assertNotNull(autoResult)
        assertEquals("mcp-soundfx", autoResult!!.serverId)
        assertEquals("download_sound_byte", autoResult.toolName)
        assertTrue(autoResult.resultTelemetry.contains("Downloaded sound byte"))
    }

    @Test
    fun testMatrixToolRegistry_Operations() {
        val registry = MatrixToolRegistry(app)

        val mathResult = registry.evaluateMath("12 * 8 + 4")
        assertTrue(mathResult.success)
        assertTrue(mathResult.message.contains("100"))

        val batteryResult = registry.getBatteryTelemetry()
        assertTrue(batteryResult.success)

        val diagResult = registry.getSystemDiagnostics()
        assertTrue(diagResult.success)
        assertTrue(diagResult.message.contains("NOMINAL"))

        val callResult = registry.makeCall("555-0199")
        assertTrue(callResult.success)

        val smsResult = registry.sendSms("555-0199", "Matrix check")
        assertTrue(smsResult.success)
    }

    @Test
    fun testPreThoughtEngine_AllCategories() {
        val engine = PreThoughtEngine()

        val callPlan = engine.analyze("Call Metro Bistro for table reservation")
        assertTrue(callPlan.plan.any { it.contains("Ghost Operator") })
        assertTrue(callPlan.constraints.isNotEmpty())

        val smsPlan = engine.analyze("Send text to John")
        assertTrue(smsPlan.plan.any { it.contains("SmsManager") })

        val alarmPlan = engine.analyze("Set alarm for 7am")
        assertTrue(alarmPlan.plan.any { it.contains("AlarmManager") })

        val autoPlan = engine.analyze("Automate evening power workflow")
        assertTrue(autoPlan.plan.any { it.contains("Cognitive Thinking") })
    }

    @Test
    fun testChatViewModel_CommandRouting() = runTest {
        viewModel.sendMessage("/thinking")
        advanceUntilIdle()

        viewModel.sendMessage("/automation")
        advanceUntilIdle()

        viewModel.sendMessage("/mcp")
        advanceUntilIdle()

        viewModel.sendMessage("/calls")
        advanceUntilIdle()

        viewModel.sendMessage("/tools")
        advanceUntilIdle()

        // Verify MCP management dialog toggle
        viewModel.openMcpMenu()
        assertTrue(viewModel.showMcpMenu.value)
        viewModel.closeMcpMenu()
        assertFalse(viewModel.showMcpMenu.value)
    }
}
