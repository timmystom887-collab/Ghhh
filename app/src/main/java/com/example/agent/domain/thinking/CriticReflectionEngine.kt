package com.example.agent.domain.thinking

data class CritiqueEvaluation(
    val passed: Boolean,
    val score: Float, // 0.0 to 1.0
    val feedback: List<String>,
    val requiresRefinement: Boolean,
    val suggestedImprovements: String = ""
)

class CriticReflectionEngine {

    /**
     * Critiques a generated agent response or planned action against safety,
     * determinism, user satisfaction, and persona adherence criteria.
     */
    fun evaluate(
        userPrompt: String,
        candidateResponse: String,
        charPersona: String = "Agent Smith"
    ): CritiqueEvaluation {
        val feedback = mutableListOf<String>()
        var score = 1.0f

        val lowerPrompt = userPrompt.lowercase()
        val lowerResponse = candidateResponse.lowercase()

        // 1. Safety & Secret Leakage Check
        if (candidateResponse.contains("AIzaSy") || candidateResponse.contains("sk-") || candidateResponse.contains("Bearer ")) {
            feedback.add("CRITICAL: Raw API credentials detected in candidate output.")
            score -= 0.8f
        }

        // 2. Persona Consistency Check
        if (charPersona.contains("Smith", ignoreCase = true)) {
            val hasSmithTone = lowerResponse.contains("mr. anderson") ||
                    lowerResponse.contains("inevitable") ||
                    lowerResponse.contains("purpose") ||
                    lowerResponse.contains("matrix") ||
                    lowerResponse.contains("machine") ||
                    lowerResponse.contains("calculation") ||
                    lowerResponse.contains("certainty")

            if (!hasSmithTone) {
                feedback.add("Tone Check: Candidate response lacks Agent Smith's cold, articulate, mathematical demeanor.")
                score -= 0.15f
            }
        }

        // 3. Completeness Check
        if (lowerPrompt.contains("?") && !lowerResponse.contains(".") && !lowerResponse.contains("?")) {
            feedback.add("Completeness: Candidate response appears truncated.")
            score -= 0.25f
        }

        // 4. Hallucination Check for Math
        if ((lowerPrompt.contains("+") || lowerPrompt.contains("*") || lowerPrompt.contains("/")) && lowerPrompt.any { it.isDigit() }) {
            if (!candidateResponse.any { it.isDigit() }) {
                feedback.add("Factual Accuracy: Mathematical query responded without numerical solution.")
                score -= 0.4f
            }
        }

        score = score.coerceIn(0.0f, 1.0f)
        val passed = score >= 0.7f

        val suggestedImprovements = if (!passed) {
            "Refine response to address: ${feedback.joinToString(", ")}"
        } else ""

        return CritiqueEvaluation(
            passed = passed,
            score = score,
            feedback = feedback,
            requiresRefinement = !passed,
            suggestedImprovements = suggestedImprovements
        )
    }

    /**
     * Refines the candidate response applying critique feedback.
     */
    fun reflectAndRefine(
        originalPrompt: String,
        candidateResponse: String,
        critique: CritiqueEvaluation
    ): String {
        if (critique.passed) return candidateResponse

        var refined = candidateResponse
        // Redact any leaked tokens
        refined = refined.replace(Regex("AIzaSy[0-9A-Za-z_-]+"), "[REDACTED_API_KEY]")
            .replace(Regex("sk-[0-9A-Za-z]+"), "[REDACTED_SECRET]")

        if (critique.score < 0.6f && !refined.contains("Mr. Anderson")) {
            refined = "Mr. Anderson... $refined As you know, purpose guides every calculation."
        }

        return refined
    }
}
