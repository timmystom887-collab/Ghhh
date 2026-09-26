package com.example.agent

import com.example.agent.util.ThinkingMethodEngine
import com.example.agent.util.ThinkingStep
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThinkingMethodEngineTest {

    @Test
    fun testDefaultMethodsAvailable() {
        val engine = ThinkingMethodEngine()
        val methods = engine.methods.value
        assertTrue("Should have at least 6 default thinking methods", methods.size >= 6)

        val firstPrinciples = engine.getMethodById("FIRST_PRINCIPLES")
        assertNotNull(firstPrinciples)
        assertEquals("First Principles Deconstruction", firstPrinciples.name)
        assertTrue(firstPrinciples.stepsToFollow.size >= 4)
    }

    @Test
    fun testCustomMethodCreation() {
        val engine = ThinkingMethodEngine()
        val customSteps = listOf(
            ThinkingStep(1, "Deconstruct", "Deconstruct problem", "Identify ground facts"),
            ThinkingStep(2, "Synthesize", "Synthesize decree", "Commit optimal path")
        )
        val created = engine.addCustomMethod(
            name = "Test Heuristic Protocol",
            icon = "⚡",
            tagline = "Test tagline",
            description = "Test description",
            steps = customSteps,
            guidancePrompt = "Follow test heuristic protocol"
        )

        assertNotNull(created)
        assertEquals("Test Heuristic Protocol", created.name)
        assertTrue(created.isCustom)

        val retrieved = engine.getMethodById(created.id)
        assertEquals(created.id, retrieved.id)
    }

    @Test
    fun testExecuteThinkingMethodGeneratesTrace() = runTest {
        val engine = ThinkingMethodEngine()
        val (trace, finalAnswer) = engine.executeThinkingMethod(
            methodId = "FIRST_PRINCIPLES",
            userQuery = "Optimize server cluster latency",
            orchestratorExecute = { prompt ->
                "[Agent Smith]: The calculation is complete. Optimal route confirmed."
            }
        )

        assertNotNull(trace)
        assertEquals("First Principles Deconstruction", trace.methodName)
        assertTrue(trace.steps.isNotEmpty())
        assertTrue(finalAnswer.contains("Optimal route confirmed"))
    }
}
