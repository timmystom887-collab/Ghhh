package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary
import com.example.agent.util.AutomatedExecutionTrace
import com.example.agent.util.AutomatedSystemRoutine

@Composable
fun AutomatedSystemsCard(
    routines: List<AutomatedSystemRoutine>,
    executionTraces: List<AutomatedExecutionTrace>,
    onToggleRoutine: (String, Boolean) -> Unit,
    onExecuteRoutine: (String) -> Unit,
    onSynthesizeLogic: (String) -> Unit
) {
    var expandedRoutineId by remember { mutableStateOf<String?>(null) }
    var naturalLanguagePrompt by remember { mutableStateOf("") }
    var isSynthesizingNew by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MatrixGreenPrimary,
        unfocusedBorderColor = MatrixBorder,
        focusedLabelColor = MatrixGreenPrimary,
        unfocusedLabelColor = MatrixTextSecondary,
        focusedTextColor = MatrixTextPrimary,
        unfocusedTextColor = MatrixTextPrimary
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
                Icon(
                    Icons.Default.SettingsSuggest,
                    contentDescription = "Automated Systems",
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚙️ AUTOMATED SYSTEMS & LOGIC ENGINE",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "${routines.count { it.isEnabled }}/${routines.size} ACTIVE",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Autonomous triggers, telemetry guards, and deterministic sequential execution logic. Synthesize new workflows or review active systems below.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Synthesize New Logic Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ WRITE NEW AUTOMATED LOGIC SCRIPT",
                style = MaterialTheme.typography.labelMedium,
                color = MatrixGreenPrimary
            )
            IconButton(onClick = { isSynthesizingNew = !isSynthesizingNew }) {
                Icon(
                    if (isSynthesizingNew) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle Logic Writer",
                    tint = MatrixGreenPrimary
                )
            }
        }

        AnimatedVisibility(visible = isSynthesizingNew) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                OutlinedTextField(
                    value = naturalLanguagePrompt,
                    onValueChange = { naturalLanguagePrompt = it },
                    label = { Text("Describe workflow (e.g. When arriving home, check alarms & battery)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (naturalLanguagePrompt.isNotBlank()) {
                            onSynthesizeLogic(naturalLanguagePrompt)
                            naturalLanguagePrompt = ""
                            isSynthesizingNew = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenPrimary,
                        contentColor = MatrixBlack
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Synthesize", tint = MatrixBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Synthesize Structured Logic Rules with AI")
                }
            }
        }

        // List of Automated Systems
        routines.forEach { routine ->
            val isExpanded = expandedRoutineId == routine.id

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MatrixGreenContainer.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (routine.isEnabled) MatrixGreenPrimary.copy(alpha = 0.5f) else MatrixBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routine.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MatrixTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Trigger: ${routine.triggerType} (${routine.triggerCondition})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixGreenPrimary
                        )
                    }

                    Switch(
                        checked = routine.isEnabled,
                        onCheckedChange = { onToggleRoutine(routine.id, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MatrixGreenPrimary,
                            checkedTrackColor = MatrixGreenContainer,
                            uncheckedThumbColor = MatrixTextSecondary,
                            uncheckedTrackColor = MatrixBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = routine.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextSecondary
                )

                // Expandable Logic Steps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { expandedRoutineId = if (isExpanded) null else routine.id },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenContainer,
                            contentColor = MatrixGreenPrimary
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            if (isExpanded) "Hide Logic To Follow" else "View Logic To Follow (${routine.logicSteps.size} Steps)",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Button(
                        onClick = { onExecuteRoutine(routine.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MatrixBlack, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run Now", style = MaterialTheme.typography.labelSmall)
                    }
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(MatrixBlack, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "📋 SEQUENTIAL EXECUTION LOGIC TO FOLLOW:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        routine.logicSteps.forEachIndexed { idx, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(MatrixGreenPrimary.copy(alpha = 0.2f), CircleShape)
                                        .border(1.dp, MatrixGreenPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MatrixGreenPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🛡️ Fallback Policy: ${routine.fallbackPolicy}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixTextSecondary
                        )
                    }
                }
            }
        }

        // Live / Recent Execution Trace Viewer
        if (executionTraces.isNotEmpty()) {
            val latest = executionTraces.first()
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixBlack, RoundedCornerShape(10.dp))
                    .border(1.dp, MatrixGreenPrimary, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = MatrixGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LATEST EXECUTION TRACE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixGreenPrimary
                            )
                        }
                        Text(
                            text = "[${latest.status}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = latest.routineTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MatrixTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    latest.stepResults.forEach { (step, outcome) ->
                        Column(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(text = "• $step", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary)
                            Text(text = "  ↳ $outcome", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = latest.finalDecree,
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextPrimary
                    )
                }
            }
        }
    }
}
