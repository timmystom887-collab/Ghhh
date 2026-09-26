package com.example.agent.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.agent.data.repository.AgentRepository

class TaskWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = AgentRepository(applicationContext)
        val taskId = inputData.getLong("task_id", -1L)
        val description = inputData.getString("description") ?: "Scheduled Task"

        try {
            repository.insertMessage(
                com.example.agent.data.local.entity.MessageEntity(
                    sender = "system",
                    content = "Executing scheduled task: $description",
                    type = "text"
                )
            )
            if (taskId != -1L) {
                repository.updateTaskStatus(taskId, "completed")
            }
            return Result.success()
        } catch (e: Exception) {
            if (taskId != -1L) {
                repository.updateTaskStatus(taskId, "failed")
            }
            return Result.failure()
        }
    }
}
