package com.example.agent.domain.model

sealed class ToolResult {
    data class Success(
        val output: String,
        val metadata: Map<String, Any> = emptyMap()
    ) : ToolResult()

    data class PermissionRequired(
        val permission: String,
        val rationale: String
    ) : ToolResult()

    data class ConfirmationRequired(
        val actionName: String,
        val details: String,
        val confidenceScore: Float
    ) : ToolResult()

    data class Error(
        val code: String,
        val message: String,
        val cause: Throwable? = null
    ) : ToolResult()
}

enum class LlmErrorType {
    AUTHENTICATION_FAILED,
    RATE_LIMITED,
    TIMEOUT,
    NETWORK_UNAVAILABLE,
    INVALID_REQUEST,
    SERVER_ERROR,
    UNKNOWN
}

sealed class LlmResult {
    data class Success(
        val text: String,
        val promptTokens: Int = 0,
        val completionTokens: Int = 0
    ) : LlmResult()

    data class Error(
        val errorType: LlmErrorType,
        val message: String,
        val cause: Throwable? = null
    ) : LlmResult()
}
