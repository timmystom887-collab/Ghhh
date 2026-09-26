package com.example.agent.ui.chat.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.PhoneForwarded
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary
import com.example.agent.util.GhostCallStage
import com.example.agent.util.GhostCallStatus

@Composable
fun GhostCallCard(
    status: GhostCallStatus,
    onSendDtmf: (String) -> Unit,
    onLaunchNativeDialer: (String) -> Unit,
    onCancelCall: () -> Unit,
    onRedial: () -> Unit
) {
    val context = LocalContext.current
    var showDtmfPad by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "CallPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
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
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            if (status.stage == GhostCallStage.MISSION_SUCCESS) MatrixGreenPrimary
                            else if (status.stage == GhostCallStage.MISSION_FAILED) MatrixRedAlert
                            else MatrixGreenPrimary,
                            CircleShape
                        )
                        .alpha(if (status.stage != GhostCallStage.MISSION_SUCCESS && status.stage != GhostCallStage.MISSION_FAILED) pulseAlpha else 1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📞 GHOST OPERATOR TELECOM BRIDGE",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "[${status.stage.name}]",
                style = MaterialTheme.typography.labelSmall,
                color = if (status.stage == GhostCallStage.MISSION_SUCCESS) MatrixGreenPrimary else MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Target: ${status.targetName.ifEmpty { "Metro Telephony Destination" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MatrixTextPrimary
            )
            Text(
                text = status.phoneNumber.ifEmpty { "555-0199" },
                style = MaterialTheme.typography.bodyMedium,
                color = MatrixGreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Objective: ${status.objective.ifEmpty { "Autonomous Table Reservation" }} [${status.callType}]",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { status.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MatrixGreenPrimary,
            trackColor = MatrixGreenContainer
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = status.currentStepText,
            style = MaterialTheme.typography.labelSmall,
            color = MatrixGreenPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Live Audio Waveform Simulation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatrixBlack, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AUDIO TELEMETRY:",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                val heights = listOf(12, 24, 18, 30, 20, 14, 28, 16, 22, 10)
                heights.forEachIndexed { i, h ->
                    val dynamicH = (h * (if (status.audioLevel > 0f) status.audioLevel else 0.2f)).dp
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(dynamicH)
                            .background(MatrixGreenPrimary, RoundedCornerShape(2.dp))
                    )
                }
            }
            if (status.confirmationCode.isNotBlank()) {
                Text(
                    text = "CODE: ${status.confirmationCode}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixGreenPrimary
                )
            }
        }

        // Live Transcript stream
        if (status.liveTranscript.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixGreenContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "💬 LIVE TELECOM TRANSCRIPT:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    status.liveTranscript.takeLast(6).forEach { (speaker, text) ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "$speaker: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (speaker.contains("Smith")) MatrixGreenPrimary else MatrixTextPrimary
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MatrixTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // DTMF Dialpad section
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showDtmfPad = !showDtmfPad },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixGreenPrimary
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.Dialpad, contentDescription = "DTMF", tint = MatrixGreenPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showDtmfPad) "Hide DTMF Keypad" else "Show DTMF Keypad", style = MaterialTheme.typography.labelSmall)
            }

            if (status.activeDtmf.isNotBlank()) {
                Text(
                    text = "Last Tone: [${status.activeDtmf}]",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixGreenPrimary
                )
            }
        }

        if (showDtmfPad) {
            Spacer(modifier = Modifier.height(6.dp))
            val dtmfKeys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("*", "0", "#")
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MatrixBlack, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                dtmfKeys.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 2.dp)) {
                        row.forEach { key ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MatrixGreenContainer, RoundedCornerShape(6.dp))
                                    .border(1.dp, MatrixGreenPrimary, RoundedCornerShape(6.dp))
                                    .clickable { onSendDtmf(key) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = key, style = MaterialTheme.typography.bodyMedium, color = MatrixGreenPrimary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons: Launch Android Phone Dialer, Copy Script, Hangup / Redial
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onLaunchNativeDialer(status.phoneNumber.ifEmpty { "555-0199" })
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenPrimary,
                    contentColor = MatrixBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhoneForwarded, contentDescription = "Dialer", tint = MatrixBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Native Dialer", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Mission Script", status.fullMissionScript.ifEmpty {
                        "Objective: ${status.objective}. Hello, calling on behalf of Thomas Anderson to confirm details."
                    })
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Mission script copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixGreenPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MatrixGreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Script", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (status.stage != GhostCallStage.MISSION_SUCCESS && status.stage != GhostCallStage.MISSION_FAILED && status.stage != GhostCallStage.IDLE) {
                Button(
                    onClick = onCancelCall,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixRedAlert.copy(alpha = 0.2f),
                        contentColor = MatrixRedAlert
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Cancel", tint = MatrixRedAlert, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Terminate Call", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Button(
                    onClick = onRedial,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenContainer,
                        contentColor = MatrixGreenPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Redial", tint = MatrixGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Redial Mission", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
