package com.example.agent.ui.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun KnowledgeBaseCard(
    knowledgeList: List<KnowledgeEntity>,
    onAddKnowledge: (title: String, content: String, category: String, tags: String) -> Unit,
    onDeleteKnowledge: (Long) -> Unit,
    onQueryKnowledge: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddForm by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("GENERAL") }
    var newTags by remember { mutableStateOf("") }

    val categories = listOf("ALL", "ARCHITECTURE", "TOOLS", "MISSIONS", "LOCAL_AI", "MCP", "PROTOCOLS", "NOTES")

    val filteredList = knowledgeList.filter { item ->
        val matchesCategory = selectedCategory == "ALL" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.content.contains(searchQuery, ignoreCase = true) ||
                item.tags.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

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
                    Icons.Default.MenuBook,
                    contentDescription = "Knowledge Vault",
                    tint = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📚 MATRIX KNOWLEDGE VAULT & RAG",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${knowledgeList.size} ARTICLES",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Persistent encrypted neural knowledge base. Agent Smith automatically references these records during inference.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search knowledge records or tags...", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MatrixGreenPrimary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = MatrixTextSecondary)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    modifier = Modifier.clickable { selectedCategory = cat },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MatrixGreenPrimary else MatrixGreenContainer.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MatrixGreenPrimary else MatrixBorder
                    )
                ) {
                    Text(
                        text = cat,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MatrixBlack else MatrixTextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons (Add Knowledge record)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showAddForm = !showAddForm },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showAddForm) MatrixGreenContainer else MatrixGreenPrimary,
                    contentColor = if (showAddForm) MatrixGreenPrimary else MatrixBlack
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Toggle Add",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showAddForm) "Cancel" else "Add Knowledge Document",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Add Knowledge Form
        AnimatedVisibility(visible = showAddForm) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(MatrixGreenContainer.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .border(1.dp, MatrixGreenPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "📥 INGEST NEW KNOWLEDGE RECORD",
                    style = MaterialTheme.typography.labelMedium,
                    color = MatrixGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Document / Protocol Title") },
                    placeholder = { Text("e.g., Matrix Protocol 412: Sub-Agent Protocols") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newContent,
                    onValueChange = { newContent = it },
                    label = { Text("Knowledge Content & Instructions") },
                    placeholder = { Text("Detailed information, step-by-step procedures, or reference specs...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it.uppercase() },
                        label = { Text("Category") },
                        placeholder = { Text("GENERAL") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newTags,
                        onValueChange = { newTags = it },
                        label = { Text("Tags (comma separated)") },
                        placeholder = { Text("matrix, voice, rag") },
                        modifier = Modifier.weight(1f),
                        colors = textFieldColors,
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            onAddKnowledge(newTitle, newContent, newCategory, newTags)
                            newTitle = ""
                            newContent = ""
                            newTags = ""
                            showAddForm = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreenPrimary,
                        contentColor = MatrixBlack
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Save", tint = MatrixBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save to Neural Knowledge Vault", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Display list of knowledge items
        if (filteredList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "No knowledge records in this category." else "No records matching '$searchQuery'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MatrixTextSecondary
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredList.forEach { item ->
                    KnowledgeItemRow(
                        item = item,
                        onDelete = { onDeleteKnowledge(item.id) },
                        onQueryWithAI = { onQueryKnowledge(item.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun KnowledgeItemRow(
    item: KnowledgeEntity,
    onDelete: () -> Unit,
    onQueryWithAI: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixGreenContainer.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .border(1.dp, if (item.isPinned) MatrixGreenPrimary else MatrixBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (item.isPinned) Icons.Default.PushPin else Icons.Default.Description,
                    contentDescription = "Doc",
                    tint = MatrixGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MatrixGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[${item.category}]",
                            style = MaterialTheme.typography.labelSmall,
                            color = MatrixTextSecondary,
                            fontSize = 10.sp
                        )
                        if (item.tags.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🏷️ ${item.tags}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Row {
                IconButton(onClick = onQueryWithAI, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Query Agent Smith",
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MatrixTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (expanded) item.content else item.content.take(160) + if (item.content.length > 160) "..." else "",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextPrimary,
            lineHeight = 18.sp
        )

        if (item.content.length > 160) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (expanded) "▲ Show less" else "▼ Read full protocol",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixGreenPrimary,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}
