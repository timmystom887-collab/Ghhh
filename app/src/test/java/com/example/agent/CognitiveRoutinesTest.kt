package com.example.agent

import com.example.agent.domain.cognitive.ActionArbitrationDecision
import com.example.agent.domain.cognitive.AgentCognitiveState
import com.example.agent.domain.cognitive.CognitiveEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CognitiveRoutinesTest {

    private lateinit var engine: CognitiveEngine

    @Before
    fun setUp() {
        engine = CognitiveEngine()
    }

    @Test
    fun testSalienceComputation() {
        val salience = engine.computeSalience("Call 555-0199 immediately at 3pm for urgent meeting")
        assertNotNull(salience)
        assertTrue(salience.urgency > 0.8f)
        assertTrue(salience.requiresImmediateAction)
        assertTrue(salience.primaryEntities.any { it.contains("555-0199") })
    }

    @Test
    fun testGoalTrackingLifecycle() {
        engine.pushGoal("goal_1", "Optimize Android background battery")
        assertEquals(AgentCognitiveState.REASONING, engine.cognitiveState.value)
        assertEquals(1, engine.activeGoals.value.size)

        engine.updateGoalProgress("goal_1", 50)
        assertEquals(50, engine.activeGoals.value[0].progressPct)

        engine.completeGoal("goal_1")
        assertEquals(0, engine.activeGoals.value.size)
        assertEquals(AgentCognitiveState.STANDBY, engine.cognitiveState.value)
    }

    @Test
    fun testActionArbitration() {
        // Low battery throttles non-essential action
        val decision1 = engine.arbitrateAction(
            actionName = "deep_research",
            confidenceScore = 0.95f,
            isLowBattery = true,
            isPreAuthorized = false
        )
        assertTrue(decision1 is ActionArbitrationDecision.Throttled)

        // Low battery permits high-priority emergency/alarm action
        val decision2 = engine.arbitrateAction(
            actionName = "set_alarm",
            confidenceScore = 0.95f,
            isLowBattery = true,
            isPreAuthorized = false
        )
        assertTrue(decision2 is ActionArbitrationDecision.Proceed)

        // Low confidence requires confirmation
        val decision3 = engine.arbitrateAction(
            actionName = "send_sms",
            confidenceScore = 0.5f,
            isLowBattery = false,
            isPreAuthorized = false
        )
        assertTrue(decision3 is ActionArbitrationDecision.RequiresConfirmation)
    }
}
