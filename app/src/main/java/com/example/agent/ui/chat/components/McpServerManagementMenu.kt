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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
fun McpServerManagementDialog(
    servers: List<McpServer>,
    catalogServers: List<McpServer>,
    tools: List<McpTool>,
    onDismiss: () -> Unit,
    onInstallServer: (McpServer) -> Unit,
    onUninstallServer: (String) -> Unit,
    onToggleServer: (String, Boolean) -> Unit,
    onAddCustomServer: (name: String, endpoint: String, transport: String, desc: String, auth: String) -> Unit,
    onExecuteTool: (String) -> Unit,
    onResetDefaults: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(680.dp)
                .background(MatrixSurface, RoundedCornerShape(20.dp))
                .border(2.dp, MatrixGreenPrimary, RoundedCornerShape(20.dp)),
            color = MatrixSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            McpServerManagementContent(
                servers = servers,
                catalogServers = catalogServers,
                tools = tools,
                onClose = onDismiss,
                onInstallServer = onInstallServer,
                onUninstallServer = onUninstallServer,
                onToggleServer = onToggleServer,
                onAddCustomServer = onAddCustomServer,
                onExecuteTool = onExecuteTool,
                onResetDefaults = onResetDefaults
            )
        }
    }
}

@Composable
fun McpServerManagementContent(
    servers: List<McpServer>,
    catalogServers: List<McpServer>,
    tools: List<McpTool>,
    onClose: (() -> Unit)? = null,
    onInstallServer: (McpServer) -> Unit,
    onUninstallServer: (String) -> Unit,
    onToggleServer: (String, Boolean) -> Unit,
    onAddCustomServer: (name: String, endpoint: String, transport: String, desc: String, auth: String) -> Unit,
    onExecuteTool: (String) -> Unit,
    onResetDefaults: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Installed, 1: Catalog, 2: Add Custom
    var catalogSearchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var streamedOnlyFilter by remember { mutableStateOf(false) }

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
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Hub,
                    contentDescription = "MCP Hub",
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "🌐 MCP SERVERS MANAGER",
                        style = MaterialTheme.typography.titleMedium,
                        color = MatrixGreenPrimary
                    )
                    Text(
                        text = "Model Context Protocol • HTTP Streamed & SSE Nodes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextSecondary
                    )
                }
            }

            if (onClose != null) {
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MatrixTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Telemetry Counters Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatrixBlack, RoundedCornerShape(10.dp))
                .border(1.dp, MatrixBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text("INSTALLED", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                    Text("${servers.size} Nodes", style = MaterialTheme.typography.labelMedium, color = MatrixGreenPrimary)
                }
                Column {
                    Text("ACTIVE / ONLINE", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                    Text("${servers.count { it.isConnected }} Online", style = MaterialTheme.typography.labelMedium, color = MatrixGreenPrimary)
                }
                Column {
                    Text("CATALOG", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                    Text("${catalogServers.size} Ready", style = MaterialTheme.typography.labelMedium, color = MatrixTextPrimary)
                }
            }

            Button(
                onClick = onResetDefaults,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixGreenPrimary
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Defaults", fontSize = 10.sp)
            }
        }

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
                text = { Text("Awesome Catalog (${catalogServers.size})", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("+ Add Custom", style = MaterialTheme.typography.labelSmall) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Body
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTab) {
                // TAB 0: INSTALLED SERVERS
                0 -> {
                    Text(
                        text = "Active Installed MCP Server Nodes (${servers.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (servers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No MCP servers installed.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MatrixTextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { selectedTab = 1 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MatrixGreenPrimary,
                                        contentColor = MatrixBlack
                                    )
                                ) {
                                    Text("Browse Awesome MCP Catalog")
                                }
                            }
                        }
                    } else {
                        servers.forEach { server ->
                            InstalledServerItem(
                                server = server,
                                tools = tools.filter { it.serverId == server.serverId },
                                onToggle = { enabled -> onToggleServer(server.serverId, enabled) },
                                onRemove = { onUninstallServer(server.serverId) },
                                onExecuteTool = onExecuteTool
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // TAB 1: AWESOME MCP CATALOG (MARKETPLACE)
                1 -> {
                    Text(
                        text = "Awesome MCP Servers — Pre-configured Streamed & Remote Nodes:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Search field
                    OutlinedTextField(
                        value = catalogSearchQuery,
                        onValueChange = { catalogSearchQuery = it },
                        label = { Text("Search Awesome MCP Servers (e.g. crawler, python, git, sql)") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MatrixGreenPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Chips
                    val categories = listOf("ALL", "WEB_SEARCH", "CODE_GIT", "CODE_EXEC", "DATABASE", "COMMUNICATION", "PRODUCTIVITY", "STORAGE", "UTILITY")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
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

                    Spacer(modifier = Modifier.height(10.dp))

                    val filteredCatalog = catalogServers.filter { s ->
                        val matchesQuery = catalogSearchQuery.isBlank() ||
                                s.name.contains(catalogSearchQuery, ignoreCase = true) ||
                                s.description.contains(catalogSearchQuery, ignoreCase = true) ||
                                s.tags.contains(catalogSearchQuery, ignoreCase = true)
                        val matchesCategory = selectedCategoryFilter == "ALL" || s.category.equals(selectedCategoryFilter, ignoreCase = true)
                        matchesQuery && matchesCategory
                    }

                    if (filteredCatalog.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (catalogServers.isEmpty()) "All catalog servers are installed!" else "No servers match search filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MatrixTextSecondary
                            )
                        }
                    } else {
                        filteredCatalog.forEach { server ->
                            CatalogServerItem(
                                server = server,
                                onInstall = { onInstallServer(server) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // TAB 2: ADD CUSTOM SERVER
                2 -> {
                    Text(
                        text = "Register Custom Remote MCP Server (SSE / Streamable HTTP):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect any Anthropic Model Context Protocol compliant server endpoint.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Server Name (e.g. My Custom Vector MCP)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customEndpoint,
                        onValueChange = { customEndpoint = it },
                        label = { Text("Endpoint URL (e.g. https://domain.com/mcp/sse)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Transport Protocol:", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary)
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
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customAuth,
                        onValueChange = { customAuth = it },
                        label = { Text("Optional Authorization Token / Bearer Key") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customDesc,
                        onValueChange = { customDesc = it },
                        label = { Text("Server Capabilities Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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
                        Text("Connect & Register MCP Server Node")
                    }
                }
            }
        }
    }
}

@Composable
private fun InstalledServerItem(
    server: McpServer,
    tools: List<McpTool>,
    onToggle: (Boolean) -> Unit,
    onRemove: () -> Unit,
    onExecuteTool: (String) -> Unit
) {
    var expandedTools by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixGreenContainer.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (server.isConnected) MatrixGreenPrimary.copy(alpha = 0.6f) else MatrixBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(text = server.icon, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MatrixTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (server.isConnected) MatrixGreenPrimary else MatrixRedAlert, CircleShape)
                        )
                    }
                    Text(
                        text = server.endpoint,
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = server.isConnected,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MatrixGreenPrimary,
                    checkedTrackColor = MatrixGreenContainer,
                    uncheckedThumbColor = MatrixTextSecondary,
                    uncheckedTrackColor = MatrixBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = server.description,
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Transport Badges & Action Buttons
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
                        Text(server.transport.replace("_", " "), style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(MatrixBlack, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("${tools.size.coerceAtLeast(server.toolsCount)} TOOLS", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { expandedTools = !expandedTools },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenContainer,
                        contentColor = MatrixGreenPrimary
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Tools", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (expandedTools) "Hide" else "Tools", fontSize = 10.sp)
                }

                Button(
                    onClick = onRemove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixRedAlert.copy(alpha = 0.2f),
                        contentColor = MatrixRedAlert
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Uninstall", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove", fontSize = 10.sp)
                }
            }
        }

        // Expandable Tools list
        AnimatedVisibility(visible = expandedTools) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(MatrixBlack.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text("Registered Tools on Node:", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary)
                Spacer(modifier = Modifier.height(4.dp))

                if (tools.isEmpty()) {
                    Text("No tools currently exposed (Server paused or initializing).", style = MaterialTheme.typography.bodySmall, color = MatrixTextSecondary, fontSize = 11.sp)
                } else {
                    tools.forEach { tool ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tool.name, style = MaterialTheme.typography.labelMedium, color = MatrixTextPrimary, fontSize = 11.sp)
                                Text(tool.description, style = MaterialTheme.typography.bodySmall, color = MatrixTextSecondary, fontSize = 10.sp, maxLines = 1)
                            }
                            Button(
                                onClick = { onExecuteTool(tool.name) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatrixGreenPrimary,
                                    contentColor = MatrixBlack
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Test", tint = MatrixBlack, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Test", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogServerItem(
    server: McpServer,
    onInstall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixGreenContainer.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .border(1.dp, MatrixBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(text = server.icon, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = server.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MatrixTextPrimary
                    )
                    Text(
                        text = server.endpoint,
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onInstall,
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

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = server.description,
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (server.isStreamed) {
                Box(
                    modifier = Modifier
                        .background(MatrixGreenPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(0.5.dp, MatrixGreenPrimary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(server.transport.replace("_", " "), style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
                }
            }
            Box(
                modifier = Modifier
                    .background(MatrixBlack, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("${server.toolsCount} TOOLS", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
            }
            Box(
                modifier = Modifier
                    .background(MatrixBlack, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(server.category, style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
            }
        }
    }
}
