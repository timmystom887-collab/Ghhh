package com.example.agent

import androidx.test.core.app.ApplicationProvider
import com.example.agent.domain.model.ToolResult
import com.example.agent.domain.tools.IntentRouteCategory
import com.example.agent.domain.tools.ToolSelectionEngine
import com.example.agent.util.MatrixToolRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ToolSelectionRoutinesTest {

    private lateinit var toolRegistry: MatrixToolRegistry
    private lateinit var selectionEngine: ToolSelectionEngine

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        toolRegistry = MatrixToolRegistry(context)
        selectionEngine = ToolSelectionEngine(toolRegistry)
    }

    @Test
    fun testClassifyAndBind_AlarmTool() {
        val call = selectionEngine.classifyAndBind("set alarm for 7:30 wake up protocol")
        assertEquals("set_alarm", call.toolName)
        assertEquals(IntentRouteCategory.SYSTEM_DEVICE_TOOL, call.category)
        assertTrue(call.isValid)
        assertEquals("7", call.arguments["hour"])
        assertEquals("30", call.arguments["minute"])
    }

    @Test
    fun testClassifyAndBind_MathCalculation() {
        val call = selectionEngine.classifyAndBind("calc 15 * 8")
        assertEquals("evaluate_math", call.toolName)
        assertEquals("15 * 8", call.arguments["expression"])
        assertTrue(call.isValid)
    }

    @Test
    fun testClassifyAndBind_SmsValidation() {
        val validCall = selectionEngine.classifyAndBind("send sms 555-0199 Mission status green")
        assertTrue(validCall.isValid)
        assertEquals("555-0199", validCall.arguments["phone_number"])

        val invalidCall = selectionEngine.classifyAndBind("sms ")
        assertFalse(invalidCall.isValid)
        assertTrue(invalidCall.validationErrors.isNotEmpty())
    }

    @Test
    fun testExecuteWithRetry_Success() = runBlocking {
        val call = selectionEngine.classifyAndBind("calc 20 + 22")
        val result = selectionEngine.executeWithRetry(call)

        assertTrue(result is ToolResult.Success)
        val success = result as ToolResult.Success
        assertTrue(success.output.contains("42"))
    }
}
