package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.domain.hermes.HermesExecutionStep
import com.example.agent.domain.hermes.HermesSkillLibrary
import com.example.agent.domain.hermes.HermesToolDefinition
import com.example.agent.domain.research.ResearchDossier
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixDarkGray
import com.example.agent.ui.theme.MatrixGoldWarning
import com.example.agent.ui.theme.MatrixGreenBright
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixSurfaceVariant
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun HermesAgentCard(
    onExecuteGoal: (String) -> Unit,
    onRunDeepResearch: (String) -> Unit,
    liveSteps: List<HermesExecutionStep> = emptyList(),
    latestDossier: ResearchDossier? = null,
    isProcessing: Boolean = false,
    progressStatus: String = "",
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var goalInput by remember { mutableStateOf("") }
    var researchInput by remember { mutableStateOf("") }

    val tabs = listOf("⚡ Autonomous Goal Loop", "🔬 Deep Research Agent", "🛠️ Tool Catalog (Hermes)")

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MatrixGreenPrimary,
        unfocusedBorderColor = MatrixBorder,
        focusedLabelColor = MatrixGreenPrimary,
        unfocusedLabelColor = MatrixTextSecondary,
        focusedTextColor = MatrixTextPrimary,
        unfocusedTextColor = MatrixTextPrimary
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, MatrixGreenPrimary, RoundedCornerShape(16.dp))
            .testTag("hermes_agent_card"),
        colors = CardDefaults.cardColors(containerColor = MatrixSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MatrixGreenPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HERMES AGENT ENGINE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MatrixGreenBright
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(MatrixGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "XML <tool_call> ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MatrixGreenPrimary
                            )
                        }
                    }
                    Text(
                        text = "Nous Research Hermes 3 Agentic Loop & Deep Research Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MatrixBlack,
                contentColor = MatrixGreenPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MatrixGreenPrimary
                    )
                },
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) MatrixGreenBright else MatrixTextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Tab 0: Autonomous Goal Loop
                    Text(
                        text = "Enter a complex multi-step directive. The Hermes Agent will reason in <scratchpad>, formulate <tool_call> schemas, evaluate <tool_response>, and iterate until completion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it },
                        placeholder = { Text("e.g. Calculate 144 * 12 then set alarm for 8am and check battery") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hermes_goal_input"),
                        colors = fieldColors,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MatrixGreenPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = progressStatus.ifEmpty { "Executing Hermes Loop..." },
                                    fontSize = 11.sp,
                                    color = MatrixGreenBright
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (goalInput.isNotBlank()) {
                                    onExecuteGoal(goalInput)
                                }
                            },
                            enabled = !isProcessing && goalInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixGreenPrimary,
                                contentColor = MatrixBlack
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("hermes_run_goal_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch Goal")
                        }
                    }

                    // Live Execution Steps Trace
                    if (liveSteps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "⚡ LIVE EXECUTION TRACE (${liveSteps.size} Steps)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MatrixGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            liveSteps.forEach { step ->
                                HermesStepCard(step = step)
                            }
                        }
                    }
                }

                1 -> {
                    // Tab 1: Deep Research Agent
                    Text(
                        text = "Autonomous research engine inspired by top Git research agents. Executes multi-vector inquiry, extracts evidence, generates verified citations [Source 1], and archives the complete report into Room SQLite Knowledge Vault.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = researchInput,
                        onValueChange = { researchInput = it },
                        placeholder = { Text("e.g. LLM On-Device NPU Acceleration and Quantization Benchmarks") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hermes_research_input"),
                        colors = fieldColors,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isProcessing) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MatrixGreenPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = progressStatus.ifEmpty { "Conducting Research..." },
                                    fontSize = 11.sp,
                                    color = MatrixGreenBright
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = {
                                if (researchInput.isNotBlank()) {
                                    onRunDeepResearch(researchInput)
                                }
                            },
                            enabled = !isProcessing && researchInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixGreenPrimary,
                                contentColor = MatrixBlack
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("hermes_start_research_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Conduct Research")
                        }
                    }

                    // Display Latest Research Dossier if present
                    if (latestDossier != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "🔬 RESEARCH DOSSIER: ${latestDossier.topic.uppercase()}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MatrixGreenBright
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MatrixDarkGray),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = latestDossier.executiveSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixTextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "📚 Citations & Sources (${latestDossier.sources.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MatrixGreenPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                latestDossier.sources.forEach { source ->
                                    Text(
                                        text = "• [Source ${source.index}]: ${source.title} (${source.domain}) — Credibility: ${(source.credibilityScore * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        color = MatrixTextSecondary
                                    )
                                }

                                if (latestDossier.savedKnowledgeId != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MatrixGreenPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Archived to Knowledge Base (ID: ${latestDossier.savedKnowledgeId})",
                                            fontSize = 10.sp,
                                            color = MatrixGreenBright
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Tab 2: Tool Catalog
                    Text(
                        text = "Bundled Hermes ChatML Tool Specifications. The agent exposes these schemas inside <tools> to perform zero-shot function calling.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HermesSkillLibrary.BUNDLED_TOOLS.forEach { tool ->
                            HermesToolItemCard(tool = tool)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HermesStepCard(step: HermesExecutionStep) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MatrixDarkGray.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (step.isFinal) MatrixGreenPrimary else MatrixBorder, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Step ${step.stepNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MatrixGreenBright
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (step.toolCall != null) {
                        Box(
                            modifier = Modifier
                                .background(MatrixGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "<tool_call>: ${step.toolCall.name}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MatrixGreenPrimary
                            )
                        }
                    } else if (step.isFinal) {
                        Box(
                            modifier = Modifier
                                .background(MatrixGreenBright.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FINAL ANSWER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MatrixGreenBright
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MatrixTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (!step.scratchpad.isNullOrBlank()) {
                        Text(
                            text = "🧠 <scratchpad> Internal Monologue:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MatrixGoldWarning
                        )
                        Text(
                            text = step.scratchpad,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MatrixTextPrimary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (step.toolCall != null) {
                        Text(
                            text = "⚙️ Arguments: ${step.toolCall.arguments}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MatrixTextSecondary
                        )
                    }

                    if (!step.toolResponse.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📥 <tool_response>: ${step.toolResponse}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MatrixGreenBright
                        )
                    }

                    if (!step.observation.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "👁️ ${step.observation}",
                            fontSize = 10.sp,
                            color = MatrixTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HermesToolItemCard(tool: HermesToolDefinition) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MatrixSurfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, MatrixBorder, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tool.name,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MatrixGreenBright
                    )
                }

                Text(
                    text = "${tool.parameters.size} params",
                    fontSize = 10.sp,
                    color = MatrixTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tool.description,
                fontSize = 11.sp,
                color = MatrixTextPrimary
            )

            AnimatedVisibility(visible = expanded && tool.parameters.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "Schema Parameters:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatrixGreenPrimary
                    )
                    tool.parameters.forEach { (name, def) ->
                        Text(
                            text = "• $name (${def.type}): ${def.description} ${if (def.required) "[Required]" else "[Optional]"}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MatrixTextSecondary,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
