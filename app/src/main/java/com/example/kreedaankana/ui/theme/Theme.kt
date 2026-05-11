package com.example.kreedaankana.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Kreeda Ankana — Neon Sports dark theme
private val NeonSportsColorScheme = darkColorScheme(
    primary              = SportOrange,
    onPrimary            = SportWhite,
    primaryContainer     = SportSurface2,
    onPrimaryContainer   = SportOrangeLight,
    secondary            = SportGreen,
    onSecondary          = SportBlack,
    secondaryContainer   = SportSurface,
    onSecondaryContainer = SportGreen,
    background           = SportBlack,
    onBackground         = SportWhite,
    surface              = SportSurface,
    onSurface            = SportWhite,
    surfaceVariant       = SportSurface2,
    onSurfaceVariant     = SportGreyLight,
    outline              = SportBorderLight,
    error                = SportRed,
    onError              = SportWhite,
    errorContainer       = SportRedBg,
    onErrorContainer     = SportRed
)

@Composable
fun KreedaAnkanaTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SportBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = NeonSportsColorScheme,
        typography  = Typography,
        content     = content
    )
}