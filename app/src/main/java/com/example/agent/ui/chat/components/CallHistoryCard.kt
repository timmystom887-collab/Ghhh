package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.data.local.entity.CallLogEntity
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixRedAlert
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallHistoryCard(
    callLogs: List<CallLogEntity>,
    onRedial: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

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
                Icon(
                    Icons.Default.PhoneInTalk,
                    contentDescription = "Call Telemetry",
                    tint = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "📞 MATRIX CALL TELEMETRY & LOGS",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary
                )
            }
            Text(
                text = "${callLogs.size} CALLS",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Persistent Room database registry of all incoming/outgoing voice missions, reservations, and verifications.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (callLogs.isEmpty()) {
            Text(
                text = "No call telemetry logged yet. Ask Agent Smith to make a reservation or call a business to record.",
                style = MaterialTheme.typography.bodySmall,
                color = MatrixTextSecondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            Column {
                callLogs.take(8).forEach { call ->
                    val timeStr = dateFormatter.format(Date(call.timestamp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(MatrixGreenContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CallMade,
                                    contentDescription = call.direction,
                                    tint = MatrixGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.padding(start = 4.dp))
                                Text(
                                    text = "${call.contactName} (${call.phoneNumber})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MatrixTextPrimary
                                )
                            }
                            Text(
                                text = "[$timeStr]",
                                style = MaterialTheme.typography.labelSmall,
                                color = MatrixTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Objective: ${call.objective} [${call.callType}]",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Summary: ${call.summary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onRedial(call.phoneNumber) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MatrixGreenContainer,
                                    contentColor = MatrixGreenPrimary
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Redial", tint = MatrixGreenPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.padding(start = 4.dp))
                                Text(text = "Re-Dial", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClearAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreenContainer,
                    contentColor = MatrixRedAlert
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = MatrixRedAlert)
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text(text = "Clear Call Logs", color = MatrixRedAlert)
            }
        }
    }
}
