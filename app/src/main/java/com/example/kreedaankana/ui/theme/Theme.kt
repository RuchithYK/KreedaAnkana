package com.example.kreedaankana.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
private val DarkColorScheme = darkColorScheme(
    primary = KhelWhite,       // Main text and prominent icons
    onPrimary = KhelBlack,
    secondary = KhelGold,      // Accents like the winner border
    background = KhelBlack,    // Main app background
    surface = SurfaceGrey,     // Card backgrounds (like the Calendar slots)
    onBackground = KhelWhite,
    onSurface = KhelWhite
)

private val LightColorScheme = lightColorScheme(
    primary = KhelBlack,
    onPrimary = KhelWhite,
    secondary = KhelGold,
    background = KhelWhite,
    surface = LightGrey,
    onBackground = KhelBlack,
    onSurface = KhelBlack
)

@Composable
fun KreedaAnkanaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}