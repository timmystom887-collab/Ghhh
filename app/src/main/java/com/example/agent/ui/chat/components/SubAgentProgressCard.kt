package com.example.agent.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun SubAgentProgressCard(
    subAgentName: String,
    currentAction: String,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "⚡ ACTIVE SUB-ROUTINE:",
                style = MaterialTheme.typography.labelMedium,
                color = MatrixGreenPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = subAgentName,
                style = MaterialTheme.typography.titleSmall,
                color = MatrixTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = currentAction,
            style = MaterialTheme.typography.bodyMedium,
            color = MatrixTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MatrixGreenPrimary,
            trackColor = MatrixBlack
        )
    }
}
