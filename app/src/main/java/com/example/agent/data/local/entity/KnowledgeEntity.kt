package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_items")
data class KnowledgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "GENERAL", // "ARCHITECTURE", "TOOLS", "MISSIONS", "LOCAL_AI", "MCP", "PROTOCOLS"
    val tags: String = "", // Comma-separated tags, e.g. "matrix,phone,calling,reservations"
    val sourceUri: String = "system://matrix/vault",
    val docType: String = "ARTICLE", // "ARTICLE", "CODE_SNIPPET", "TRANSCRIPT", "MANUAL", "SPEC"
    val summary: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val accessCount: Int = 0
)
