package com.example.agent.domain.usecase

import com.example.agent.data.repository.IAgentRepository
import com.example.agent.domain.model.ToolResult
import com.example.agent.util.MatrixToolRegistry

class ExecuteToolUseCase(
    private val repository: IAgentRepository,
    private val toolRegistry: MatrixToolRegistry
) {
    suspend operator fun invoke(
        toolName: String,
        params: Map<String, String> = emptyMap()
    ): ToolResult {
        return try {
            val output = toolRegistry.executeTool(toolName, params)
            ToolResult.Success(output = output, metadata = params)
        } catch (e: SecurityException) {
            ToolResult.PermissionRequired(
                permission = toolName,
                rationale = e.message ?: "Permission required to execute $toolName"
            )
        } catch (e: Exception) {
            ToolResult.Error(
                code = "TOOL_EXECUTION_FAILED",
                message = e.localizedMessage ?: "Failed to execute tool $toolName",
                cause = e
            )
        }
    }
}
