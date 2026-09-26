package com.example.agent

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AgentUiTest {
    @Test
    fun testMainActivityLaunch() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onActivity { activity ->
            assertNotNull(activity)
            assertEquals(Lifecycle.State.RESUMED, activity.lifecycle.currentState)
        }
        scenario.close()
    }
}
