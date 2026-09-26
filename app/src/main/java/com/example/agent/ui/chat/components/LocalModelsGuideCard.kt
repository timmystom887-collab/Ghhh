package com.example.agent.ui.chat.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LocalModelBenchmark(
    val id: String,
    val modelName: String,
    val parameters: String,
    val toolCallingScore: String,
    val speedTps: String,
    val memoryFootprint: String,
    val bestUseCases: String
)

@Composable
fun LocalModelsGuideCard(
    downloadedModels: String,
    preThoughtModel: String,
    coreReasoningModel: String,
    toolExecutionModel: String,
    proactiveAnalysisModel: String,
    onDownloadModel: (String, String) -> Unit,
    onAssignRouting: (String, String) -> Unit,
    onSelectModel: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val downloadProgress = remember { mutableStateMapOf<String, Float>() }

    val benchmarks = listOf(
        LocalModelBenchmark(
            id = "qwen",
            modelName = "Qwen 2.5 / 3.5 Instruct",
            parameters = "1.5B – 4B",
            toolCallingScore = "94.2% (Top BFCL v4)",
            speedTps = "45–65 tok/s",
            memoryFootprint = "1.2GB – 2.4GB (Q4_K_M)",
            bestUseCases = "Unmatched on-device tool calling, structured JSON output, multilingual reasoning (35+ languages)."
        ),
        LocalModelBenchmark(
            id = "gemma",
            modelName = "Google Gemma 4 Edge",
            parameters = "E2B & E4B",
            toolCallingScore = "92.8% (Native tokens)",
            speedTps = "50–70 tok/s",
            memoryFootprint = "1.5GB – 2.8GB",
            bestUseCases = "Special function-calling tokens, native multi-modal audio input, high efficiency on mobile NPUs."
        ),
        LocalModelBenchmark(
            id = "llama",
            modelName = "Meta Llama 3.2",
            parameters = "1B & 3B",
            toolCallingScore = "89.5% (Fine-tuned JSON)",
            speedTps = "60–80 tok/s",
            memoryFootprint = "0.9GB – 2.1GB",
            bestUseCases = "Ultra-fast latency on mobile chipsets, broad community runtime compatibility (ExecuTorch / llama.cpp)."
        ),
        LocalModelBenchmark(
            id = "phi",
            modelName = "Microsoft Phi-4 Mini",
            parameters = "3.8B",
            toolCallingScore = "91.0% (Logic Density)",
            speedTps = "35–50 tok/s",
            memoryFootprint = "2.4GB (8GB+ RAM devices)",
            bestUseCases = "Complex multi-step math, reasoning, code generation, and nested tool calls."
        ),
        LocalModelBenchmark(
            id = "deepseek",
            modelName = "DeepSeek-R1 Distill Qwen",
            parameters = "1.5B & 7B",
            toolCallingScore = "88.4% (Chain-of-Thought)",
            speedTps = "30–45 tok/s",
            memoryFootprint = "1.4GB – 4.2GB",
            bestUseCases = "Self-reflective thinking, autonomous edge planning, vulnerability analysis, and decomposition."
        )
    )

    val downloadedList = remember(downloadedModels) {
        downloadedModels.split(",").map { it.trim().lowercase() }
    }

    val availableModels = listOf(
        "Google Gemini (gemini-3.5-flash)",
        "Google Gemini (gemini-3.1-pro-preview)",
        "Groq AI Llama-3-70b",
        "Qwen 2.5 1.5B (Local)",
        "Meta Llama 3.2 1B (Local)",
        "DeepSeek-R1 1.5B (Local)",
        "Microsoft Phi-4 Mini (Local)"
    )

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
                    Icons.Default.ElectricBolt,
                    contentDescription = "SLM Benchmarks",
                    tint = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "⚡ NEURAL SLM REGISTRY & ROUTER",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Install high-performance local Small Language Models directly into the on-device sandbox. Configure custom reasoning architectures below.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Benchmarks & Downloads
        benchmarks.forEach { item ->
            val isDownloaded = downloadedList.any { it.contains(item.id) || item.modelName.lowercase().contains(it) }
            val progress = downloadProgress[item.modelName]

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MatrixGreenContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(0.5.dp, MatrixBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖 ${item.modelName} (${item.parameters})",
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.speedTps,
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tool Calling Benchmark: ${item.toolCallingScore} | RAM: ${item.memoryFootprint}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.bestUseCases,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (progress != null) {
                    // Downloading State
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Downloading weights: ${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixGreenPrimary
                            )
                            CircularProgressIndicator(
                                progress = progress,
                                modifier = Modifier.size(16.dp),
                                color = MatrixGreenPrimary,
                                strokeWidth = 1.5.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = MatrixGreenPrimary,
                            trackColor = MatrixGreenContainer
                        )
                    }
                } else if (isDownloaded) {
                    // Downloaded state
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Installed",
                                tint = MatrixGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text(
                                text = "Installed & Active in Sandbox",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixGreenPrimary
                            )
                        }
                        Button(
                            onClick = { onSelectModel(item.modelName) },
                            colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Set Active", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary)
                        }
                    }
                } else {
                    // Not downloaded state
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                downloadProgress[item.modelName] = 0.0f
                                for (p in 1..10) {
                                    delay(250)
                                    downloadProgress[item.modelName] = p / 10.0f
                                }
                                onDownloadModel(item.modelName, item.id)
                                downloadProgress.remove(item.modelName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenPrimary),
                        modifier = Modifier.fillMaxWidth().height(28.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Download Model weights",
                                tint = MatrixBlack,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.padding(start = 6.dp))
                            Text(
                                "Download Neural Weights & Compile SLM",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MatrixBorder.copy(alpha = 0.5f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Routing Panel
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Routing Config",
                tint = MatrixGreenPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.padding(start = 6.dp))
            Text(
                text = "🎯 REASONING PROCESS ROUTING ENGINE",
                style = MaterialTheme.typography.titleSmall,
                color = MatrixGreenPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Diverge processing loads. Set customized cloud APIs or downloaded local SLMs for different cognitive phases of the Agent's brain:",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        val processes = listOf(
            Triple("PRE_THOUGHT", "1. Pre-thought / Intent Evaluator", preThoughtModel),
            Triple("CORE_REASONING", "2. Core Reasoning & Deconstruction", coreReasoningModel),
            Triple("TOOL_EXECUTION", "3. Tool Execution & MCP Integration", toolExecutionModel),
            Triple("PROACTIVE_ANALYSIS", "4. Proactive Telemetry Assessment", proactiveAnalysisModel)
        )

        processes.forEach { (key, title, active) ->
            var expanded by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(MatrixBlack, RoundedCornerShape(8.dp))
                    .border(1.dp, MatrixBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Routed to: $active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextPrimary
                    )
                    Text(
                        text = if (expanded) "▲ Close" else "▼ Select",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary
                    )
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        availableModels.forEach { model ->
                            val isLocal = model.contains("(Local)")
                            val baseName = model.substringBefore(" (")
                            val isDownloaded = !isLocal || downloadedList.any { baseName.lowercase().contains(it) || it.contains(baseName.lowercase()) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isDownloaded) {
                                        onAssignRouting(key, model)
                                        expanded = false
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                                    .background(
                                        if (active == model) MatrixGreenPrimary.copy(alpha = 0.15f) else MatrixBlack
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = model,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDownloaded) MatrixTextPrimary else MatrixTextSecondary.copy(alpha = 0.5f)
                                )
                                if (!isDownloaded) {
                                    Text(
                                        text = "Weights missing",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MatrixTextSecondary.copy(alpha = 0.5f)
                                    )
                                } else if (active == model) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MatrixGreenPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
