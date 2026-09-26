package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.agent.util.ThinkingMethod
import com.example.agent.util.ThinkingStep
import com.example.agent.util.ThinkingTrace

@Composable
fun ThinkingMethodsCard(
    methods: List<ThinkingMethod>,
    activeMethodId: String,
    lastTrace: ThinkingTrace?,
    onSelectMethod: (String) -> Unit,
    onExecuteTest: (methodId: String, query: String) -> Unit,
    onCreateCustomMethod: (name: String, tagline: String, desc: String, steps: List<ThinkingStep>, prompt: String) -> Unit
) {
    var selectedId by remember(activeMethodId) { mutableStateOf(activeMethodId) }
    val currentMethod = methods.firstOrNull { it.id == selectedId } ?: methods.firstOrNull()
    var isCreatingCustom by remember { mutableStateOf(false) }
    var testQueryInput by remember { mutableStateOf("How can we achieve deterministic software autonomy with zero security flaws?") }

    // Custom creation form state
    var customName by remember { mutableStateOf("") }
    var customTagline by remember { mutableStateOf("") }
    var customDesc by remember { mutableStateOf("") }
    var customStep1 by remember { mutableStateOf("Step 1: Identify foundational axioms") }
    var customStep2 by remember { mutableStateOf("Step 2: Stress-test failure vectors") }
    var customStep3 by remember { mutableStateOf("Step 3: Synthesize optimal execution path") }

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
        // Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = "Cognitive Frameworks",
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🧠 COGNITIVE THINKING FRAMEWORKS",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "${methods.size} METHODS",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Select, customize, or execute structured reasoning methodologies. Each framework enforces strict cognitive steps before formulating conclusions.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal chips of thinking methods
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            methods.forEach { method ->
                FilterChip(
                    selected = method.id == selectedId,
                    onClick = {
                        selectedId = method.id
                        onSelectMethod(method.id)
                    },
                    label = { Text("${method.icon} ${method.name}") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MatrixGreenContainer,
                        labelColor = MatrixTextPrimary,
                        selectedContainerColor = MatrixGreenPrimary,
                        selectedLabelColor = MatrixBlack
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = method.id == selectedId,
                        borderColor = MatrixBorder,
                        selectedBorderColor = MatrixGreenPrimary
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Detailed view of selected method
        if (currentMethod != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixGreenContainer.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentMethod.icon} ${currentMethod.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MatrixGreenPrimary
                        )
                        if (currentMethod.id == activeMethodId) {
                            Text(
                                text = "● ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentMethod.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentMethod.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "📋 STRUCTURED LOGIC TO FOLLOW:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    currentMethod.stepsToFollow.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(MatrixGreenPrimary.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, MatrixGreenPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${step.stepNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatrixGreenPrimary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.stepName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixTextPrimary
                                )
                                Text(
                                    text = step.executionRule,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatrixTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test execution section
            Text(
                text = "⚡ TEST ACTIVE THINKING METHOD ON QUERY:",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixGreenPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = testQueryInput,
                onValueChange = { testQueryInput = it },
                label = { Text("Directive or Problem Statement") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    if (testQueryInput.isNotBlank()) {
                        onExecuteTest(currentMethod.id, testQueryInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenPrimary,
                    contentColor = MatrixBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run Thinking Method", tint = MatrixBlack)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Execute Reasoning Trace via ${currentMethod.name}")
            }
        }

        // Active / Recent Thinking Trace Display
        if (lastTrace != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixBlack, RoundedCornerShape(10.dp))
                    .border(1.dp, MatrixGreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔬 REASONING TRACE [${lastTrace.methodName}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixGreenPrimary
                        )
                        Text(
                            text = "${lastTrace.durationMs}ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    lastTrace.steps.forEach { step ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Step ${step.stepNumber}: ${step.stepTitle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixGreenPrimary
                                )
                                Text(
                                    text = "${(step.confidence * 100).toInt()}% certainty",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatrixTextSecondary
                                )
                            }
                            Text(
                                text = step.reasoning,
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✨ Synthesis: ${lastTrace.synthesisSummary}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Create Custom Thinking Method Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ CREATE CUSTOM THINKING METHOD",
                style = MaterialTheme.typography.labelMedium,
                color = MatrixGreenPrimary
            )
            IconButton(onClick = { isCreatingCustom = !isCreatingCustom }) {
                Icon(
                    if (isCreatingCustom) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle Form",
                    tint = MatrixGreenPrimary
                )
            }
        }

        AnimatedVisibility(visible = isCreatingCustom) {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Method Name (e.g. Asymmetric Heuristic)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customTagline,
                    onValueChange = { customTagline = it },
                    label = { Text("Core Tagline") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customDesc,
                    onValueChange = { customDesc = it },
                    label = { Text("Description & Philosophy") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customStep1,
                    onValueChange = { customStep1 = it },
                    label = { Text("Step 1 Execution Rule") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customStep2,
                    onValueChange = { customStep2 = it },
                    label = { Text("Step 2 Execution Rule") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customStep3,
                    onValueChange = { customStep3 = it },
                    label = { Text("Step 3 Execution Rule") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (customName.isNotBlank()) {
                            val steps = listOf(
                                ThinkingStep(1, "Phase 1: Ingestion", "Initial analysis", customStep1),
                                ThinkingStep(2, "Phase 2: Evaluation", "Risk & utility calculation", customStep2),
                                ThinkingStep(3, "Phase 3: Synthesis", "Final decree formulation", customStep3)
                            )
                            onCreateCustomMethod(
                                customName,
                                customTagline.ifBlank { "Custom Cognitive Framework" },
                                customDesc.ifBlank { "User-defined structured reasoning protocol." },
                                steps,
                                "Follow custom steps: $customStep1 -> $customStep2 -> $customStep3."
                            )
                            isCreatingCustom = false
                            customName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenContainer,
                        contentColor = MatrixGreenPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = MatrixGreenPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register Custom Method in Matrix Memory")
                }
            }
        }
    }
}
