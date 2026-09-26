package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.data.local.entity.MemoryEntity
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun MatrixMemoryCard(
    memories: List<MemoryEntity>,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Memory,
                    contentDescription = "Neural Memory Vault",
                    tint = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "🧠 MATRIX NEURAL MEMORY VAULT",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "${memories.size} NODES",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Persistent user facts and past query telemetry stored in Room SQLite database.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (memories.isEmpty()) {
            Text(
                text = "No neural memory nodes recorded yet. Chat or execute commands to populate.",
                style = MaterialTheme.typography.bodySmall,
                color = MatrixTextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column {
                memories.takeLast(6).reversed().forEach { mem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(MatrixGreenContainer.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CATEGORY: [${mem.category.uppercase()}]",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixGreenPrimary
                            )
                            Text(
                                text = mem.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MatrixTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onClearAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixRedAlert
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Purge", tint = MatrixRedAlert)
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text(text = "Purge Matrix Neural Memory", color = MatrixRedAlert)
            }
        }
    }
}
