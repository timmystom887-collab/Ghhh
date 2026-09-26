package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenBright
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary
import kotlin.math.cos
import kotlin.math.sin

data class OrchestratedSubAgent(
    val id: String,
    val designation: String,
    val role: String,
    val status: String, // "ACTIVE", "PROCESSING", "SYNTHESIZING", "CALLING", "IDLE", "PAUSED"
    val activeTask: String,
    val progress: Float,
    val tokensProcessed: Int,
    val latencyMs: Int,
    val iconType: String,
    var priorityScore: Int = 50 // 100 = CRITICAL, 75 = HIGH, 50 = MEDIUM, 25 = LOW
)

@Composable
fun SubAgentOrchestratorVisualizer(
    isMasterActive: Boolean = true,
    subAgents: List<OrchestratedSubAgent> = defaultSubAgents(),
    onScaleSwarm: (Int) -> Unit = {},
    onTriggerTask: (String) -> Unit = {},
    onToggleAgentState: (String) -> Unit = {},
    onBoostPriority: (String) -> Unit = {}
) {
    var selectedAgentId by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember { mutableStateOf(true) }

    // Dynamic priority queue list state
    val priorityQueue = remember(subAgents) {
        mutableStateListOf(*subAgents.toTypedArray()).apply {
            sortByDescending { it.priorityScore }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "TopologyOrbit")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitRot"
    )
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CoreGlow"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MatrixGreenContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.GroupWork,
                        contentDescription = "Swarm Orchestrator",
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "🕶️ PRIORITY TASK ORCHESTRATOR TOPOLOGY",
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Priority Queue • ${priorityQueue.count { it.status != "IDLE" && it.status != "PAUSED" }} Active Sub-Nodes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenBright,
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MatrixGreenContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, MatrixGreenPrimary)
            ) {
                Text(
                    text = "PRIORITY SYNCED",
                    color = MatrixGreenPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Neural Orbit Visualizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(MatrixBlack.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .border(1.dp, MatrixBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(210.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val orbitRadius = (size.height.coerceAtMost(size.width) / 2f) - 32f

                // Draw background energy rings
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(MatrixGreenPrimary.copy(alpha = 0.2f * pulseGlow), Color.Transparent),
                        center = center,
                        radius = orbitRadius * 1.2f
                    ),
                    radius = orbitRadius,
                    center = center,
                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )

                drawCircle(
                    color = MatrixBorder.copy(alpha = 0.35f),
                    radius = orbitRadius * 0.55f,
                    center = center,
                    style = Stroke(width = 1f)
                )

                // Sub-Agent Nodes coordinates
                val agentCount = priorityQueue.size.coerceAtLeast(1)
                priorityQueue.forEachIndexed { index, agent ->
                    val angleDeg = orbitAngle + (index * (360f / agentCount))
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val nodeX = center.x + (orbitRadius * cos(angleRad)).toFloat()
                    val nodeY = center.y + (orbitRadius * sin(angleRad)).toFloat()
                    val nodePos = Offset(nodeX, nodeY)

                    val isActive = agent.status == "ACTIVE" || agent.status == "PROCESSING" || agent.status == "CALLING"

                    // Draw connecting synapse beam
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MatrixGreenPrimary.copy(alpha = if (isActive) 0.8f else 0.25f),
                                if (isActive) MatrixGreenBright.copy(alpha = 0.9f) else MatrixBorder.copy(alpha = 0.15f)
                            ),
                            start = center,
                            end = nodePos
                        ),
                        start = center,
                        end = nodePos,
                        strokeWidth = if (agent.priorityScore >= 90) 3.5f else if (isActive) 2.2f else 1f,
                        cap = StrokeCap.Round
                    )

                    // Draw orbiting child node
                    val nodeColor = when {
                        agent.priorityScore >= 90 -> Color(0xFFFF3366) // Critical
                        agent.priorityScore >= 70 -> Color(0xFFFFB700) // High
                        agent.status == "CALLING" -> Color(0xFF00E5FF)
                        agent.status == "SYNTHESIZING" -> Color(0xFFFFD700)
                        agent.status == "PAUSED" -> Color(0xFFFF5555)
                        else -> MatrixGreenBright
                    }

                    drawCircle(
                        color = nodeColor.copy(alpha = 0.35f),
                        radius = (if (agent.priorityScore >= 90) 20f else 16f) * if (isActive) pulseGlow else 1f,
                        center = nodePos
                    )
                    drawCircle(
                        color = nodeColor,
                        radius = if (agent.priorityScore >= 90) 11f else 8f,
                        center = nodePos
                    )
                }

                // Draw Center Coordinator Node (Smith Prime)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(MatrixGreenPrimary, MatrixGreenContainer),
                        center = center,
                        radius = 28f * pulseGlow
                    ),
                    radius = 20f * pulseGlow,
                    center = center
                )
                drawCircle(
                    color = MatrixGreenBright,
                    radius = 10f,
                    center = center
                )
            }

            // Overlay label on center node
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SMITH PRIME",
                    color = MatrixGreenBright,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "PRIORITY QUEUE",
                    color = MatrixTextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Sub-Agents Priority Queue Task Cards List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRIORITY QUEUE ORDERING:",
                style = MaterialTheme.typography.labelMedium,
                color = MatrixGreenPrimary,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    if (isExpanded) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = "Expand",
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                priorityQueue.forEachIndexed { index, agent ->
                    SubAgentRowCard(
                        queueIndex = index + 1,
                        agent = agent,
                        isSelected = selectedAgentId == agent.id,
                        onClick = {
                            selectedAgentId = if (selectedAgentId == agent.id) null else agent.id
                        },
                        onTogglePause = { onToggleAgentState(agent.id) },
                        onExecuteTask = { onTriggerTask(agent.designation) },
                        onBoostPriority = {
                            agent.priorityScore = (agent.priorityScore + 25).coerceAtMost(100)
                            priorityQueue.sortByDescending { it.priorityScore }
                            onBoostPriority(agent.id)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Swarm Management Quick Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onScaleSwarm(priorityQueue.size + 1) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenPrimary,
                    contentColor = MatrixBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Replicate", tint = MatrixBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Replicate Node", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }

            Button(
                onClick = { onTriggerTask("Synchronize and audit all active sub-agent threads") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixGreenPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = MatrixGreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Swarm", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun SubAgentRowCard(
    queueIndex: Int,
    agent: OrchestratedSubAgent,
    isSelected: Boolean,
    onClick: () -> Unit,
    onTogglePause: () -> Unit,
    onExecuteTask: () -> Unit,
    onBoostPriority: () -> Unit
) {
    val icon = when (agent.iconType) {
        "CALL" -> Icons.Default.Call
        "RECON" -> Icons.Default.Security
        "MCP" -> Icons.Default.Hub
        "RAG" -> Icons.Default.Storage
        "SENTINEL" -> Icons.Default.Hearing
        else -> Icons.Default.AutoAwesome
    }

    val (priorityTag, priorityColor) = when {
        agent.priorityScore >= 90 -> "CRITICAL" to Color(0xFFFF3366)
        agent.priorityScore >= 70 -> "HIGH" to Color(0xFFFFB700)
        agent.priorityScore >= 40 -> "MEDIUM" to MatrixGreenPrimary
        else -> "LOW" to MatrixTextSecondary
    }

    val statusColor = when (agent.status) {
        "ACTIVE", "PROCESSING" -> MatrixGreenPrimary
        "CALLING" -> Color(0xFF00E5FF)
        "SYNTHESIZING" -> Color(0xFFFFD700)
        "PAUSED" -> Color(0xFFFF5555)
        else -> MatrixTextSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MatrixGreenContainer.copy(alpha = if (isSelected) 0.5f else 0.3f))
            .border(1.dp, if (isSelected) priorityColor else MatrixBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$queueIndex",
                    color = priorityColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Icon(
                    icon,
                    contentDescription = agent.role,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = agent.designation,
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = agent.role,
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = priorityColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor)
                ) {
                    Text(
                        text = priorityTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onBoostPriority,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = priorityColor,
                        contentColor = MatrixBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = "Boost", tint = MatrixBlack, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("BOOST", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "⚡ Task: ${agent.activeTask}",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextPrimary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { agent.progress },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = priorityColor,
            trackColor = MatrixBlack
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = "Tokens", tint = MatrixTextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${agent.tokensProcessed} tokens • ${agent.latencyMs}ms latency",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextSecondary,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = onExecuteTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenPrimary,
                        contentColor = MatrixBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Offload Directive", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                }
            }
        }
    }
}

fun defaultSubAgents(): List<OrchestratedSubAgent> = listOf(
    OrchestratedSubAgent(
        id = "agent_telephony",
        designation = "Smith-Telephony",
        role = "Voice Mission & ACTION_CALL Assistant",
        status = "ACTIVE",
        activeTask = "Monitoring telephony channel for restaurant reservations & verifications",
        progress = 0.85f,
        tokensProcessed = 420,
        latencyMs = 45,
        iconType = "CALL",
        priorityScore = 95
    ),
    OrchestratedSubAgent(
        id = "agent_recon",
        designation = "Smith-Recon",
        role = "System Telemetry, Sensors & Battery Auditor",
        status = "PROCESSING",
        activeTask = "Auditing device sensors, power telemetry, and memory bank consistency",
        progress = 0.65f,
        tokensProcessed = 310,
        latencyMs = 38,
        iconType = "RECON",
        priorityScore = 50
    ),
    OrchestratedSubAgent(
        id = "agent_mcp",
        designation = "Smith-MCP",
        role = "Model Context Protocol & Autonomous Tool Synthesizer",
        status = "ACTIVE",
        activeTask = "Evaluating missing device capabilities for on-demand JSON-RPC compilation",
        progress = 0.90f,
        tokensProcessed = 890,
        latencyMs = 62,
        iconType = "MCP",
        priorityScore = 75
    ),
    OrchestratedSubAgent(
        id = "agent_rag",
        designation = "Smith-RAG",
        role = "Matrix Knowledge Vault & Vector Indexing Shard",
        status = "IDLE",
        activeTask = "Vector index standby for document retrieval and offline context injection",
        progress = 1.0f,
        tokensProcessed = 150,
        latencyMs = 12,
        iconType = "RAG",
        priorityScore = 25
    ),
    OrchestratedSubAgent(
        id = "agent_sentinel",
        designation = "Smith-Sentinel",
        role = "100% Always-On Ambient Audio Cognition Hub",
        status = "ACTIVE",
        activeTask = "Streaming ambient audio buffer and cross-referencing memory triggers",
        progress = 0.95f,
        tokensProcessed = 740,
        latencyMs = 28,
        iconType = "SENTINEL",
        priorityScore = 80
    )
)
