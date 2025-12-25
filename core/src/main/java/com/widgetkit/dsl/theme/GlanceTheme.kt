package com.widgetkit.dsl.theme

/**
 * Glance 위젯에서 사용할 색상 토큰 모음.
 *
 * 값은 ARGB Int 로 유지해서 DSL / Proto 계층에서 바로 사용할 수 있게 합니다.
 */
data class GlanceTheme(
    val surface: Int,
    val surfaceVariant: Int,
    val onSurface: Int,
    val onSurfaceVariant: Int,
    val primary: Int,
    val outlineVariant: Int,
    val error: Int
)


