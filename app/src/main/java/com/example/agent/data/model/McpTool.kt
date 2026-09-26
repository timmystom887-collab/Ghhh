package com.example.agent.data.model

data class McpServer(
    val serverId: String,
    val name: String,
    val endpoint: String,
    val description: String,
    val transport: String = "HTTP_STREAMED_SSE", // "HTTP_STREAMED_SSE", "STREAMABLE_HTTP", "LOCAL_DEVICE", "STDIO"
    val isInstalled: Boolean = true,
    val isConnected: Boolean = true,
    val isStreamed: Boolean = true,
    val toolsCount: Int = 0,
    val category: String = "GENERAL", // "WEB_SEARCH", "CODE_GIT", "BROWSER_AUTOMATION", "DATABASE", "TELEPHONY", "HARDWARE", "COMMUNICATION", "KNOWLEDGE"
    val tags: String = "",
    val authTokenOrHeader: String = "",
    val icon: String = "🌐"
)

data class McpTool(
    val name: String,
    val serverId: String,
    val description: String,
    val inputSchema: Map<String, String>, // Param name to type/desc
    val isDynamic: Boolean = false,
    val isStreamed: Boolean = false,
    val executionHandler: suspend (Map<String, String>) -> String
)
