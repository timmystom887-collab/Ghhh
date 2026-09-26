package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatrixToolsMenuCard(
    onExecuteTool: (String) -> Unit
) {
    val toolsList = listOf(
        Triple("Battery Status", "get_battery", Icons.Default.Info),
        Triple("System Diagnostics", "diagnostics", Icons.Default.Settings),
        Triple("Toggle Torch", "torch", Icons.Default.FlashlightOn),
        Triple("Set 7:00 Alarm", "alarm 7 0 Morning Protocol", Icons.Default.Alarm),
        Triple("Launch Maps", "app Maps", Icons.Default.Launch),
        Triple("Launch Chrome", "app Chrome", Icons.Default.Launch),
        Triple("Test Calculator", "calc 42 * 1337", Icons.Default.Calculate),
        Triple("Dialer Interface", "call 911", Icons.Default.Call),
        Triple("Send SMS Protocol", "sms 555-0199 System check", Icons.Default.Message)
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
            Text(
                text = "🛠️ MATRIX DEVICE AUTOMATION SUITE",
                style = MaterialTheme.typography.titleSmall,
                color = MatrixGreenPrimary
            )
            Text(
                text = "${toolsList.size} TOOLS READY",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tap to trigger real device tools directly, or type natural commands (e.g., 'Open Chrome', 'Set alarm for 8am', 'What is my battery level').",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            toolsList.forEach { (label, command, icon) ->
                Button(
                    onClick = { onExecuteTool(command) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenContainer,
                        contentColor = MatrixGreenPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(icon, contentDescription = label, tint = MatrixGreenPrimary)
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(text = label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
