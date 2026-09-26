package com.example.agent.util

object SecurityUtils {
    fun redactSecrets(input: String): String {
        return input.replace(Regex("AIzaSy[0-9A-Za-z_-]{33}"), "[REDACTED_API_KEY]")
            .replace(Regex("sk-[0-9A-Za-z]{20,}"), "[REDACTED_SECRET]")
    }
}
