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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

data class LocalModelBenchmark(
    val modelName: String,
    val parameters: String,
    val toolCallingScore: String, // BFCL / Function calling accuracy
    val speedTps: String, // Tokens per second on NPU/mobile
    val memoryFootprint: String,
    val bestUseCases: String
)

@Composable
fun LocalModelsGuideCard(
    onSelectModel: (String) -> Unit
) {
    val benchmarks = listOf(
        LocalModelBenchmark(
            modelName = "Qwen 2.5 / 3.5 Instruct",
            parameters = "1.5B – 4B",
            toolCallingScore = "94.2% (Top BFCL v4)",
            speedTps = "45–65 tok/s",
            memoryFootprint = "1.2GB – 2.4GB (Q4_K_M)",
            bestUseCases = "Unmatched on-device tool calling, structured JSON output, multilingual reasoning (35+ languages)."
        ),
        LocalModelBenchmark(
            modelName = "Google Gemma 4 Edge",
            parameters = "E2B & E4B",
            toolCallingScore = "92.8% (Native tokens)",
            speedTps = "50–70 tok/s",
            memoryFootprint = "1.5GB – 2.8GB",
            bestUseCases = "Special function-calling tokens, native multi-modal audio input, high efficiency on mobile NPUs."
        ),
        LocalModelBenchmark(
            modelName = "Meta Llama 3.2",
            parameters = "1B & 3B",
            toolCallingScore = "89.5% (Fine-tuned JSON)",
            speedTps = "60–80 tok/s",
            memoryFootprint = "0.9GB – 2.1GB",
            bestUseCases = "Ultra-fast latency on mobile chipsets, broad community runtime compatibility (ExecuTorch / llama.cpp)."
        ),
        LocalModelBenchmark(
            modelName = "Microsoft Phi-4 Mini",
            parameters = "3.8B",
            toolCallingScore = "91.0% (Logic Density)",
            speedTps = "35–50 tok/s",
            memoryFootprint = "2.4GB (8GB+ RAM devices)",
            bestUseCases = "Complex multi-step math, reasoning, code generation, and nested tool calls."
        ),
        LocalModelBenchmark(
            modelName = "DeepSeek-R1 Distill Qwen",
            parameters = "1.5B & 7B",
            toolCallingScore = "88.4% (Chain-of-Thought)",
            speedTps = "30–45 tok/s",
            memoryFootprint = "1.4GB – 4.2GB",
            bestUseCases = "Self-reflective thinking, autonomous edge planning, vulnerability analysis, and decomposition."
        )
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
                    text = "⚡ 2025–2026 TOP LOCAL SLMs & RUNTIMES",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "State-of-the-art on-device small language models evaluated for intelligence density, mobile NPU speed, and function/tool-calling reliability:",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        benchmarks.forEach { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(MatrixGreenContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
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
                        color = MatrixGreenPrimary
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
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.bestUseCases,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextPrimary
                )
            }
        }
    }
}
