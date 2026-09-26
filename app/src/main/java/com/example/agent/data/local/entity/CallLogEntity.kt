package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val contactName: String,
    val direction: String = "OUTGOING", // "OUTGOING" or "INCOMING"
    val callType: String = "Reservation", // "Reservation", "Verification", "Inquiry", "VoiceAssistant", "Direct"
    val objective: String,
    val summary: String, // Brief conversation summary / outcome
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val status: String = "COMPLETED" // "COMPLETED", "FAILED", "IN_PROGRESS", "MISSED"
)
