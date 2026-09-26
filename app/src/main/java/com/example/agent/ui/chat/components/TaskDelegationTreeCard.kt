package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

data class SubTaskNode(
    val title: String,
    val assignedModel: String,
    val status: String
)

@Composable
fun TaskDelegationTreeCard(
    parentTask: String,
    subTasks: List<SubTaskNode>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "🌲 MATRIX SUB-AGENT DELEGATION TREE",
            style = MaterialTheme.typography.titleMedium,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Root Protocol: $parentTask",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        subTasks.forEachIndexed { index, node ->
            val prefix = if (index == subTasks.size - 1) "└── " else "├── "
            Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)) {
                Text(
                    text = "$prefix ${node.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatrixTextPrimary
                )
                Row(modifier = Modifier.padding(start = 24.dp, top = 2.dp)) {
                    Text(
                        text = "⚡ Assigned: ${node.assignedModel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "status: [${node.status}]",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextSecondary
                    )
                }
            }
        }
    }
}
