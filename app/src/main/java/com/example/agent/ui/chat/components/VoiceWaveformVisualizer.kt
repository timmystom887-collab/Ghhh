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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
fun VoiceWaveformVisualizer(
    isListening: Boolean,
    liveTranscript: String,
    onStopListening: () -> Unit
) {
    if (!isListening) return

    val infiniteTransition = rememberInfiniteTransition(label = "WavePulse")
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale2"
    )
    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale3"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixGreenPrimary, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MatrixGreenContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Microphone Active",
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "🎙️ MATRIX VOCAL RECEPTOR ACTIVE",
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary
                    )
                    Text(
                        text = "Speak your task naturally to Agent Smith...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                }
            }

            Button(
                onClick = onStopListening,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixRedAlert
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = MatrixRedAlert)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Stop", color = MatrixRedAlert, style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Matrix Audio Waveform
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val heights = listOf(scale1, scale2, scale3, scale1 * 1.1f, scale2 * 0.9f, scale3 * 1.2f, scale1 * 0.8f, scale2 * 1.1f)
            heights.forEach { h ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(4.dp)
                        .height((24 * h).dp)
                        .background(MatrixGreenPrimary, RoundedCornerShape(2.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (liveTranscript.isNotBlank()) "\"$liveTranscript\"" else "Awaiting acoustic input...",
            style = MaterialTheme.typography.bodyMedium,
            color = MatrixTextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
