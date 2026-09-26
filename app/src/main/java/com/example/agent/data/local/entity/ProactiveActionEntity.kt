package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proactive_actions")
data class ProactiveActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val triggerPhrase: String, // Ambient audio transcription trigger
    val correlatedContext: String, // Memories, Knowledge records, or Telemetry correlated
    val suggestedTitle: String, // E.g. "Automate Dinner Reservation at BlueFin Sushi"
    val suggestedExplanation: String, // Reasoning why Smith recommends this
    val actionPayload: String, // Direct executable command or directive
    val actionType: String = "CALL", // "CALL", "ALARM", "KNOWLEDGE", "MCP_TOOL", "SMS", "SWARM", "SYSTEM"
    val confidenceScore: Float = 0.85f,
    val status: String = "PENDING", // "PENDING", "EXECUTED", "DISMISSED"
    val timestamp: Long = System.currentTimeMillis()
)
