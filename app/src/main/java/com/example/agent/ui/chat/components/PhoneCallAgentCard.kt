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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

data class PhoneCallMission(
    val id: String,
    val contactOrBusinessName: String,
    val phoneNumber: String,
    val callType: String, // "Reservation", "Verification", "Inquiry", "Custom"
    val objective: String,
    val callScript: String,
    val status: String, // "Ready", "Dialing", "In Call", "Completed"
    val outcomeSummary: String? = null
)

@Composable
fun PhoneCallAgentCard(
    mission: PhoneCallMission,
    onCallNow: (String) -> Unit,
    onLogToMemory: (String) -> Unit,
    onMarkCompleted: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CallPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
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
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(MatrixGreenPrimary, CircleShape)
                        .alpha(if (mission.status != "Completed") pulseAlpha else 1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📞 AGENT SMITH CALL DISPATCHER",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "[${mission.status.uppercase()}]",
                style = MaterialTheme.typography.labelSmall,
                color = if (mission.status == "Completed") MatrixGreenPrimary else MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Target: ${mission.contactOrBusinessName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MatrixTextPrimary
            )
            Text(
                text = mission.phoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MatrixGreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Objective: ${mission.objective}",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatrixGreenContainer.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "💬 AI DIALOGUE & VERIFICATION SCRIPT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.callScript,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextPrimary
                )
            }
        }

        if (mission.outcomeSummary != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixGreenContainer, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "✅ Outcome: ${mission.outcomeSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onCallNow(mission.phoneNumber) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenPrimary,
                    contentColor = MatrixBlack
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = MatrixBlack)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Execute Call", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = {
                    val logText = "Phone Call Record for ${mission.contactOrBusinessName} (${mission.phoneNumber}): ${mission.objective} -> Status: ${mission.status}. Script: ${mission.callScript}"
                    onLogToMemory(logText)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixGreenPrimary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Memory, contentDescription = "Log to Memory", tint = MatrixGreenPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Save Memory", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
