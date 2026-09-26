package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.data.local.entity.MessageEntity
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixSurfaceVariant
import com.example.agent.ui.theme.MatrixTextMuted
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun MessageCard(message: MessageEntity) {
    val isUser = message.sender == "user"
    val isSystem = message.sender == "system"
    val isTool = message.type == "tool_execution"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    
    val backgroundColor = when {
        isTool -> MatrixSurface
        isUser -> MatrixGreenContainer
        isSystem -> MatrixSurfaceVariant
        else -> MatrixSurface
    }
    val borderColor = when {
        isTool -> MatrixGreenPrimary
        isUser -> MatrixGreenPrimary
        isSystem -> MatrixTextMuted
        else -> MatrixBorder
    }
    val tag = when {
        isTool -> "⚙️ MATRIX TOOL EXECUTION // DEVICE ACTION"
        isUser -> "👤 USER // TERMINAL"
        isSystem -> "⚡ MATRIX SYSTEM PROTOCOL"
        else -> "🕶️ AGENT SMITH // NEURAL CORE"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 5.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .border(if (isTool) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isTool) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = "Tool",
                            tint = MatrixGreenPrimary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUser || isTool) MatrixGreenPrimary else MatrixTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatrixTextPrimary
                )
            }
        }
    }
}
