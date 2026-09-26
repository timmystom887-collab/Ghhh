package com.example.agent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val skillName: String,
    val category: String, // "MCP_TOOL", "SYNTHESIZED_SKILL", "PHONE_ACTION", "WORKFLOW"
    val description: String,
    val triggerKeywords: String, // Comma-separated triggers
    val parametersSchema: String, // JSON schema or parameter names
    val executionLogic: String, // Executable command, JS/Kotlin script, or intent template
    val isAutoCreated: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val usageCount: Int = 0
)
