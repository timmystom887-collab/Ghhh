package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.data.local.entity.ProactiveActionEntity
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenBright
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun ProactiveSentinelCard(
    isSentinelActive: Boolean,
    isDeepSleepActive: Boolean = false,
    isWakeWordEnabled: Boolean = true,
    customWakeWord: String = "Agent Smith",
    wakeWordSensitivity: String = "High",
    ambientTranscript: String,
    pendingActions: List<ProactiveActionEntity>,
    onToggleSentinel: (Boolean) -> Unit,
    onToggleDeepSleep: (Boolean) -> Unit = {},
    onFlushBuffer: () -> Unit = {},
    onUpdateWakeWordConfig: (enabled: Boolean, wakeWord: String, sensitivity: String) -> Unit = { _, _, _ -> },
    onExecuteAction: (ProactiveActionEntity) -> Unit,
    onDismissAction: (Long) -> Unit,
    onClearActions: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var wakeWordEnabledState by remember { mutableStateOf(isWakeWordEnabled) }
    var wakeWordInputState by remember { mutableStateOf(customWakeWord) }
    var wakeWordSensitivityState by remember { mutableStateOf(wakeWordSensitivity) }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RadarScale"
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
                        .scale(if (isSentinelActive && !isMuted) pulseScale else 1f)
                        .background(
                            if (isSentinelActive && !isMuted) MatrixGreenPrimary.copy(alpha = 0.25f) else MatrixGreenContainer,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isMuted) Icons.Default.MicOff else Icons.Default.Hearing,
                        contentDescription = "Sentinel Radar",
                        tint = if (isSentinelActive && !isMuted) MatrixGreenPrimary else MatrixTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "👁️ PROACTIVE SENTINEL AGENT",
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isSentinelActive) "100% Continuous Ambient Mic • Context Aware" else "Disabled • Standby Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSentinelActive) MatrixGreenBright else MatrixTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Switch(
                checked = isSentinelActive,
                onCheckedChange = onToggleSentinel,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MatrixGreenPrimary,
                    checkedTrackColor = MatrixGreenContainer,
                    uncheckedThumbColor = MatrixTextSecondary,
                    uncheckedTrackColor = MatrixBlack
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Autonomous background cognition engine. Proactively analyzes ambient speech, correlates with memories, past calls, and knowledge records to formulate actions before you even ask.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Deep Sleep & Resource Optimization Mode
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MatrixGreenContainer.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDeepSleepActive) MatrixGreenPrimary else MatrixBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🌙 DEEP SLEEP MODE",
                            style = MaterialTheme.typography.titleSmall,
                            color = MatrixGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isDeepSleepActive) MatrixGreenPrimary.copy(alpha = 0.2f) else MatrixBlack,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDeepSleepActive) MatrixGreenPrimary else MatrixBorder)
                        ) {
                            Text(
                                text = if (isDeepSleepActive) "BATTERY & RAM SAVER" else "OFF",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDeepSleepActive) MatrixGreenBright else MatrixTextSecondary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Switch(
                        checked = isDeepSleepActive,
                        onCheckedChange = onToggleDeepSleep,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MatrixBlack,
                            checkedTrackColor = MatrixGreenPrimary,
                            uncheckedTrackColor = MatrixBlack
                        )
                    )
                }

                Text(
                    text = "Periodically purges short-term audio buffer memory every 30s to maximize mobile battery life & lower processing overhead.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixTextSecondary,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onFlushBuffer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenContainer,
                            contentColor = MatrixGreenPrimary
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("🧹 Flush Memory Buffer Now", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Local Wake-Word Trigger Mechanism Box
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MatrixGreenContainer.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (wakeWordEnabledState) MatrixGreenPrimary else MatrixBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🎙️ LOCAL WAKE-WORD MECHANISM",
                            style = MaterialTheme.typography.titleSmall,
                            color = MatrixGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (wakeWordEnabledState) MatrixGreenPrimary.copy(alpha = 0.2f) else MatrixBlack,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (wakeWordEnabledState) MatrixGreenPrimary else MatrixBorder)
                        ) {
                            Text(
                                text = if (wakeWordEnabledState) "ACTIVE: '$wakeWordInputState'" else "OFF",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (wakeWordEnabledState) MatrixGreenBright else MatrixTextSecondary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Switch(
                        checked = wakeWordEnabledState,
                        onCheckedChange = { enabled ->
                            wakeWordEnabledState = enabled
                            onUpdateWakeWordConfig(enabled, wakeWordInputState, wakeWordSensitivityState)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MatrixBlack,
                            checkedTrackColor = MatrixGreenPrimary,
                            uncheckedTrackColor = MatrixBlack
                        )
                    )
                }

                Text(
                    text = "Allows customizing the exact trigger phrase that activates proactive listening directives beyond always-on mode (e.g. 'Agent Smith', 'Hey Smith', 'Computer', 'Jarvis').",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixTextSecondary,
                    fontSize = 10.sp
                )

                if (wakeWordEnabledState) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = wakeWordInputState,
                        onValueChange = { wakeWordInputState = it },
                        label = { Text("Custom Trigger Phrase (e.g. Agent Smith, Matrix)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MatrixGreenPrimary,
                            unfocusedBorderColor = MatrixBorder,
                            focusedLabelColor = MatrixGreenPrimary,
                            unfocusedLabelColor = MatrixTextSecondary,
                            focusedTextColor = MatrixTextPrimary,
                            unfocusedTextColor = MatrixTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detection Sensitivity:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixTextSecondary,
                            fontSize = 10.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("High", "Medium", "Strict").forEach { sensitivity ->
                                FilterChip(
                                    selected = wakeWordSensitivityState == sensitivity,
                                    onClick = {
                                        wakeWordSensitivityState = sensitivity
                                        onUpdateWakeWordConfig(wakeWordEnabledState, wakeWordInputState, sensitivity)
                                    },
                                    label = { Text(sensitivity, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MatrixGreenContainer,
                                        labelColor = MatrixTextPrimary,
                                        selectedContainerColor = MatrixGreenPrimary,
                                        selectedLabelColor = MatrixBlack
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            onUpdateWakeWordConfig(wakeWordEnabledState, wakeWordInputState, wakeWordSensitivityState)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(30.dp)
                    ) {
                        Text("Save Custom Trigger Phrase", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }

        AnimatedVisibility(visible = isSentinelActive) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                // Live Ambient Audio Spectrogram / Stream Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MatrixGreenContainer.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .border(1.dp, MatrixGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = "Cognition Stream",
                                tint = MatrixGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AMBIENT COGNITION STREAM",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixGreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row {
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mute",
                                    tint = if (isMuted) MatrixRedAlert else MatrixGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isMuted) {
                            "[Muted by Operator] Ambient audio pipeline paused."
                        } else if (ambientTranscript.isNotBlank()) {
                            "\"$ambientTranscript\""
                        } else {
                            "Listening to background speech... [Mention dinner, alarms, flights, notes, or tasks to trigger proactive intelligence]"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (ambientTranscript.isNotBlank()) MatrixTextPrimary else MatrixTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Proactive Recommendations Feed Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ PROACTIVE ACTIONS (${pendingActions.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    if (pendingActions.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixTextSecondary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (pendingActions.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MatrixBlack.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MatrixBorder)
                    ) {
                        Text(
                            text = "No pending actions. Agent Smith is continuously monitoring ambient audio and cross-referencing your neural memory bank.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixTextSecondary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pendingActions.forEach { action ->
                            ProactiveActionItem(
                                action = action,
                                onExecute = { onExecuteAction(action) },
                                onDismiss = { onDismissAction(action.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProactiveActionItem(
    action: ProactiveActionEntity,
    onExecute: () -> Unit,
    onDismiss: () -> Unit
) {
    val icon = when (action.actionType) {
        "CALL" -> Icons.Default.Call
        "ALARM" -> Icons.Default.Alarm
        "KNOWLEDGE" -> Icons.Default.Storage
        "MCP_TOOL" -> Icons.Default.Hub
        "SWARM" -> Icons.Default.GroupWork
        "SYSTEM" -> Icons.Default.FlashlightOn
        else -> Icons.Default.AutoAwesome
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixGreenContainer.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .border(1.dp, MatrixGreenPrimary, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = action.actionType,
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = action.suggestedTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Confidence: ${(action.confidenceScore * 100).toInt()}% • Heard: \"${action.triggerPhrase.take(30)}...\"",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = action.suggestedExplanation,
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "🔗 ${action.correlatedContext}",
            style = MaterialTheme.typography.labelSmall,
            color = MatrixGreenBright,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onExecute,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenPrimary,
                    contentColor = MatrixBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Execute", tint = MatrixBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Authorize & Execute", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }

            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MatrixTextSecondary
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dismiss", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
