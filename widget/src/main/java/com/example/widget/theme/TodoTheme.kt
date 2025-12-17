package com.example.widget.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors (MainActivity와 동일)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFFFFFF),  // White
    onPrimary = Color(0xFF1C1B1F),  // Dark text on white
    primaryContainer = Color(0xFFF5F5F5),  // Very light gray
    onPrimaryContainer = Color(0xFF000000),  // Black text
    secondary = Color(0xFF2196F3),  // Material Blue
    onSecondary = Color(0xFFFFFFFF),  // White text on blue
    secondaryContainer = Color(0xFFBBDEFB),  // Light blue container
    onSecondaryContainer = Color(0xFF0D47A1),  // Dark blue text
    tertiary = Color(0xFF03A9F4),  // Light Blue
    onTertiary = Color(0xFFFFFFFF),  // White text
    tertiaryContainer = Color(0xFFE1F5FE),  // Very light blue
    onTertiaryContainer = Color(0xFF01579B),  // Dark blue
    error = Color(0xFFB00020),  // Material error red
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFFFF),  // Pure white
    onBackground = Color(0xFF1C1B1F),  // Dark text
    surface = Color(0xFFFFFFFF),  // White surface
    onSurface = Color(0xFF1C1B1F),  // Dark text
    surfaceVariant = Color(0xFFF5F5F5),  // Light gray variant
    onSurfaceVariant = Color(0xFF49454F),  // Medium dark text
    outline = Color(0xFF79747E),  // Medium gray outline
    outlineVariant = Color(0xFFCAC4D0),  // Light gray outline
    scrim = Color(0xFF000000),  // Black scrim
    inverseSurface = Color(0xFF313033),  // Dark inverse surface
    inverseOnSurface = Color(0xFFF4EFF4),  // Light text on dark
    inversePrimary = Color(0xFFE0E0E0),  // Light gray inverse
    surfaceTint = Color(0xFF2196F3),  // Blue tint
)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF424242),  // Dark gray
    onPrimary = Color(0xFFFFFFFF),  // White text
    primaryContainer = Color(0xFF2C2C2C),  // Darker gray container
    onPrimaryContainer = Color(0xFFFFFFFF),  // White text
    secondary = Color(0xFF64B5F6),  // Light blue for dark mode
    onSecondary = Color(0xFF000000),  // Black text on light blue
    secondaryContainer = Color(0xFF1565C0),  // Medium blue container
    onSecondaryContainer = Color(0xFFE3F2FD),  // Very light blue text
    tertiary = Color(0xFF81D4FA),  // Light blue accent
    onTertiary = Color(0xFF000000),  // Black text
    tertiaryContainer = Color(0xFF0277BD),  // Dark blue container
    onTertiaryContainer = Color(0xFFE1F5FE),  // Very light blue text
    error = Color(0xFFCF6679),  // Light red for dark mode
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),  // Material dark background
    onBackground = Color(0xFFE6E1E5),  // Light text
    surface = Color(0xFF1E1E1E),  // Dark surface
    onSurface = Color(0xFFE6E1E5),  // Light text
    surfaceVariant = Color(0xFF2C2C2C),  // Medium dark variant
    onSurfaceVariant = Color(0xFFCAC4D0),  // Light gray text
    outline = Color(0xFF938F99),  // Medium light gray outline
    outlineVariant = Color(0xFF49454F),  // Dark gray outline
    scrim = Color(0xFF000000),  // Black scrim
    inverseSurface = Color(0xFFE6E1E5),  // Light inverse surface
    inverseOnSurface = Color(0xFF313033),  // Dark text on light
    inversePrimary = Color(0xFF616161),  // Medium gray inverse
    surfaceTint = Color(0xFF64B5F6),  // Light blue tint
)

@Composable
fun TodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) {
                DarkColorScheme.surface.toArgb()
            } else {
                LightColorScheme.surface.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

