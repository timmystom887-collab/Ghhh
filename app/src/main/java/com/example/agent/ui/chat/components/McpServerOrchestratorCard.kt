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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.data.model.McpServer
import com.example.agent.data.model.McpTool
import com.example.agent.data.model.McpWebRepository
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun McpServerOrchestratorCard(
    servers: List<McpServer>,
    webRepositories: List<McpWebRepository>,
    tools: List<McpTool>,
    isSearchingWeb: Boolean = false,
    onToggleServer: (String, Boolean) -> Unit,
    onSearchWebRepositories: (String) -> Unit = {},
    onAddStreamedServerFromRepo: (McpWebRepository) -> Unit = {},
    onUninstallServer: (String) -> Unit = {},
    onExecuteTool: (String) -> Unit = {},
    onAddCustomServer: (name: String, endpoint: String, transport: String, desc: String, auth: String) -> Unit = { _, _, _, _, _ -> },
    onResetDefaults: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Installed, 1: Web Repositories, 2: Tools, 3: + Custom
    var serverStatusFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, STREAMED, OFFLINE
    var webRepoQuery by remember { mutableStateOf("") }
    var selectedRepoCategory by remember { mutableStateOf("ALL") }

    // Custom Server Form State
    var customName by remember { mutableStateOf("") }
    var customEndpoint by remember { mutableStateOf("https://") }
    var customTransport by remember { mutableStateOf("HTTP_STREAMED_SSE") }
    var customDesc by remember { mutableStateOf("") }
    var customAuth by remember { mutableStateOf("") }

    val activeCount = servers.count { it.isConnected }
    val streamedCount = servers.count { it.isStreamed }

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
            .border(1.5.dp, MatrixGreenPrimary, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(MatrixGreenContainer, CircleShape)
                        .border(1.dp, MatrixGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Hub,
                        contentDescription = "MCP Core Hub",
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "🌐 MCP ORCHESTRATION & WEB REPOS",
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary
                    )
                    Text(
                        text = "Model Context Protocol Node Controller • Streamed SSE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(MatrixGreenContainer.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .border(1.dp, MatrixGreenPrimary, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$activeCount/${servers.size} ONLINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatrixGreenPrimary,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // STATUS METRIC ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MatrixBlack, RoundedCornerShape(10.dp))
                .border(1.dp, MatrixBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text("CONNECTED", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                    Text("$activeCount Active", style = MaterialTheme.typography.labelMedium, color = MatrixGreenPrimary)
                }
                Column {
                    Text("STREAMED SSE", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                    Text("$streamedCount Streamed", style = MaterialTheme.typography.labelMedium, color = MatrixTextPrimary)
                }
                Column {
                    Text("AGENT TOOLS", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 9.sp)
                    Text("${tools.size} Exposed", style = MaterialTheme.typography.labelMedium, color = MatrixGreenPrimary)
                }
            }

            IconButton(
                onClick = onResetDefaults,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Restore Default MCP Nodes",
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TABS
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
                text = { Text("Installed (${servers.size})", fontSize = 10.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Web Repos (${webRepositories.size})", fontSize = 10.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Tools (${tools.size})", fontSize = 10.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("+ Custom", fontSize = 10.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            // TAB 0: INSTALLED MCP SERVERS WITH TOGGLEABLE CONNECTIONS
            0 -> {
                Column {
                    // Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "ACTIVE ONLY", "STREAMED SSE", "STANDBY").forEach { filter ->
                            FilterChip(
                                selected = serverStatusFilter == filter,
                                onClick = { serverStatusFilter = filter },
                                label = { Text(filter, fontSize = 9.sp) },
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

                    val filteredServers = servers.filter { s ->
                        when (serverStatusFilter) {
                            "ACTIVE ONLY" -> s.isConnected
                            "STREAMED SSE" -> s.isStreamed
                            "STANDBY" -> !s.isConnected
                            else -> true
                        }
                    }

                    if (filteredServers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No servers matching filter. Switch tabs to discover Web MCP Repositories.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MatrixTextSecondary
                            )
                        }
                    } else {
                        filteredServers.forEach { server ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(
                                        if (server.isConnected) MatrixGreenContainer.copy(alpha = 0.35f) else MatrixBlack.copy(alpha = 0.6f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (server.isConnected) MatrixGreenPrimary.copy(alpha = 0.6f) else MatrixBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                // Server Title & Toggle Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = server.icon, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = server.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MatrixTextPrimary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                // Live Status Indicator Dot
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            if (server.isConnected) MatrixGreenPrimary else MatrixRedAlert.copy(alpha = 0.6f),
                                                            CircleShape
                                                        )
                                                )
                                            }
                                            Text(
                                                text = server.endpoint,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (server.isConnected) MatrixGreenPrimary else MatrixTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    // INDIVIDUAL CONNECTION TOGGLE
                                    Switch(
                                        checked = server.isConnected,
                                        onCheckedChange = { isChecked ->
                                            onToggleServer(server.serverId, isChecked)
                                        },
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
                                    color = MatrixTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Badges and Action Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (server.isStreamed) {
                                            Box(
                                                modifier = Modifier
                                                    .background(MatrixGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, MatrixGreenPrimary, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.Stream,
                                                        contentDescription = "Streamed",
                                                        tint = MatrixGreenPrimary,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("HTTP SSE", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 8.sp)
                                                }
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(MatrixBlack, RoundedCornerShape(4.dp))
                                                .border(0.5.dp, MatrixBorder, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                if (server.isConnected) "ACTIVE FOR AGENT" else "OFFLINE",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (server.isConnected) MatrixGreenPrimary else MatrixTextSecondary,
                                                fontSize = 8.sp
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(MatrixBlack, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("${server.toolsCount} TOOLS", style = MaterialTheme.typography.labelSmall, color = MatrixTextSecondary, fontSize = 8.sp)
                                        }
                                    }

                                    Button(
                                        onClick = { onUninstallServer(server.serverId) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MatrixRedAlert.copy(alpha = 0.2f),
                                            contentColor = MatrixRedAlert
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Remove", fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: WEB REPOSITORY FINDER & AUTO-ADD HTTP STREAMED SERVER
            1 -> {
                Column {
                    Text(
                        text = "Find & Add MCP Repositories from the Web:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Search open-source MCP repositories across GitHub and community hubs. Add them instantly as HTTP streamed SSE servers to Agent Smith.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search Input with Action Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = webRepoQuery,
                            onValueChange = { webRepoQuery = it },
                            placeholder = { Text("Search MCP repos (e.g. brave, weather, e2b, postgres)...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = MatrixGreenPrimary)
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { onSearchWebRepositories(webRepoQuery) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatrixGreenPrimary,
                                contentColor = MatrixBlack
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isSearchingWeb,
                            modifier = Modifier.height(52.dp)
                        ) {
                            if (isSearchingWeb) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MatrixBlack,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Find", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Pills
                    val categories = listOf("ALL", "OFFICIAL", "DATABASE", "WEB_SEARCH", "CODE_GIT", "CODE_EXEC", "BROWSER_AUTOMATION", "UTILITY", "DEVOPS")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedRepoCategory == cat,
                                onClick = { selectedRepoCategory = cat },
                                label = { Text(cat.replace("_", " "), fontSize = 9.sp) },
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

                    val filteredRepos = webRepositories.filter { r ->
                        val matchesQuery = webRepoQuery.isBlank() ||
                                r.name.contains(webRepoQuery, true) ||
                                r.description.contains(webRepoQuery, true) ||
                                r.detectedTools.any { it.contains(webRepoQuery, true) }
                        val matchesCat = selectedRepoCategory == "ALL" || r.category.equals(selectedRepoCategory, true)
                        matchesQuery && matchesCat
                    }

                    if (filteredRepos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No web repositories matched '$webRepoQuery'. Click 'Find' to query GitHub API.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MatrixTextSecondary
                            )
                        }
                    } else {
                        filteredRepos.forEach { repo ->
                            // Check if this repository is already installed
                            val isAlreadyInstalled = servers.any { s ->
                                s.endpoint == repo.suggestedStreamEndpoint || s.name.contains(repo.name.split("/").last(), true)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MatrixBlack, RoundedCornerShape(10.dp))
                                    .border(1.dp, if (isAlreadyInstalled) MatrixGreenPrimary else MatrixBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = repo.fullName,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MatrixTextPrimary,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(MatrixGreenContainer, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.Star,
                                                        contentDescription = "Stars",
                                                        tint = MatrixGreenPrimary,
                                                        modifier = Modifier.size(9.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = if (repo.stars > 1000) String.format("%.1fk", repo.stars / 1000.0) else repo.stars.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MatrixGreenPrimary,
                                                        fontSize = 8.sp
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = repo.htmlUrl,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MatrixTextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }

                                    // 1-CLICK AUTO-ADD BUTTON
                                    if (isAlreadyInstalled) {
                                        Box(
                                            modifier = Modifier
                                                .background(MatrixGreenContainer, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Added",
                                                    tint = MatrixGreenPrimary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("ADDED", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 9.sp)
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { onAddStreamedServerFromRepo(repo) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MatrixGreenPrimary,
                                                contentColor = MatrixBlack
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.CloudDownload,
                                                contentDescription = "Add Streamed",
                                                tint = MatrixBlack,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Auto-Add SSE", fontSize = 10.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = repo.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MatrixTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Suggested Endpoint and Detected Tools
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MatrixSurface, RoundedCornerShape(6.dp))
                                        .padding(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Stream,
                                            contentDescription = "SSE",
                                            tint = MatrixGreenPrimary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Streamed SSE: ${repo.suggestedStreamEndpoint}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MatrixGreenPrimary,
                                            fontSize = 9.sp
                                        )
                                    }
                                    if (repo.detectedTools.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Detected Tools: " + repo.detectedTools.joinToString(", "),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MatrixTextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: ACTIVE TOOLS & TELEMETRY
            2 -> {
                Column {
                    Text(
                        text = "Exposed Tools for Agent Smith Orchestrator (${tools.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "These capabilities are currently callable by the agent. Disable servers on Tab 0 to revoke individual tool permissions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (tools.isEmpty()) {
                        Text(
                            text = "No tools available. Connect servers on Tab 0 to expose tools.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixTextSecondary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        tools.forEach { tool ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .background(MatrixGreenContainer.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, MatrixBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
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
                                            Text("[STREAMED SSE]", style = MaterialTheme.typography.labelSmall, color = MatrixGreenPrimary, fontSize = 8.sp)
                                        }
                                    }
                                    Text(
                                        text = tool.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MatrixTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                Button(
                                    onClick = { onExecuteTool(tool.name) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MatrixGreenPrimary,
                                        contentColor = MatrixBlack
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = MatrixBlack, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Test", fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: CUSTOM ENDPOINT FORM
            3 -> {
                Column {
                    Text(
                        text = "Add Custom Remote or Streamed MCP Server:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MatrixGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Server Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customEndpoint,
                        onValueChange = { customEndpoint = it },
                        label = { Text("Endpoint URL (e.g. https://api.domain.com/mcp/sse)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customDesc,
                        onValueChange = { customDesc = it },
                        label = { Text("Description & Tools Provided") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customAuth,
                        onValueChange = { customAuth = it },
                        label = { Text("Authorization Header / Bearer Token (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = customName.isNotBlank() && customEndpoint.length > 8
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Connect", tint = MatrixBlack)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Connect MCP Server Node")
                    }
                }
            }
        }
    }
}
