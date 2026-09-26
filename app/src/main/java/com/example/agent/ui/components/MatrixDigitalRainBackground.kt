package com.example.agent.ui.components

import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.random.Random

// Matrix glyph alphabet: Authentic Katakana characters, digits, binary, and math symbols
private val MATRIX_GLYPHS = (
    "ｦｱｳｴｵｶｷｹｺｻｼｽｾｿﾀﾂﾃﾅﾆﾇﾈﾊﾋﾎﾏﾐﾑﾒﾓﾔﾕﾗﾘﾜ" +
    "0123456789" +
    "Z0101XYZ+-*:=<>"
).toCharArray()

private val isRunningInRobolectricTest: Boolean by lazy {
    try {
        Class.forName("org.robolectric.Robolectric") != null
    } catch (e: Throwable) {
        false
    }
}

private class RainDrop(
    val columnX: Float,
    var headY: Float,
    var baseSpeed: Float,
    val length: Int,
    var glyphs: CharArray,
    var isSurgeColumn: Boolean = false
)

@Composable
fun MatrixDigitalRainBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.38f,
    isProcessing: Boolean = false,
    activityLevel: Float = 0.0f,
    content: @Composable () -> Unit
) {
    // If in JVM Robolectric tests, avoid starting infinite transition loops
    val tick: Float = if (isRunningInRobolectricTest) {
        0f
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "MatrixDigitalRain")
        val anim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 10000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 60000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "RainTick"
        )
        anim
    }

    // Dynamic processing glow pulse
    val glowPulse: Float = if (isRunningInRobolectricTest || (!isProcessing && activityLevel <= 0.05f)) {
        1f
    } else {
        val pulseTransition = rememberInfiniteTransition(label = "ActivityPulse")
        val glow by pulseTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "GlowAnim"
        )
        glow
    }

    val drops = remember { mutableListOf<RainDrop>() }
    val headPaint = remember {
        Paint().apply {
            isAntiAlias = true
            typeface = Typeface.MONOSPACE
            textSize = 28f
        }
    }
    val trailPaint = remember {
        Paint().apply {
            isAntiAlias = true
            typeface = Typeface.MONOSPACE
            textSize = 26f
        }
    }
    val flarePaint = remember {
        Paint().apply {
            isAntiAlias = true
            typeface = Typeface.MONOSPACE
            textSize = 30f
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val charHeight = 32f
            val columnSpacing = 28f

            // Register recomposition dependency on clock tick
            val currentTick = tick

            // Processing multipliers: when agent is thinking or delegating tasks, speed and energy surge
            val speedMultiplier = if (isProcessing || activityLevel > 0.1f) {
                1.0f + (activityLevel.coerceAtLeast(0.5f) * 2.2f)
            } else {
                1.0f
            }

            val mutationChance = if (isProcessing || activityLevel > 0.1f) 0.18f else 0.04f

            // Initialize or adjust column count to screen width
            val requiredColumns = (width / columnSpacing).toInt() + 1
            if (drops.size != requiredColumns && requiredColumns > 0) {
                drops.clear()
                for (i in 0 until requiredColumns) {
                    val len = Random.nextInt(14, 28)
                    val glyphArr = CharArray(len) { MATRIX_GLYPHS[Random.nextInt(MATRIX_GLYPHS.size)] }
                    drops.add(
                        RainDrop(
                            columnX = i * columnSpacing + Random.nextFloat() * 4f,
                            headY = Random.nextFloat() * (height + 500f) - 400f,
                            baseSpeed = Random.nextFloat() * 4.5f + 3.5f,
                            length = len,
                            glyphs = glyphArr,
                            isSurgeColumn = i % 4 == 0
                        )
                    )
                }
            }

            drawIntoCanvas { composeCanvas ->
                val canvas = composeCanvas.nativeCanvas

                for (drop in drops) {
                    // Advance vertical position based on activity level
                    if (!isRunningInRobolectricTest) {
                        val effectiveSpeed = drop.baseSpeed * speedMultiplier * (if (drop.isSurgeColumn && isProcessing) 1.4f else 1.0f)
                        drop.headY += effectiveSpeed

                        if (Random.nextFloat() < mutationChance) {
                            val mutateIdx = Random.nextInt(drop.length)
                            drop.glyphs[mutateIdx] = MATRIX_GLYPHS[Random.nextInt(MATRIX_GLYPHS.size)]
                        }

                        val tailY = drop.headY - (drop.length * charHeight)
                        if (tailY > height) {
                            drop.headY = -Random.nextFloat() * 300f - 50f
                        }
                    }

                    // Render vertical character stream
                    for (i in 0 until drop.length) {
                        val charY = drop.headY - (i * charHeight)
                        if (charY in -30f..height + 30f && i < drop.glyphs.size) {
                            val glyphStr = drop.glyphs[i].toString()

                            if (i == 0) {
                                // Leading head glyph (responds dynamically to agent activity)
                                if (isProcessing || activityLevel > 0.2f) {
                                    // High energy cyan/neon green flare when actively computing
                                    flarePaint.color = AndroidColor.argb(
                                        (255 * alpha * glowPulse).toInt().coerceIn(0, 255),
                                        (180 + 75 * glowPulse).toInt().coerceIn(0, 255),
                                        255,
                                        240
                                    )
                                    flarePaint.setShadowLayer(
                                        14f * glowPulse,
                                        0f,
                                        0f,
                                        AndroidColor.argb(230, 0, 255, 180)
                                    )
                                    canvas.drawText(glyphStr, drop.columnX, charY, flarePaint)
                                } else {
                                    // Standard calm Matrix neon white-green leading glyph
                                    headPaint.color = AndroidColor.argb(
                                        (245 * alpha).toInt().coerceIn(0, 255),
                                        235,
                                        255,
                                        240
                                    )
                                    headPaint.setShadowLayer(8f, 0f, 0f, AndroidColor.argb(200, 0, 255, 65))
                                    canvas.drawText(glyphStr, drop.columnX, charY, headPaint)
                                }
                            } else {
                                // Trailing green gradient
                                val fadeRatio = (1f - (i.toFloat() / drop.length)).coerceIn(0.05f, 1f)
                                val greenAlpha = (220 * fadeRatio * alpha * (if (isProcessing) 1.25f else 1.0f)).toInt().coerceIn(0, 255)
                                
                                val redInt = if (isProcessing) (30 * (1f - fadeRatio)).toInt() else 0
                                val greenInt = (180 + 75 * fadeRatio).toInt().coerceIn(100, 255)
                                val blueInt = if (isProcessing) (120 * fadeRatio).toInt() else 45

                                trailPaint.color = AndroidColor.argb(greenAlpha, redInt, greenInt, blueInt)
                                canvas.drawText(glyphStr, drop.columnX, charY, trailPaint)
                            }
                        }
                    }
                }
            }

            // Darkening vignette for foreground text legibility with dynamic processing glow
            val overlayTop = if (isProcessing) Color(0x99001A08) else Color(0xBB020703)
            val overlayMid = if (isProcessing) Color(0x66002A10) else Color(0x88030904)
            val overlayBot = if (isProcessing) Color(0xAA001506) else Color(0xCC020703)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(overlayTop, overlayMid, overlayBot)
                )
            )
        }

        content()
    }
}
