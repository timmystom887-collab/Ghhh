package com.example.agent.ui.chat.components

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.agent.ui.theme.MatrixBlack
import com.example.agent.ui.theme.MatrixBorder
import com.example.agent.ui.theme.MatrixGreenContainer
import com.example.agent.ui.theme.MatrixGreenPrimary
import com.example.agent.ui.theme.MatrixSurface
import com.example.agent.ui.theme.MatrixTextPrimary
import com.example.agent.ui.theme.MatrixTextSecondary

@Composable
fun UserGuideCard(
    onNavigateSection: (String) -> Unit = {},
    onExecuteCommand: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var showEmbeddedWebView by remember { mutableStateOf(false) }
    var selectedSection by remember { mutableStateOf("overview") }

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
                    contentDescription = "User Guide",
                    tint = MatrixGreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📖 AGENT SMITH USER GUIDE & MANUAL",
                    style = MaterialTheme.typography.titleSmall,
                    color = MatrixGreenPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "HTML v4.5 (HERMES)",
                style = MaterialTheme.typography.labelSmall,
                color = MatrixTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Comprehensive classified documentation covering Hermes XML tool calling, Deep Research agents, battery governance, real phone calls, MCP tool synthesis, and KeyStore encryption.",
            style = MaterialTheme.typography.bodySmall,
            color = MatrixTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Topic Shortcuts
        Text(
            text = "Quick Documentation Topics:",
            style = MaterialTheme.typography.labelMedium,
            color = MatrixGreenPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        val topicButtons = listOf(
            Triple("⚡ Hermes Agent", Icons.Default.AutoAwesome, "hermes-protocol"),
            Triple("🔬 Deep Research", Icons.Default.Search, "deep-research"),
            Triple("🔋 Battery Monitor", Icons.Default.Settings, "power-governance"),
            Triple("🧠 Thinking Routines", Icons.Default.Psychology, "thinking-engine"),
            Triple("👁️ Cognitive Routines", Icons.Default.Hub, "cognitive-engine"),
            Triple("🔒 KeyStore Security", Icons.Default.Code, "keystore-security"),
            Triple("📞 Autonomous Calls", Icons.Default.Call, "telephony-voice"),
            Triple("🌐 MCP Tool Synthesis", Icons.Default.Storage, "mcp-synthesis")
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            topicButtons.chunked(2).forEach { rowPair ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    rowPair.forEach { (title, icon, sectionId) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedSection = sectionId
                                    showEmbeddedWebView = true
                                    onNavigateSection(sectionId)
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = MatrixGreenContainer.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MatrixBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = title,
                                    tint = MatrixGreenPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MatrixTextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showEmbeddedWebView = !showEmbeddedWebView },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showEmbeddedWebView) MatrixGreenContainer else MatrixGreenPrimary,
                    contentColor = if (showEmbeddedWebView) MatrixGreenPrimary else MatrixBlack
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (showEmbeddedWebView) Icons.Default.Close else Icons.Default.Fullscreen,
                    contentDescription = "Toggle Viewer",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showEmbeddedWebView) "Close Viewer" else "Open In-App Guide",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Embedded In-App HTML Guide Viewer
        AnimatedVisibility(visible = showEmbeddedWebView) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(MatrixBlack, RoundedCornerShape(12.dp))
                    .border(1.dp, MatrixGreenPrimary, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TERMINAL BROWSER // file:///android_asset/user_guide.html#$selectedSection",
                        style = MaterialTheme.typography.labelSmall,
                        color = MatrixGreenPrimary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(
                        onClick = { showEmbeddedWebView = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MatrixGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.allowFileAccess = true
                            settings.allowContentAccess = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                    if (url == null) return false
                                    return when {
                                        url.startsWith("command:") -> {
                                            val cmd = url.removePrefix("command:")
                                            onExecuteCommand(cmd)
                                            true
                                        }
                                        url.startsWith("http://") || url.startsWith("https://") -> {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                ctx.startActivity(intent)
                                            } catch (_: Exception) {}
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            }
                            loadUrl("file:///android_asset/user_guide.html#$selectedSection")
                        }
                    },
                    update = { webView ->
                        webView.evaluateJavascript(
                            "location.hash = '$selectedSection'; document.getElementById('$selectedSection')?.scrollIntoView({behavior: 'smooth'});",
                            null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                )
            }
        }
    }
}
