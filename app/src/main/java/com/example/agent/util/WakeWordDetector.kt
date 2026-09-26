package com.example.agent.util

data class WakeWordMatch(
    val isMatched: Boolean,
    val matchedPhrase: String,
    val extractedQuery: String,
    val confidence: Float
)

class WakeWordDetector {
    fun evaluateTranscript(
        rawTranscript: String,
        targetWakeWord: String,
        sensitivity: String = "High",
        isWakeWordEnabled: Boolean = true
    ): WakeWordMatch {
        if (!isWakeWordEnabled || rawTranscript.isBlank() || targetWakeWord.isBlank()) {
            return WakeWordMatch(false, "", "", 0f)
        }

        val transcriptClean = rawTranscript.trim()
        val transcriptLower = transcriptClean.lowercase()
        val wakeWordClean = targetWakeWord.trim().lowercase()

        // Multi-alias variations based on user's target wake word (e.g. "Agent Smith" -> "smith", "agent smith", "hey smith", "ok smith")
        val wakeWordAliases = mutableListOf(wakeWordClean)
        val tokens = wakeWordClean.split(" ")
        if (tokens.size > 1) {
            wakeWordAliases.add(tokens.last()) // e.g. "smith"
        }
        if (!wakeWordClean.startsWith("hey ")) {
            wakeWordAliases.add("hey $wakeWordClean")
        }
        if (!wakeWordClean.startsWith("ok ")) {
            wakeWordAliases.add("ok $wakeWordClean")
        }

        var bestMatch: String? = null
        var matchIndex = -1

        for (alias in wakeWordAliases) {
            val idx = transcriptLower.indexOf(alias)
            if (idx != -1) {
                bestMatch = alias
                matchIndex = idx
                break
            }
        }

        // Fuzzy phoneme & alias matching if exact match not found on High sensitivity
        if (bestMatch == null && sensitivity.equals("High", ignoreCase = true)) {
            val commonPhoneticAliases = listOf("smith", "smyth", "matrix", "agent", "jarvis", "computer", "oracle")
            for (alias in commonPhoneticAliases) {
                if (transcriptLower.contains(alias)) {
                    bestMatch = alias
                    matchIndex = transcriptLower.indexOf(alias)
                    break
                }
            }
        }

        if (bestMatch != null && matchIndex != -1) {
            // Extract the query following the wake word
            val postQuery = transcriptClean.substring(matchIndex + bestMatch.length).trim()
                .removePrefix(",").removePrefix(".").removePrefix(":").trim()

            val confidence = when {
                transcriptLower.startsWith(bestMatch) -> 0.98f
                sensitivity.equals("High", ignoreCase = true) -> 0.92f
                else -> 0.85f
            }

            return WakeWordMatch(
                isMatched = true,
                matchedPhrase = bestMatch,
                extractedQuery = postQuery,
                confidence = confidence
            )
        }

        return WakeWordMatch(false, "", "", 0f)
    }
}
