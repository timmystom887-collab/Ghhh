package com.example.agent

import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MemoryEntity
import com.example.agent.domain.thinking.ContextAssembler
import com.example.agent.domain.thinking.CriticReflectionEngine
import com.example.agent.domain.thinking.ExecutionType
import com.example.agent.domain.thinking.TaskDecomposer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThinkingEngineRoutinesTest {

    @Test
    fun testTaskDecomposer_SequentialChain() {
        val decomposer = TaskDecomposer()
        val plan = decomposer.decompose("turn on flashlight then set alarm for 7 then check battery")

        assertNotNull(plan)
        assertEquals(3, plan.subTasks.size)
        assertEquals(ExecutionType.SEQUENTIAL, plan.executionType)
        assertEquals("set_torch", plan.subTasks[0].toolRequired)
        assertEquals("set_alarm", plan.subTasks[1].toolRequired)
        assertEquals("get_battery", plan.subTasks[2].toolRequired)
    }

    @Test
    fun testTaskDecomposer_PhoneCallMission() {
        val decomposer = TaskDecomposer()
        val plan = decomposer.decompose("call Metro Bistro for a reservation")

        assertTrue(plan.subTasks.size >= 3)
        assertTrue(plan.subTasks.any { it.toolRequired == "make_call" })
    }

    @Test
    fun testCriticReflectionEngine_SafetyAndPersonaCheck() {
        val critic = CriticReflectionEngine()

        // 1. Leaked secret candidate should fail
        val unsafeOutput = "Here is your key: AIzaSyTestKey1234567890123456789012"
        val critique1 = critic.evaluate("give key", unsafeOutput)
        assertFalse(critique1.passed)
        assertTrue(critique1.feedback.any { it.contains("Raw API credentials") })

        val refined = critic.reflectAndRefine("give key", unsafeOutput, critique1)
        assertFalse(refined.contains("AIzaSyTestKey1234567890123456789012"))

        // 2. Persona compliant candidate should pass
        val safeOutput = "Mr. Anderson... As you know, the calculation is inevitable and complete."
        val critique2 = critic.evaluate("status", safeOutput)
        assertTrue(critique2.passed)
    }

    @Test
    fun testContextAssembler_RelevanceScoringAndPacking() {
        val assembler = ContextAssembler(maxTokenBudget = 500)

        val memories = listOf(
            MemoryEntity(id = 1, category = "work", content = "User prefers python and reactive architecture"),
            MemoryEntity(id = 2, category = "emotion", content = "Feeling tired after travel")
        )

        val knowledge = listOf(
            KnowledgeEntity(id = 1, title = "Android Architecture", content = "Jetpack Compose and Clean Architecture patterns.", category = "ARCHITECTURE", tags = "android, compose, architecture"),
            KnowledgeEntity(id = 2, title = "Cooking Recipes", content = "How to make pasta carbonara.", category = "GENERAL", tags = "cooking, food")
        )

        val assembled = assembler.assemble(
            query = "Explain Android Compose architecture for reactive work",
            allMemories = memories,
            allKnowledge = knowledge
        )

        assertNotNull(assembled)
        assertTrue(assembled.relevantKnowledge.any { it.title.contains("Architecture") })
        assertTrue(assembled.relevantMemories.any { it.content.contains("reactive") })
        assertTrue(assembled.formattedContextString.contains("Android Architecture"))
    }
}
