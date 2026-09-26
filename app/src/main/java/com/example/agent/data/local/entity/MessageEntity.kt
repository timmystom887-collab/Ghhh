package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sender: String, // "user", "agent", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "text", // "text", "options", "scheduling", "confirmation", "onboarding"
    val metadata: String? = null
)
