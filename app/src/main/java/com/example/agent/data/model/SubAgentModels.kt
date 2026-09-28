package com.example.agent.data.model

data class SubAgentClarificationRequest(
    val id: String = java.util.UUID.randomUUID().toString(),
    val subAgentName: String, // e.g. "Hermes Agent-01", "Deep Research Specialist", "Smith Swarm-Replica 2", "Sentinel Guard"
    val subAgentRole: String, // e.g. "Hermes Multi-Turn Protocol", "Deep Vector Search", "Task Delegation Node"
    val question: String, // e.g. "Should I proceed with live web extraction or use local Room cache?"
    val suggestedOptions: List<String> = emptyList(), // e.g. ["Live Web Extraction", "Local Room Cache", "Both (Parallel)"]
    val contextSnippet: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class TerminalLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: String = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
    val source: String, // "SYS", "USER", "AGENT", "HERMES", "RESEARCH", "SWARM", "SENTINEL"
    val text: String,
    val isError: Boolean = false,
    val isCommand: Boolean = false
)
