package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.data.model.ActiveSubAgentStatus
import com.example.agent.data.model.SubAgentStatusState
import com.example.agent.data.model.TerminalLogEntry
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun MatrixTerminalConsole(
    modifier: Modifier = Modifier,
    logs: List<TerminalLogEntry>,
    isExpanded: Boolean,
    subAgentStatus: ActiveSubAgentStatus = ActiveSubAgentStatus(),
    onToggleExpand: () -> Unit,
    onExecuteCommand: (String) -> Unit,
    onClearLogs: () -> Unit,
    onTriggerSampleClarification: () -> Unit
) {
    var commandInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size, isExpanded) {
        if (logs.isNotEmpty() && isExpanded) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    // Determine visual status colors and label
    val statusColor = when (subAgentStatus.state) {
        SubAgentStatusState.IDLE -> MatrixGreenPrimary
        SubAgentStatusState.PROCESSING -> Color(0xFF00E5FF)
        SubAgentStatusState.WAITING_FOR_USER -> Color(0xFFFFD600)
    }

    val statusLabel = when (subAgentStatus.state) {
        SubAgentStatusState.IDLE -> "Idle"
        SubAgentStatusState.PROCESSING -> "Processing"
        SubAgentStatusState.WAITING_FOR_USER -> "Waiting for User"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MatrixBlack.copy(alpha = 0.92f))
            .border(1.5.dp, MatrixGreenPrimary.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
        color = MatrixBlack.copy(alpha = 0.90f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onToggleExpand() }
                        .weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ROOT@AGENT-SMITH:~#",
                        color = MatrixGreenPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("terminal_header_title")
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // Sub-Agent Visual Indicator Pill
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                            .border(1.dp, statusColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("terminal_subagent_indicator")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "[${subAgentStatus.agentName}: $statusLabel]",
                                color = statusColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.testTag("terminal_subagent_status_text")
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onClearLogs,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("terminal_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Terminal",
                            tint = MatrixTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("terminal_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Console",
                            tint = MatrixGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quick Workflow Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AssistChip(
                    onClick = { commandInput = "/hermes "; onToggleExpand() },
                    label = { Text("/hermes", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MatrixGreenPrimary) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MatrixSurface),
                    border = AssistChipDefaults.assistChipBorder(borderColor = MatrixBorder, enabled = true),
                    modifier = Modifier.testTag("terminal_chip_hermes")
                )
                AssistChip(
                    onClick = { commandInput = "/research "; onToggleExpand() },
                    label = { Text("/research", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF00E5FF)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MatrixSurface),
                    border = AssistChipDefaults.assistChipBorder(borderColor = MatrixBorder, enabled = true),
                    modifier = Modifier.testTag("terminal_chip_research")
                )
                AssistChip(
                    onClick = { commandInput = "/swarm "; onToggleExpand() },
                    label = { Text("/swarm", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFFFD600)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MatrixSurface),
                    border = AssistChipDefaults.assistChipBorder(borderColor = MatrixBorder, enabled = true),
                    modifier = Modifier.testTag("terminal_chip_swarm")
                )
                AssistChip(
                    onClick = onTriggerSampleClarification,
                    label = { Text("/ask ❓", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFFF4081)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MatrixSurface),
                    border = AssistChipDefaults.assistChipBorder(borderColor = MatrixBorder, enabled = true),
                    modifier = Modifier.testTag("terminal_chip_clarify")
                )
            }

            // Expandable Monospace Console Output Buffer
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(MatrixSurface.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                        .border(1.dp, MatrixBorder, RoundedCornerShape(8.dp))
                        .padding(6.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("terminal_log_column")
                    ) {
                        items(logs, key = { it.id }) { log ->
                            val textColor = when {
                                log.isError -> MatrixRedAlert
                                log.isCommand -> Color(0xFF00E5FF)
                                log.source == "SYS" -> MatrixTextSecondary
                                log.source == "HERMES" -> MatrixGreenPrimary
                                log.source == "RESEARCH" -> Color(0xFF00E5FF)
                                log.source == "SWARM" -> Color(0xFFFFD600)
                                else -> MatrixTextPrimary
                            }
                            Text(
                                text = "[${log.timestamp}] [${log.source}] ${log.text}",
                                color = textColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Command Prompt Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$",
                    color = MatrixGreenPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    placeholder = {
                        Text(
                            text = "Type /hermes, /research, /swarm or prompt...",
                            fontSize = 12.sp,
                            color = MatrixTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MatrixSurface,
                        unfocusedContainerColor = MatrixSurface,
                        focusedBorderColor = MatrixGreenPrimary,
                        unfocusedBorderColor = MatrixBorder,
                        focusedTextColor = MatrixGreenPrimary,
                        unfocusedTextColor = MatrixTextPrimary
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MatrixGreenPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_input_field")
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (commandInput.isNotBlank()) {
                            onExecuteCommand(commandInput)
                            commandInput = ""
                        }
                    },
                    modifier = Modifier
                        .background(MatrixGreenPrimary, RoundedCornerShape(10.dp))
                        .size(44.dp)
                        .testTag("terminal_submit_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Execute Command",
                        tint = MatrixBlack
                    )
                }
            }
        }
    }
}
