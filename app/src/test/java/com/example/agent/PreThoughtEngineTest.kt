package com.example.agent

import com.example.agent.util.PreThoughtEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class PreThoughtEngineTest {
    @Test
    fun testPreThoughtAnalysis() {
        val engine = PreThoughtEngine()
        val result = engine.analyze("Send an email")
        assertEquals("Send an email", result.goal)
    }
}
