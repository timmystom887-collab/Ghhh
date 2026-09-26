package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val description: String,
    val actionType: String,
    val parameters: String,
    val scheduleTime: Long,
    val recurrenceRule: String? = null,
    val status: String = "pending", // pending, running, completed, failed, paused
    val preAuthorized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
