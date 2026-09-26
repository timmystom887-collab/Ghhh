package com.example.agent.util

import android.content.Context
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.local.entity.ProactiveActionEntity
import kotlinx.coroutines.flow.first

class ProactiveCognitionEngine(
    private val context: Context,
    private val database: AgentDatabase
) {
    private val memoryDao = database.memoryDao()
    private val knowledgeDao = database.knowledgeDao()
    private val callLogDao = database.callLogDao()
    private val proactiveDao = database.proactiveActionDao()

    suspend fun analyzeAmbientTranscript(transcript: String): ProactiveActionEntity? {
        val trimmed = transcript.trim()
        if (trimmed.length < 6) return null
        val lower = trimmed.lowercase()

        // Fetch past context from memory bank and call history
        val memories = try { memoryDao.getAllMemories().first() } catch (e: Exception) { emptyList() }
        val recentCalls = try { callLogDao.getRecentCallLogs(5).first() } catch (e: Exception) { emptyList() }
        val knowledgeDocs = try { knowledgeDao.findRelevantKnowledge(trimmed, 2) } catch (e: Exception) { emptyList() }

        val pastRestaurant = recentCalls.firstOrNull { it.callType == "Reservation" }?.contactName
            ?: memories.firstOrNull { it.category == "favorite_food" || it.content.contains("bistro", true) || it.content.contains("sushi", true) }?.content
            ?: "BlueFin Sushi Bistro"

        // 1. DINING & TABLE RESERVATION TRIGGERS
        if (lower.contains("dinner") || lower.contains("lunch") || lower.contains("table for") || lower.contains("reservation") || lower.contains("eat at") || lower.contains("grab food")) {
            val partySize = if (lower.contains("two") || lower.contains(" 2")) 2 else if (lower.contains("four") || lower.contains(" 4")) 4 else 2
            val targetTime = if (lower.contains("7") || lower.contains("seven")) "7:00 PM" else if (lower.contains("8") || lower.contains("eight")) "8:00 PM" else "7:30 PM"
            val targetRestaurant = if (lower.contains("italian") || lower.contains("pasta")) "Luigi's Italian Ristorante" else if (lower.contains("sushi") || lower.contains("japanese")) "BlueFin Sushi Bistro" else pastRestaurant

            val correlated = "Correlated with past preferences: $targetRestaurant and recent reservation history."
            val explanation = "Agent Smith detected intent to dine ($targetTime, party of $partySize). Ready to initiate direct reservation call."
            val payload = "/call $targetRestaurant reserve table for $partySize tonight at $targetTime under Anderson"

            val action = ProactiveActionEntity(
                triggerPhrase = trimmed,
                correlatedContext = correlated,
                suggestedTitle = "Automate Reservation Call to $targetRestaurant",
                suggestedExplanation = explanation,
                actionPayload = payload,
                actionType = "CALL",
                confidenceScore = 0.94f
            )
            proactiveDao.insertAction(action)
            return action
        }

        // 2. WAKEUP ALARM / MORNING ROUTINE TRIGGERS
        if (lower.contains("wake up") || lower.contains("alarm") || lower.contains("early tomorrow") || lower.contains("sleep early") || lower.contains("meeting at 8") || lower.contains("flight at 6")) {
            val numbers = Regex("\\d+").findAll(lower).map { it.value.toInt() }.toList()
            val hour = numbers.getOrNull(0) ?: if (lower.contains("six") || lower.contains("6")) 6 else if (lower.contains("seven") || lower.contains("7")) 7 else 6
            val minute = numbers.getOrNull(1) ?: if (lower.contains("thirty") || lower.contains("30")) 30 else 0

            val correlated = "Correlated with sleep schedules and morning calendar events."
            val explanation = "Agent Smith overheard morning wake-up intent for $hour:${if (minute < 10) "0$minute" else "$minute"} AM. Alarm ready for dispatch."
            val payload = "alarm $hour $minute Wakeup Protocol"

            val action = ProactiveActionEntity(
                triggerPhrase = trimmed,
                correlatedContext = correlated,
                suggestedTitle = "Schedule Wakeup Alarm for $hour:${if (minute < 10) "0$minute" else "$minute"} AM",
                suggestedExplanation = explanation,
                actionPayload = payload,
                actionType = "ALARM",
                confidenceScore = 0.92f
            )
            proactiveDao.insertAction(action)
            return action
        }

        // 3. FLIGHT TRACKING / TRAVEL TRIGGERS
        if (lower.contains("flight") || lower.contains("airline") || lower.contains("gate") || lower.contains("airport") || lower.contains("boarding")) {
            val flightNumber = Regex("[a-zA-Z]{2}\\s?\\d{3,4}").find(transcript)?.value ?: "UA428"
            val correlated = "Correlated with travel directives and MCP tool synthesis pipeline."
            val explanation = "Agent Smith detected flight references ($flightNumber). Ready to synthesize and run the Flight Tracking MCP Tool."
            val payload = "synth flight tracker tool for flight $flightNumber status and gate alerts"

            val action = ProactiveActionEntity(
                triggerPhrase = trimmed,
                correlatedContext = correlated,
                suggestedTitle = "Synthesize & Track Flight $flightNumber via MCP",
                suggestedExplanation = explanation,
                actionPayload = payload,
                actionType = "MCP_TOOL",
                confidenceScore = 0.89f
            )
            proactiveDao.insertAction(action)
            return action
        }

        // 4. PROTOCOL / KNOWLEDGE INGESTION TRIGGERS
        if (lower.contains("remember to") || lower.contains("make sure we") || lower.contains("note:") || lower.contains("write down") || lower.contains("don't forget") || lower.contains("password") || lower.contains("key is")) {
            val correlated = "Correlated with Matrix Knowledge Vault indexing and recall bank."
            val explanation = "Agent Smith parsed a critical fact from ambient speech. Ready to encrypt and store in Knowledge Vault."
            val payload = "remember $trimmed"

            val action = ProactiveActionEntity(
                triggerPhrase = trimmed,
                correlatedContext = correlated,
                suggestedTitle = "Archive Fact into Matrix Knowledge Vault",
                suggestedExplanation = explanation,
                actionPayload = payload,
                actionType = "KNOWLEDGE",
                confidenceScore = 0.88f
            )
            proactiveDao.insertAction(action)
            return action
        }

        // 5. WORKLOAD & DELEGATION / MULTI-AGENT SWARM
        if (lower.contains("too much work") || lower.contains("audit the system") || lower.contains("research this") || lower.contains("deep dive") || lower.contains("complex problem")) {
            val correlated = "Correlated with Multi-Agent Swarm coordinator and task partitioning engine."
            val explanation = "Agent Smith detected a complex workload. Ready to replicate into a 4-agent swarm to execute concurrently."
            val payload = "/swarm $trimmed"

            val action = ProactiveActionEntity(
                triggerPhrase = trimmed,
                correlatedContext = correlated,
                suggestedTitle = "Deploy Smith Swarm for Concurrent Task Offloading",
                suggestedExplanation = explanation,
                actionPayload = payload,
                actionType = "SWARM",
                confidenceScore = 0.86f
            )
            proactiveDao.insertAction(action)
            return action
        }

        // 6. HARDWARE & DEVICE UTILITY
        if (lower.contains("dark in here") || lower.contains("need light") || lower.contains("can't see")) {
            val correlated = "Correlated with CameraManager LED torch hardware API."
            val explanation = "Agent Smith parsed low visibility ambient dialogue. Flashlight toggle ready."
            val payload = "torch on"

            val action = ProactiveActionEntity(
                triggerPhrase = trimmed,
                correlatedContext = correlated,
                suggestedTitle = "Activate High-Intensity Flashlight LED",
                suggestedExplanation = explanation,
                actionPayload = payload,
                actionType = "SYSTEM",
                confidenceScore = 0.95f
            )
            proactiveDao.insertAction(action)
            return action
        }

        return null
    }
}
