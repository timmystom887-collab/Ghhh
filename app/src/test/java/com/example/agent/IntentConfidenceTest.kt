package com.example.agent

import com.example.agent.util.IntentConfidenceEvaluator
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentConfidenceTest {
    @Test
    fun testEvaluation() {
        val evaluator = IntentConfidenceEvaluator()
        val score = evaluator.evaluate("Call Mom")
        assertTrue(score.overall > 0)
    }
}
