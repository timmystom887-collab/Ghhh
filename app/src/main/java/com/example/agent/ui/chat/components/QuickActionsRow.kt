package com.example.agent.ui.chat.components

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.provider.Settings
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixTextPrimary

@Composable
fun QuickActionsRow(
    onTriggerCallAgent: () -> Unit,
    onOpenCallLogs: () -> Unit,
    onOpenKnowledgeBase: () -> Unit,
    onOpenUserGuide: () -> Unit,
    onOpenSentinel: () -> Unit,
    onOpenPreCognition: () -> Unit,
    onOpenSubAgents: () -> Unit,
    onOpenMcpHub: () -> Unit,
    onOpenSlmBenchmarks: () -> Unit,
    onTriggerSwarm: () -> Unit,
    onTriggerDeepResearch: () -> Unit,
    onTriggerMultiApi: () -> Unit,
    onOpenMemoryVault: () -> Unit,
    onOpenToolsMenu: () -> Unit,
    onOpenThinkingMethods: () -> Unit = {},
    onOpenAutomatedSystems: () -> Unit = {},
    onTriggerGhostCall: () -> Unit = {}
) {
    val context = LocalContext.current
    var torchOn by remember { mutableStateOf(false) }

    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = MatrixGreenContainer,
        labelColor = MatrixTextPrimary,
        iconColor = MatrixGreenPrimary,
        selectedContainerColor = MatrixGreenPrimary,
        selectedLabelColor = MatrixBlack,
        selectedLeadingIconColor = MatrixBlack
    )
    val chipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = MatrixBorder,
        selectedBorderColor = MatrixGreenPrimary,
        borderWidth = 1.dp,
        selectedBorderWidth = 1.5.dp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        FilterChip(
            selected = false,
            onClick = onOpenThinkingMethods,
            label = { Text("🧠 Thinking Methods") },
            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Thinking Frameworks") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenAutomatedSystems,
            label = { Text("⚙️ Automated Logic") },
            leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = "Automated Systems") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onTriggerGhostCall,
            label = { Text("📞 Ghost Telecom") },
            leadingIcon = { Icon(Icons.Default.PhoneInTalk, contentDescription = "Ghost Operator") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenPreCognition,
            label = { Text("🔮 Pre-Cognition") },
            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Pre-Cognition Engine") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenSubAgents,
            label = { Text("⚡ Sub-Agents Topology") },
            leadingIcon = { Icon(Icons.Default.GroupWork, contentDescription = "Sub-Agents Topology") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenSentinel,
            label = { Text("👁️ Proactive Sentinel") },
            leadingIcon = { Icon(Icons.Default.Hearing, contentDescription = "Proactive Sentinel") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenUserGuide,
            label = { Text("📖 User Guide") },
            leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = "User Guide") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenKnowledgeBase,
            label = { Text("📚 Knowledge Vault") },
            leadingIcon = { Icon(Icons.Default.Storage, contentDescription = "Knowledge Vault") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onTriggerCallAgent,
            label = { Text("📞 Phone Call Agent") },
            leadingIcon = { Icon(Icons.Default.Call, contentDescription = "Phone Call Agent") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenCallLogs,
            label = { Text("Call Metadata Logs") },
            leadingIcon = { Icon(Icons.Default.PhoneInTalk, contentDescription = "Call Logs") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenMcpHub,
            label = { Text("🌐 MCP & Skills Hub") },
            leadingIcon = { Icon(Icons.Default.Hub, contentDescription = "MCP Hub") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenSlmBenchmarks,
            label = { Text("⚡ Top Local SLMs") },
            leadingIcon = { Icon(Icons.Default.ElectricBolt, contentDescription = "Local SLMs") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onTriggerSwarm,
            label = { Text("🕶️ Smith Swarm") },
            leadingIcon = { Icon(Icons.Default.GroupWork, contentDescription = "Swarm Mode") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onTriggerDeepResearch,
            label = { Text("Matrix Deep Research") },
            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Deep Research") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onTriggerMultiApi,
            label = { Text("Multi-API Cluster") },
            leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = "Multi-API") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenMemoryVault,
            label = { Text("Neural Memory Vault") },
            leadingIcon = { Icon(Icons.Default.Memory, contentDescription = "Memory Vault") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = onOpenToolsMenu,
            label = { Text("Device Tools") },
            leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = "Device Tools") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = torchOn,
            onClick = {
                try {
                    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    val cameraId = cameraManager.cameraIdList[0]
                    torchOn = !torchOn
                    cameraManager.setTorchMode(cameraId, torchOn)
                } catch (e: Exception) {
                    // Fallback
                }
            },
            label = { Text(if (torchOn) "Torch Active" else "Torch") },
            leadingIcon = { Icon(Icons.Default.FlashlightOn, contentDescription = "Flashlight") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            },
            label = { Text("Wi-Fi Node") },
            leadingIcon = { Icon(Icons.Default.NetworkWifi, contentDescription = "Wi-Fi") },
            colors = chipColors,
            border = chipBorder
        )
        Spacer(modifier = Modifier.width(6.dp))
        FilterChip(
            selected = false,
            onClick = {
                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            },
            label = { Text("Bluetooth Comms") },
            leadingIcon = { Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth") },
            colors = chipColors,
            border = chipBorder
        )
    }
}
