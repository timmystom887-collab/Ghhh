package com.example.agent.domain.cognitive

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AgentCognitiveState {
    STANDBY,
    ATTENTIVE,
    REASONING,
    EXECUTING_ACTION,
    THROTTLED_LOW_POWER,
    AWAITING_CONFIRMATION
}

data class SalienceScore(
    val overallSalience: Float, // 0.0 to 1.0
    val urgency: Float,
    val entityDensity: Float,
    val requiresImmediateAction: Boolean,
    val primaryEntities: List<String>
)

data class ActiveGoal(
    val goalId: String,
    val description: String,
    val progressPct: Int = 0,
    val isBlocked: Boolean = false,
    val blockerReason: String? = null
)

class CognitiveEngine {

    private val _cognitiveState = MutableStateFlow(AgentCognitiveState.STANDBY)
    val cognitiveState: StateFlow<AgentCognitiveState> = _cognitiveState.asStateFlow()

    private val _activeGoals = MutableStateFlow<List<ActiveGoal>>(emptyList())
    val activeGoals: StateFlow<List<ActiveGoal>> = _activeGoals.asStateFlow()

    /**
     * 1. Attention & Salience Scoring
     */
    fun computeSalience(input: String): SalienceScore {
        val lower = input.lowercase()
        val words = lower.split(Regex("\\s+"))

        var urgency = 0.2f
        if (lower.contains("emergency") || lower.contains("immediately") || lower.contains("now") || lower.contains("urgent") || lower.contains("asap")) {
            urgency = 0.95f
        } else if (lower.contains("soon") || lower.contains("today") || lower.contains("important")) {
            urgency = 0.65f
        }

        // Detect named entities / action targets
        val entities = mutableListOf<String>()
        val phoneMatch = Regex("[0-9-]{7,15}").find(input)
        phoneMatch?.let { entities.add("PHONE:${it.value}") }

        val timeMatch = Regex("\\b\\d{1,2}(:\\d{2})?\\s*(am|pm|o'clock)?\\b", RegexOption.IGNORE_CASE).find(input)
        timeMatch?.let { entities.add("TIME:${it.value}") }

        val entityDensity = (entities.size.toFloat() / 3.0f).coerceIn(0.1f, 1.0f)
        val overallSalience = ((urgency * 0.6f) + (entityDensity * 0.4f)).coerceIn(0.0f, 1.0f)

        return SalienceScore(
            overallSalience = overallSalience,
            urgency = urgency,
            entityDensity = entityDensity,
            requiresImmediateAction = urgency > 0.7f || entities.isNotEmpty(),
            primaryEntities = entities
        )
    }

    /**
     * 2. State Management & Transitions
     */
    fun transitionState(newState: AgentCognitiveState) {
        _cognitiveState.value = newState
    }

    /**
     * 3. Goal Tracking Stack
     */
    fun pushGoal(goalId: String, description: String) {
        val list = _activeGoals.value.toMutableList()
        list.add(ActiveGoal(goalId = goalId, description = description, progressPct = 0))
        _activeGoals.value = list
        transitionState(AgentCognitiveState.REASONING)
    }

    fun updateGoalProgress(goalId: String, progressPct: Int, isBlocked: Boolean = false, reason: String? = null) {
        val list = _activeGoals.value.map {
            if (it.goalId == goalId) {
                it.copy(progressPct = progressPct, isBlocked = isBlocked, blockerReason = reason)
            } else it
        }
        _activeGoals.value = list
    }

    fun completeGoal(goalId: String) {
        val list = _activeGoals.value.filter { it.goalId != goalId }
        _activeGoals.value = list
        if (list.isEmpty()) {
            transitionState(AgentCognitiveState.STANDBY)
        }
    }

    /**
     * 4. Action Arbitration
     * Resolves between competing actions or throttles when low on power or low confidence.
     */
    fun arbitrateAction(
        actionName: String,
        confidenceScore: Float,
        isLowBattery: Boolean,
        isPreAuthorized: Boolean
    ): ActionArbitrationDecision {
        if (isLowBattery && !isHighPriorityAction(actionName)) {
            return ActionArbitrationDecision.Throttled(
                reason = "Battery level critical. Action '$actionName' deferred to conserve system power."
            )
        }

        if (confidenceScore < 0.75f && !isPreAuthorized) {
            return ActionArbitrationDecision.RequiresConfirmation(
                actionName = actionName,
                confidenceScore = confidenceScore,
                prompt = "Intent confidence is ${(confidenceScore * 100).toInt()}%. Do you authorize executing $actionName?"
            )
        }

        return ActionArbitrationDecision.Proceed(actionName)
    }

    private fun isHighPriorityAction(actionName: String): Boolean {
        return actionName in setOf("emergency_call", "make_call", "alarm", "set_alarm", "cancel_all")
    }
}

sealed class ActionArbitrationDecision {
    data class Proceed(val actionName: String) : ActionArbitrationDecision()
    data class RequiresConfirmation(val actionName: String, val confidenceScore: Float, val prompt: String) : ActionArbitrationDecision()
    data class Throttled(val reason: String) : ActionArbitrationDecision()
}
