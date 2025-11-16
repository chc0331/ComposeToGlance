package com.example.composetoglance.ui.theme

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6dd58c),
    secondary = Color(0xFFb7ccb7),
    tertiary = Color(0xFFa0d0b0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006e2c),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF8af2a6),
    onPrimaryContainer = Color(0xFF002108),
    secondary = Color(0xFF506352),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFd3e8d2),
    onSecondaryContainer = Color(0xFF0e1f12),
    tertiary = Color(0xFF3a656a),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFbeebee),
    onTertiaryContainer = Color(0xFF002022),
    error = Color(0xFFba1a1a),
    errorContainer = Color(0xFFffdad6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0x802C622F),
    onBackground = Color(0xFF1a1c1a),
    surface = Color(0xFFfcfdf7),
    onSurface = Color(0xFF1a1c1a),
    surfaceVariant = Color(0xFFdee5d9),
    onSurfaceVariant = Color(0xFF424941),
    outline = Color(0xFF16420A),
    inverseOnSurface = Color(0xFFf0f1ec),
    inverseSurface = Color(0xFF2e312e),
    inversePrimary = Color(0xFF6dd58c),
    surfaceTint = Color(0xFF006e2c),
    outlineVariant = Color(0xFFc2c9be),
    scrim = Color.Black,
)

@Composable
fun ComposeToGlanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    Log.i("heec.choi","Glance : $dynamicColor $darkTheme")
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
