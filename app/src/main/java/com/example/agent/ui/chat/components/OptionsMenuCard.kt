package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.agent.data.model.AgentSmithCharacterCard
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun OptionsMenuCard(
    currentProvider: String,
    currentModel: String,
    openaiKey: String,
    anthropicKey: String,
    groqKey: String = "",
    openrouterKey: String = "",
    huggingfaceKey: String = "",
    biometricLock: Boolean,
    totalCost: Double,
    charName: String,
    charPersonality: String,
    charTone: String,
    charDescription: String = AgentSmithCharacterCard.DESCRIPTION,
    charScenario: String = AgentSmithCharacterCard.SCENARIO,
    charFirstMessage: String = AgentSmithCharacterCard.FIRST_MESSAGE,
    charMesExample: String = AgentSmithCharacterCard.MES_EXAMPLE,
    soundFxVolume: Double = 0.8,
    soundFxFrequency: String = "ALL_ACTIONS",
    providerModels: List<Pair<String, String>> = emptyList(),
    onSaveKeys: (gemini: String, groq: String, openrouter: String, hf: String, mistral: String, together: String, cohere: String, openai: String, anthropic: String) -> Unit,
    onSaveCharacter: (name: String, personality: String, tone: String) -> Unit,
    onSaveCharacterV2: (name: String, description: String, personality: String, scenario: String, firstMessage: String, mesExample: String, tone: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onSaveSoundFx: (volume: Double, frequency: String) -> Unit = { _, _ -> },
    onTestSoundFx: (String) -> Unit = {},
    onToggleBiometric: (Boolean) -> Unit,
    onSelectProvider: (String, String) -> Unit
) {
    var selectedProvider by remember { mutableStateOf(currentProvider) }

    // Sound FX State
    var currentVolume by remember { mutableStateOf(soundFxVolume) }
    var currentFreqMode by remember { mutableStateOf(soundFxFrequency) }

    // Multi-Provider Keys
    var geminiInput by remember { mutableStateOf("") }
    var groqInput by remember { mutableStateOf(groqKey) }
    var openrouterInput by remember { mutableStateOf(openrouterKey) }
    var hfInput by remember { mutableStateOf("") }
    var mistralInput by remember { mutableStateOf("") }
    var togetherInput by remember { mutableStateOf("") }
    var cohereInput by remember { mutableStateOf("") }
    var openaiInput by remember { mutableStateOf("") }
    var anthropicInput by remember { mutableStateOf("") }

    // Character Card v2 State
    var nameInput by remember { mutableStateOf(charName) }
    var descInput by remember { mutableStateOf(charDescription) }
    var personalityInput by remember { mutableStateOf(charPersonality) }
    var scenarioInput by remember { mutableStateOf(charScenario) }
    var firstMessageInput by remember { mutableStateOf(charFirstMessage) }
    var mesExampleInput by remember { mutableStateOf(charMesExample) }
    var toneInput by remember { mutableStateOf(charTone) }

    var expandedExamples by remember { mutableStateOf(false) }

    val providersList = listOf(
        "Google Gemini" to "Free Tier (Built-in)",
        "Groq" to "Free Ultra-Fast Tier",
        "OpenRouter" to "Free Models Hub",
        "Local SLM" to "100% Free & Offline (Qwen 3 Q 1.7B)",
        "Multi-API" to "Distributed Concurrent Tri-Grid"
    )

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
        Text(
            text = "⚡ MATRIX SYSTEM CONFIGURATION & PROVIDERS",
            style = MaterialTheme.typography.titleMedium,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Active Node: $selectedProvider",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Select AI Model Provider (Free Tiers Highlighted):",
            style = MaterialTheme.typography.labelMedium,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            providersList.forEach { (prov, tier) ->
                FilterChip(
                    selected = selectedProvider == prov,
                    onClick = {
                        selectedProvider = prov
                        onSelectProvider(prov, tier)
                    },
                    label = { Text("$prov [$tier]") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MatrixGreenPrimary,
                        selectedLabelColor = MatrixBlack,
                        containerColor = MatrixGreenContainer,
                        labelColor = MatrixTextPrimary
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Frontier Gemini Model & Cognitive Reasoning Architecture:",
            style = MaterialTheme.typography.titleSmall,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            val models = if (providerModels.isNotEmpty() && selectedProvider == currentProvider) {
                providerModels
            } else {
                when (selectedProvider) {
                    "Google Gemini" -> listOf(
                        "models/gemini-2.5-flash" to "gemini-2.5-flash (Standard)",
                        "models/gemini-2.5-pro" to "gemini-2.5-pro (Advanced)",
                        "models/gemini-1.5-flash" to "gemini-1.5-flash (Fast)",
                        "models/gemini-1.5-pro" to "gemini-1.5-pro (High intelligence)"
                    )
                    "Groq" -> listOf(
                        "llama-3.3-70b-versatile" to "llama-3.3-70b-versatile (Default)",
                        "llama-3.1-8b-instant" to "llama-3.1-8b-instant (Fast)",
                        "mixtral-8x7b-32768" to "mixtral-8x7b-32768 (MoE)",
                        "gemma2-9b-it" to "gemma2-9b-it (Google Core)"
                    )
                    "OpenRouter" -> listOf(
                        "meta-llama/llama-3.3-70b-instruct:free" to "Llama 3.3 70B Instruct (Free)",
                        "deepseek/deepseek-r1:free" to "DeepSeek R1 (Free)",
                        "qwen/qwen-2.5-72b-instruct:free" to "Qwen 2.5 72B (Free)",
                        "google/gemma-2-9b-it:free" to "Gemma 2 9B (Free)"
                    )
                    else -> listOf(
                        "qwen_3_q_1_7b" to "Qwen 3 Q 1.7B (Pre-installed)"
                    )
                }
            }
            models.forEach { (modId, modLabel) ->
                FilterChip(
                    selected = currentModel == modId,
                    onClick = { onSelectProvider(selectedProvider, modId) },
                    label = { Text(modLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MatrixGreenPrimary,
                        selectedLabelColor = MatrixBlack,
                        containerColor = MatrixGreenContainer,
                        labelColor = MatrixTextPrimary
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Provider API Credentials:",
            style = MaterialTheme.typography.titleSmall,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = groqInput,
            onValueChange = { groqInput = it },
            label = { Text("Groq API Key (Free: console.groq.com)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = openrouterInput,
            onValueChange = { openrouterInput = it },
            label = { Text("OpenRouter API Key (Free models: openrouter.ai)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSaveKeys(geminiInput, groqInput, openrouterInput, "", "", "", "", "", "")
            },
            colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenPrimary, contentColor = MatrixBlack),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Credentials to Secure DataStore")
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "🕶️ AGENT SMITH CHARACTER CARD V2 SPECIFICATION",
            style = MaterialTheme.typography.titleSmall,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Spec: chara_card_v2 | Authentic Movie Dialogue & Philosophical Directives",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Agent Designation (name)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = descInput,
            onValueChange = { descInput = it },
            label = { Text("Description (description)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = personalityInput,
            onValueChange = { personalityInput = it },
            label = { Text("Matrix Personality Directives (personality)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = scenarioInput,
            onValueChange = { scenarioInput = it },
            label = { Text("Scenario (scenario)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = firstMessageInput,
            onValueChange = { firstMessageInput = it },
            label = { Text("Greeting (first_message)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = toneInput,
            onValueChange = { toneInput = it },
            label = { Text("Behavioral Tone (tone)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { expandedExamples = !expandedExamples },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenContainer, contentColor = MatrixGreenPrimary),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (expandedExamples) "Hide Movie Dialogue Examples" else "View Movie Dialogue Examples (mes_example)")
            }
        }

        if (expandedExamples) {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = mesExampleInput,
                onValueChange = { mesExampleInput = it },
                label = { Text("Authentic Movie Dialogues (mes_example)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                maxLines = 10
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                onSaveCharacter(nameInput, personalityInput, toneInput)
                onSaveCharacterV2(nameInput, descInput, personalityInput, scenarioInput, firstMessageInput, mesExampleInput, toneInput)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenPrimary, contentColor = MatrixBlack),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Agent Smith Character Card v2")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sound FX & Audio Feedback Customization Section
        Text(
            text = "🔊 MATRIX AUDIO FEEDBACK & SOUND EFFECTS",
            style = MaterialTheme.typography.titleSmall,
            color = MatrixGreenPrimary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Audio Feedback Frequency Mode:",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL_ACTIONS", "CRITICAL_ONLY", "MUTED").forEach { freq ->
                FilterChip(
                    selected = currentFreqMode == freq,
                    onClick = {
                        currentFreqMode = freq
                        onSaveSoundFx(currentVolume, freq)
                    },
                    label = { Text(freq) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MatrixGreenContainer,
                        labelColor = MatrixTextPrimary,
                        selectedContainerColor = MatrixGreenPrimary,
                        selectedLabelColor = MatrixBlack
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sound FX Volume: ${(currentVolume * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(0.0, 0.3, 0.6, 0.8, 1.0).forEach { vol ->
                FilterChip(
                    selected = (currentVolume - vol).let { Math.abs(it) < 0.05 },
                    onClick = {
                        currentVolume = vol
                        onSaveSoundFx(vol, currentFreqMode)
                    },
                    label = { Text("${(vol * 100).toInt()}%") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MatrixGreenContainer,
                        labelColor = MatrixTextPrimary,
                        selectedContainerColor = MatrixGreenPrimary,
                        selectedLabelColor = MatrixBlack
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { onTestSoundFx("NEURAL_KEYSTROKE") },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenContainer, contentColor = MatrixGreenPrimary),
                modifier = Modifier.height(30.dp)
            ) { Text("Keystroke", fontSize = 10.sp) }

            Button(
                onClick = { onTestSoundFx("PRIORITY_BOOST") },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenContainer, contentColor = MatrixGreenPrimary),
                modifier = Modifier.height(30.dp)
            ) { Text("Boost Chirp", fontSize = 10.sp) }

            Button(
                onClick = { onTestSoundFx("SWARM_REPLICATE") },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenContainer, contentColor = MatrixGreenPrimary),
                modifier = Modifier.height(30.dp)
            ) { Text("Swarm Warp", fontSize = 10.sp) }

            Button(
                onClick = { onTestSoundFx("TELECOM_DIAL") },
                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenContainer, contentColor = MatrixGreenPrimary),
                modifier = Modifier.height(30.dp)
            ) { Text("Telecom Dial", fontSize = 10.sp) }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Biometric Matrix Lock",
                color = MatrixTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = biometricLock,
                onCheckedChange = onToggleBiometric,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MatrixBlack,
                    checkedTrackColor = MatrixGreenPrimary,
                    uncheckedTrackColor = MatrixSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Matrix Telemetry Cost: \$${String.format("%.4f", totalCost)}",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixGreenPrimary
        )
    }
}
