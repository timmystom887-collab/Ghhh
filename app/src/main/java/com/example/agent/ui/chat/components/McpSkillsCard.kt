package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.agent.data.local.entity.SkillEntity
import com.example.agent.data.model.McpServer
import com.example.agent.data.model.McpTool
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun McpSkillsCard(
    servers: List<McpServer>,
    catalogServers: List<McpServer> = emptyList(),
    tools: List<McpTool>,
    persistedSkills: List<SkillEntity>,
    onInstallServer: (McpServer) -> Unit = {},
    onUninstallServer: (String) -> Unit = {},
    onToggleServer: (String, Boolean) -> Unit = { _, _ -> },
    onAddCustomServer: (name: String, endpoint: String, transport: String, desc: String, auth: String) -> Unit = { _, _, _, _, _ -> },
    onResetDefaults: () -> Unit = {},
    onSynthesizeSkill: (String) -> Unit,
    onExecuteTool: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Installed, 1: Awesome Catalog, 2: Add Custom, 3: Synthesizer
    var newSkillPrompt by remember { mutableStateOf("") }
    var catalogSearch by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("ALL") }

    // Custom Server Input Form
    var customName by remember { mutableStateOf("") }
    var customEndpoint by remember { mutableStateOf("https://") }
    var customTransport by remember { mutableStateOf("HTTP_STREAMED_SSE") }
    var customDesc by remember { mutableStateOf("") }
    var customAuth by remember { mutableStateOf("") }

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
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Hub,
                    contentDescription = "MCP Hub",
                    tint = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "🌐 MCP SERVERS & SKILLS HUB",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "${servers.count { it.isConnected }}/${servers.size} ACTIVE",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixGreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Anthropic Model Context Protocol runtime. Browse Awesome MCP servers with HTTP streaming (SSE), install/remove nodes, or add custom endpoints.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MatrixBlack,
            contentColor = MatrixGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MatrixGreenPrimary
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Installed (${servers.size})", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Catalog (${catalogServers.size})", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("+ Custom", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("✨ Synth", style = MaterialTheme.typography.labelSmall) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            // TAB 0: INSTALLED SERVERS
            0 -> {
                Column {
                    Text(
                        text = "Active & Installed MCP Server Nodes:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (servers.isEmpty()) {
                        Text(
                            text = "No MCP servers installed. Switch to the 'Catalog' tab to install Awesome MCP servers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixTextSecondary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        servers.forEach { server ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MatrixGreenContainer.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        if (server.isConnected) MatrixGreenPrimary.copy(alpha = 0.5f) else MatrixBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text(text = server.icon, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = server.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MatrixTextPrimary
                                            )
                                            Text(
                                                text = server.endpoint,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MatrixGreenPrimary
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = server.isConnected,
                                        onCheckedChange = { onToggleServer(server.serverId, it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MatrixGreenPrimary,
                                            checkedTrackColor = MatrixGreenContainer,
                                            uncheckedThumbColor = MatrixTextSecondary,
                                            uncheckedTrackColor = MatrixBorder
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = server.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixTextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (server.isStreamed) {
                                            Box(
                                                modifier = Modifier
                                                    .background(MatrixGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, MatrixGreenPrimary, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("HTTP STREAMED SSE", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(MatrixBlack, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("${server.toolsCount} TOOLS", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                                        }
                                    }

                                    Button(
                                        onClick = { onUninstallServer(server.serverId) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MatrixRedAlert.copy(alpha = 0.2f),
                                            contentColor = MatrixRedAlert
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Uninstall", modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Remove", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Registered Tools from active servers
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Exposed Tools from Active Servers (${tools.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    tools.take(6).forEach { tool ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .background(MatrixGreenContainer.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tool.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MatrixGreenPrimary
                                    )
                                    if (tool.isStreamed) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("[SSE]", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
                                    }
                                }
                                Text(
                                    text = tool.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatrixTextSecondary
                                )
                            }
                            Button(
                                onClick = { onExecuteTool(tool.name) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatrixGreenPrimary,
                                    contentColor = MatrixBlack
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MatrixBlack, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Test", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // TAB 1: AWESOME MCP CATALOG (MARKETPLACE)
            1 -> {
                Column {
                    Text(
                        text = "Awesome MCP Servers (Curated HTTP Streamed & Remote):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = catalogSearch,
                        onValueChange = { catalogSearch = it },
                        label = { Text("Search Awesome MCP (e.g. python, crawler, git, sql)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val categories = listOf("ALL", "WEB_SEARCH", "CODE_GIT", "CODE_EXEC", "DATABASE", "COMMUNICATION", "PRODUCTIVITY", "STORAGE", "UTILITY")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCat == cat,
                                onClick = { selectedCat = cat },
                                label = { Text(cat.replace("_", " "), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MatrixGreenPrimary,
                                    selectedLabelColor = MatrixBlack,
                                    containerColor = MatrixGreenContainer,
                                    labelColor = MatrixTextPrimary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val filtered = catalogServers.filter { s ->
                        val matchesQuery = catalogSearch.isBlank() ||
                                s.name.contains(catalogSearch, ignoreCase = true) ||
                                s.description.contains(catalogSearch, ignoreCase = true) ||
                                s.tags.contains(catalogSearch, ignoreCase = true)
                        val matchesCat = selectedCat == "ALL" || s.category.equals(selectedCat, ignoreCase = true)
                        matchesQuery && matchesCat
                    }

                    if (filtered.isEmpty()) {
                        Text(
                            text = if (catalogServers.isEmpty()) "All catalog servers are currently installed!" else "No servers match query.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixGreenPrimary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        filtered.forEach { server ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MatrixGreenContainer.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .border(1.dp, MatrixBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text(text = server.icon, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = server.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MatrixTextPrimary
                                            )
                                            Text(
                                                text = server.endpoint,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MatrixGreenPrimary
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onInstallServer(server) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MatrixGreenPrimary,
                                            contentColor = MatrixBlack
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = "Install", tint = MatrixBlack, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Install", style = MaterialTheme.typography.labelSmall)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = server.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixTextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (server.isStreamed) {
                                        Box(
                                            modifier = Modifier
                                                .background(MatrixGreenPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .border(0.5.dp, MatrixGreenPrimary, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("STREAMABLE HTTP", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MatrixBlack, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("${server.toolsCount} TOOLS", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: ADD CUSTOM SERVER
            2 -> {
                Column {
                    Text(
                        text = "Register Custom Remote MCP Server (SSE / HTTP):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Server Name (e.g. My Custom Vector MCP)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customEndpoint,
                        onValueChange = { customEndpoint = it },
                        label = { Text("Endpoint URL (e.g. https://domain.com/mcp/sse)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Select Transport Protocol:", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("HTTP_STREAMED_SSE", "STREAMABLE_HTTP", "LOCAL_DEVICE").forEach { tr ->
                            FilterChip(
                                selected = customTransport == tr,
                                onClick = { customTransport = tr },
                                label = { Text(tr.replace("_", " "), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MatrixGreenPrimary,
                                    selectedLabelColor = MatrixBlack,
                                    containerColor = MatrixGreenContainer,
                                    labelColor = MatrixTextPrimary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customAuth,
                        onValueChange = { customAuth = it },
                        label = { Text("Optional Authorization Token / API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customDesc,
                        onValueChange = { customDesc = it },
                        label = { Text("Server Capabilities Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (customName.isNotBlank() && customEndpoint.isNotBlank()) {
                                onAddCustomServer(customName, customEndpoint, customTransport, customDesc, customAuth)
                                customName = ""
                                customEndpoint = "https://"
                                customDesc = ""
                                customAuth = ""
                                selectedTab = 0
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = MatrixBlack)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Connect & Register MCP Server")
                    }
                }
            }

            // TAB 3: DYNAMIC SKILL SYNTHESIS
            3 -> {
                Column {
                    Text(
                        text = "Autonomous Dynamic Skill Synthesizer:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When an operational directive requires an unregistered capability, Smith creates the JSON-RPC schema and persists it to Room SQLite.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newSkillPrompt,
                        onValueChange = { newSkillPrompt = it },
                        label = { Text("Skill requirement (e.g. flight tracking, stock audits)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newSkillPrompt.isNotBlank()) {
                                onSynthesizeSkill(newSkillPrompt)
                                newSkillPrompt = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Synthesize", tint = MatrixBlack)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Synthesize & Register Skill to Room")
                    }
                }
            }
        }
    }
}
