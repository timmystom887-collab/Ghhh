package com.example.agent.domain.usecase

import com.example.agent.data.local.entity.TaskEntity
import com.example.agent.data.repository.IAgentRepository
import kotlinx.coroutines.flow.Flow

class ManageTasksUseCase(
    private val repository: IAgentRepository
) {
    val tasks: Flow<List<TaskEntity>> = repository.tasks

    suspend fun scheduleTask(
        description: String,
        actionType: String,
        parameters: String,
        scheduleTime: Long,
        preAuthorized: Boolean = false
    ): Long {
        val task = TaskEntity(
            description = description,
            actionType = actionType,
            parameters = parameters,
            scheduleTime = scheduleTime,
            status = "pending",
            preAuthorized = preAuthorized,
            createdAt = System.currentTimeMillis()
        )
        return repository.insertTask(task)
    }

    suspend fun cancelTask(task: TaskEntity) {
        repository.deleteTask(task)
    }

    suspend fun completeTask(taskId: Long) {
        repository.updateTaskStatus(taskId, "completed")
    }

    suspend fun failTask(taskId: Long) {
        repository.updateTaskStatus(taskId, "failed")
    }
}
