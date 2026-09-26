package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String, // "work", "emotion", "preference", "collaboration"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
