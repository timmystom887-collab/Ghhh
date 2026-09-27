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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    if (message.content.contains("🔄 [Qwen 3 Thought-Chain Re-Prompting Core Initiated]")) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Qwen3ThoughtChainCard(content = message.content)
        }
        return
    }
    
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

@Composable
fun Qwen3ThoughtChainCard(content: String) {
    var isExpanded by remember { mutableStateOf(true) }
    
    val lines = content.split("\n")
    val disclosure = lines.filter { it.contains("Warning") || it.contains("private weights") || it.contains("boundaries") }
        .joinToString("\n") { it.replace("*", "").trim() }
    
    val cycles = lines.filter { it.contains("Cycle") }
    val decompositionSteps = lines.filter { it.matches(Regex("^\\d+\\.\\s+.*")) }
    val body = lines.filter { 
        !it.contains("Warning") && !it.contains("private weights") && 
        !it.matches(Regex("^\\d+\\.\\s+.*")) && !it.contains("Cycle") && 
        !it.contains("Initiated") && !it.contains("───") && 
        !it.contains("Decomposition") && !it.contains("boundaries") && 
        !it.contains("Qwen 3") && !it.contains("Qwen 1.7B") 
    }.joinToString("\n").trim()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixBlack, RoundedCornerShape(12.dp))
            .border(1.5.dp, MatrixGreenPrimary, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Active Core",
                tint = MatrixGreenPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🕶️ QWEN 3 // OFFLINE NEURAL CORE",
                style = MaterialTheme.typography.labelMedium,
                color = MatrixGreenPrimary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { isExpanded = !isExpanded }) {
                Text(
                    text = if (isExpanded) "COLLAPSE THOUGHTS" else "EXPAND THOUGHTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixGreenPrimary
                )
            }
        }

        if (disclosure.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(MatrixSurfaceVariant, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "⚠️ SLM Alert: " + disclosure.take(160) + "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextSecondary
                )
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🔄 ACTIVE COGNITIVE CYCLE STEPS:",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextMuted
            )
            
            cycles.forEachIndexed { idx, cycle ->
                val cleaned = cycle.replace("*", "").replace("🔄", "").replace("💬", "").replace("🛡️", "").replace("⚡", "").trim()
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MatrixGreenPrimary, RoundedCornerShape(100.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "L${idx+1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixBlack
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cleaned,
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🧠 ATOMIC TASK DECOMPOSITION SEQUENCE:",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextMuted
            )

            decompositionSteps.forEach { step ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = "Step Icon",
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.padding(2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.trim(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixGreenPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (body.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, MatrixBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "⚡ CONSOLIDATED RESPONSE EXECUTION:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MatrixTextPrimary
                    )
                }
            }
        }
    }
}
