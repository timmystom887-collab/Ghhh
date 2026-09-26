package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.data.local.entity.TaskEntity
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun TaskManagerCard(tasks: List<TaskEntity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "⚡ MATRIX PROCESS & TASK MANAGER",
            style = MaterialTheme.typography.titleMedium,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (tasks.isEmpty()) {
            Text(
                text = "All Matrix sub-routines idling. No pending asynchronous jobs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MatrixTextSecondary
            )
        } else {
            Column {
                tasks.forEach { task ->
                    Text(
                        text = "• [PID-${task.id}] ${task.description} -> status: [${task.status}]",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextPrimary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
