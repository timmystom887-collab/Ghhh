package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.agent.data.model.SubAgentClarificationRequest
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubAgentClarificationDialog(
    request: SubAgentClarificationRequest,
    onRespond: (SubAgentClarificationRequest, String) -> Unit,
    onDismiss: () -> Unit
) {
    var responseText by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(MatrixSurface, RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFFFFD600), RoundedCornerShape(20.dp))
                .testTag("clarification_dialog"),
            color = MatrixSurface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with Agent Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFD600).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFFFD600), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD600),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(
                                    text = "SUB-AGENT QUERY",
                                    color = Color(0xFFFFD600),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("clarification_dismiss_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MatrixTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-Agent Identity Title
                Text(
                    text = "🕶️ ${request.subAgentName}",
                    color = MatrixGreenPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("clarification_subagent_title")
                )
                Text(
                    text = request.subAgentRole,
                    color = MatrixTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Clarification Question Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MatrixBlack.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MatrixBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "Clarification Requested:",
                                color = Color(0xFF00E5FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = request.question,
                            color = MatrixTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.testTag("clarification_question_text")
                        )

                        if (request.contextSnippet.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Context: ${request.contextSnippet}",
                                color = MatrixTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Suggested Option Chips
                if (request.suggestedOptions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Suggested Decisions:",
                        color = MatrixTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        request.suggestedOptions.forEach { option ->
                            val isSelected = selectedOption == option
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedOption = option
                                    responseText = option
                                },
                                label = {
                                    Text(
                                        text = option,
                                        fontSize = 12.sp,
                                        color = if (isSelected) MatrixBlack else MatrixTextPrimary
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MatrixGreenPrimary,
                                    containerColor = MatrixBlack
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) MatrixGreenPrimary else MatrixBorder,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                modifier = Modifier.testTag("clarification_option_chip_$option")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct User Input Field
                OutlinedTextField(
                    value = responseText,
                    onValueChange = {
                        responseText = it
                        selectedOption = null
                    },
                    label = {
                        Text(
                            text = "Your Decision / Instructions",
                            fontSize = 12.sp,
                            color = MatrixTextSecondary
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Type clarification for sub-agent...",
                            fontSize = 12.sp,
                            color = MatrixTextSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clarification_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MatrixBlack,
                        unfocusedContainerColor = MatrixBlack,
                        focusedBorderColor = MatrixGreenPrimary,
                        unfocusedBorderColor = MatrixBorder,
                        focusedTextColor = MatrixTextPrimary,
                        unfocusedTextColor = MatrixTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("clarification_cancel_button")
                    ) {
                        Text("Dismiss", color = MatrixTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (responseText.isNotBlank()) {
                                onRespond(request, responseText)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("clarification_submit_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = MatrixBlack,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Submit Decision",
                            color = MatrixBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
