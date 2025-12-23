package com.widgetkit.dsl.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Jetpack Glance의 GlanceTheme를 custom GlanceTheme로 변환하는 유틸리티
 */
object GlanceThemeConverter {

    /**
     * Glance composable context에서 GlanceTheme를 가져와서 custom GlanceTheme로 변환
     * Glance 위젯 렌더링 시 사용
     * 
     * Note: 현재 GlanceTheme API가 제한적이므로 Context를 파라미터로 받아 기본 테마를 생성합니다.
     * 향후 GlanceTheme API가 확장되면 실제 테마 값을 사용할 수 있습니다.
     */
    @Composable
    fun fromGlanceTheme(context: android.content.Context): GlanceTheme {
        // GlanceTheme.colors는 현재 버전에서 사용할 수 없으므로
        // Context를 통해 기본 테마를 생성합니다
        // TODO: GlanceTheme API가 확장되면 실제 테마 값 사용
        return createDefaultTheme(context)
    }

    /**
     * Context를 기반으로 다크 모드 여부를 확인하고 기본 테마 생성
     * Fallback으로 사용
     */
    fun createDefaultTheme(context: Context): GlanceTheme {
        val isDarkMode = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        
        // 기본 색상 (라이트/다크 모드에 따라)
        return if (isDarkMode) {
            GlanceTheme(
                surface = Color(0xFF121212).toArgb(),
                surfaceVariant = Color(0xFF1E1E1E).toArgb(),
                onSurface = Color(0xFFFFFFFF).toArgb(),
                onSurfaceVariant = Color(0xFFB3B3B3).toArgb(),
                primary = Color(0xFFBB86FC).toArgb(),
                outlineVariant = Color(0xFF3A3A3A).toArgb(),
                error = Color(0xFFCF6679).toArgb()
            )
        } else {
            GlanceTheme(
                surface = Color(0xFFFFFFFF).toArgb(),
                surfaceVariant = Color(0xFFF5F5F5).toArgb(),
                onSurface = Color(0xFF000000).toArgb(),
                onSurfaceVariant = Color(0xFF616161).toArgb(),
                primary = Color(0xFF6200EE).toArgb(),
                outlineVariant = Color(0xFFE0E0E0).toArgb(),
                error = Color(0xFFB00020).toArgb()
            )
        }
    }
}

