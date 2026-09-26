package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmationCard(
    actionDescription: String,
    confidenceScore: Int,
    onApprove: () -> Unit,
    onDeny: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(text = "Sensitive Action Consent Required", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = actionDescription, color = MaterialTheme.colorScheme.onErrorContainer)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Intent Confidence: $confidenceScore%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Button(onClick = onApprove) {
                Text(text = "Approve")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = onDeny) {
                Text(text = "Deny")
            }
        }
    }
}
