package com.example.agent.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixDarkGray
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun OnboardingCard(
    onComplete: (String, String, String) -> Unit,
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MatrixGreenPrimary,
        unfocusedBorderColor = MatrixBorder,
        focusedLabelColor = MatrixGreenPrimary,
        unfocusedLabelColor = MatrixTextSecondary,
        focusedTextColor = MatrixTextPrimary,
        unfocusedTextColor = MatrixTextPrimary
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MatrixSurface, RoundedCornerShape(16.dp))
            .border(1.5.dp, MatrixBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
            .semantics { contentDescription = "Agent Onboarding Card" }
    ) {
        // Header with Close / Skip button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MatrixGreenPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MatrixGreenPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Welcome to Agent Smith",
                        style = MaterialTheme.typography.titleMedium,
                        color = MatrixGreenPrimary
                    )
                    Text(
                        text = "Step $step of 3 • Autonomous Assistant Setup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MatrixTextSecondary
                    )
                }
            }

            IconButton(
                onClick = onSkip,
                modifier = Modifier.testTag("onboarding_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Skip Onboarding",
                    tint = MatrixTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (step) {
            1 -> {
                // Step 1: User Profile Setup
                Text(
                    text = "Let's set up your profile for voice calls and automated task dispatching.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatrixTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    placeholder = { Text("e.g. Thomas Anderson") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input"),
                    colors = fieldColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("e.g. user@example.com") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_email_input"),
                    colors = fieldColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("e.g. +1 555 0199") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_phone_input"),
                    colors = fieldColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MatrixTextSecondary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Skip Setup")
                    }
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("onboarding_step1_next_button")
                    ) {
                        Text("Continue")
                    }
                }
            }

            2 -> {
                // Step 2: Permission Rationale at Point-of-Need
                Text(
                    text = "Why Agent Smith requests device permissions:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MatrixTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                PermissionRationaleRow(
                    icon = Icons.Default.Mic,
                    title = "Microphone (Local Wake-Word)",
                    description = "Used for on-device wake-word detection. Raw audio is processed locally and never recorded or uploaded without your command."
                )
                Spacer(modifier = Modifier.height(8.dp))

                PermissionRationaleRow(
                    icon = Icons.Default.Phone,
                    title = "Phone & Calling",
                    description = "Enables automated calling missions (e.g. booking restaurant reservations or confirming clinic appointments)."
                )
                Spacer(modifier = Modifier.height(8.dp))

                PermissionRationaleRow(
                    icon = Icons.Default.Notifications,
                    title = "Notifications & Foreground Tasks",
                    description = "Keeps scheduled background tasks alive and notifies you when proactive automation executes."
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { step = 1 },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MatrixTextSecondary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                    Button(
                        onClick = { step = 3 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("onboarding_step2_next_button")
                    ) {
                        Text("Privacy Notice")
                    }
                }
            }

            3 -> {
                // Step 3: Privacy & Data Flow Commitment
                Card(
                    colors = CardDefaults.cardColors(containerColor = MatrixDarkGray.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MatrixGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transparent Data Flows",
                                style = MaterialTheme.typography.titleSmall,
                                color = MatrixGreenPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• 🔒 On-Device: Personal profile, call history, task schedules, and local memory are stored in an encrypted database backed by Android Keystore.\n• ☁️ Cloud AI: Only text prompts you explicitly send are forwarded via secure TLS to the configured AI model.\n• 🚫 No Third-Party Tracking: No advertising SDKs, no behavioral trackers, and no unauthorized cloud telemetry.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MatrixTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MatrixTextSecondary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                    Button(
                        onClick = { onComplete(name, email, phone) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreenPrimary,
                            contentColor = MatrixBlack
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("onboarding_complete_button")
                    ) {
                        Text("Get Started")
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRationaleRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MatrixDarkGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MatrixGreenPrimary,
            modifier = Modifier
                .size(22.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MatrixTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MatrixTextSecondary
            )
        }
    }
}
