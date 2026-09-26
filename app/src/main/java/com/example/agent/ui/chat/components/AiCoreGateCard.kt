package com.example.agent.ui.chat.components

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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun AiCoreGateCard(
    onActivateLocalAi: () -> Unit,
    onSaveGeminiKey: (String) -> Unit,
    onOpenAllProviders: () -> Unit
) {
    var geminiInput by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "GatePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
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
            .border(2.dp, MatrixGreenPrimary, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MatrixRedAlert, CircleShape)
                        .alpha(pulseAlpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🔒 MATRIX NEURAL CORE INACTIVE",
                    style = MaterialTheme.typography.titleMedium,
                    color = MatrixGreenPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Agent Smith requires an active neural intelligence engine to process voice and text instructions with zero filler responses. Configure one of the options below to Jack In:",
            style = MaterialTheme.typography.bodyMedium,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onActivateLocalAi,
            colors = ButtonDefaults.buttonColors(
                containerColor = MatrixGreenPrimary,
                contentColor = MatrixBlack
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Memory, contentDescription = "Local SLM", tint = MatrixBlack)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Activate Local Matrix SLM (100% Free & Offline)")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "— OR CONNECT GOOGLE GEMINI API —",
            style = MaterialTheme.typography.labelSmall,
            color = MatrixTextSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = geminiInput,
            onValueChange = { geminiInput = it },
            label = { Text("Gemini API Key (Free tier)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (geminiInput.isNotBlank()) {
                    onSaveGeminiKey(geminiInput)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MatrixGreenContainer,
                contentColor = MatrixGreenPrimary
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Key, contentDescription = "Key", tint = MatrixGreenPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Engage Gemini Neural Link")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onOpenAllProviders,
            colors = ButtonDefaults.buttonColors(
                containerColor = MatrixBlack,
                contentColor = MatrixTextSecondary
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Speed, contentDescription = "Providers", tint = MatrixTextSecondary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Configure Groq, OpenRouter, HF Free Tiers")
        }
    }
}
