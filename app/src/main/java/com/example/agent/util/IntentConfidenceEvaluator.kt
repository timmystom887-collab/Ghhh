package com.example.agent.util

data class ConfidenceScore(
    val intentClarity: Int,
    val purposeClarity: Int,
    val outcomeAlignment: Int,
    val overall: Int
) {
    fun isApproved(isSensitive: Boolean): Boolean {
        val threshold = if (isSensitive) 95 else 80
        return overall >= threshold
    }
}

class IntentConfidenceEvaluator {
    fun evaluate(prompt: String): ConfidenceScore {
        // In production, analyze prompt clarity. For standard instructions, high confidence if specific.
        val clarity = if (prompt.length > 5) 90 else 60
        return ConfidenceScore(
            intentClarity = clarity,
            purposeClarity = clarity,
            outcomeAlignment = clarity,
            overall = clarity
        )
    }
}
