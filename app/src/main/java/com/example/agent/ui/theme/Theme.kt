package com.example.agent.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MatrixDarkColorScheme = darkColorScheme(
    primary = MatrixGreenPrimary,
    onPrimary = MatrixBlack,
    primaryContainer = MatrixGreenContainer,
    onPrimaryContainer = MatrixOnGreenContainer,
    secondary = MatrixGreenSecondary,
    onSecondary = MatrixBlack,
    secondaryContainer = MatrixSurfaceVariant,
    onSecondaryContainer = MatrixGreenTertiary,
    tertiary = MatrixGreenTertiary,
    onTertiary = MatrixBlack,
    background = MatrixBackground,
    onBackground = MatrixTextPrimary,
    surface = MatrixSurface,
    onSurface = MatrixTextPrimary,
    surfaceVariant = MatrixSurfaceVariant,
    onSurfaceVariant = MatrixTextSecondary,
    outline = MatrixBorder,
    error = MatrixRedAlert,
    onError = MatrixBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = MatrixDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MatrixBackground.toArgb()
                window.navigationBarColor = MatrixBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
