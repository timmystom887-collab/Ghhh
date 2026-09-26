package com.example.agent

import com.example.agent.util.AutomatedSystemEngine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutomatedSystemEngineTest {

    @Test
    fun testDefaultRoutinesLoaded() {
        val engine = AutomatedSystemEngine()
        val routines = engine.routines.value
        assertTrue("Should have default automated system routines", routines.size >= 5)

        val dining = routines.firstOrNull { it.id == "auto_dining_concierge" }
        assertNotNull(dining)
        assertTrue(dining!!.logicSteps.isNotEmpty())
    }

    @Test
    fun testSynthesizeAutomatedLogic() {
        val engine = AutomatedSystemEngine()
        val synthesized = engine.synthesizeAutomatedLogic("Whenever battery drops, switch to offline AI and mute alarms")

        assertNotNull(synthesized)
        assertEquals("POWER_MGMT", synthesized.category)
        assertEquals("BATTERY_TELEMETRY", synthesized.triggerType)
        assertTrue(synthesized.logicSteps.size >= 3)
    }

    @Test
    fun testExecuteRoutineNow() = runTest {
        val engine = AutomatedSystemEngine()
        val trace = engine.executeRoutineNow("auto_dining_concierge")

        assertNotNull(trace)
        assertEquals("SUCCESS", trace.status)
        assertTrue(trace.stepResults.isNotEmpty())
    }
}
