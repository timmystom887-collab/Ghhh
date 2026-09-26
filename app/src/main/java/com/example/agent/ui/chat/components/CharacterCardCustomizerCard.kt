package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CharacterCardCustomizerCard(
    currentName: String,
    currentPersonality: String,
    currentTone: String,
    onSaveCharacter: (String, String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var personalityInput by remember { mutableStateOf(currentPersonality) }
    var toneInput by remember { mutableStateOf(currentTone) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(text = "Character Card v2 Personality Customizer", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Agent Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = personalityInput,
            onValueChange = { personalityInput = it },
            label = { Text("Personality & Core Behavior") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = toneInput,
            onValueChange = { toneInput = it },
            label = { Text("Tone & Emotional Style") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSaveCharacter(nameInput, personalityInput, toneInput) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Character Card v2")
        }
    }
}
