package com.example.dsl.glance.converter

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.dsl.proto.Color as ProtoColor
import com.example.dsl.proto.ColorProvider

/**
 * Proto Color를 Glance Color로 변환
 */
object ColorConverter {
    /**
     * Proto Color를 Glance Color로 변환
     * @param protoColor Proto Color
     * @return Glance Color
     */
    fun toGlanceColor(protoColor: ProtoColor): Color {
        return Color(protoColor.argb)
    }

    /**
     * Proto ColorProvider를 Glance Color로 변환
     * 다크 모드 지원: 현재 테마에 따라 적절한 색상 선택
     * @param colorProvider Proto ColorProvider
     * @param context Context (다크 모드 감지용)
     * @return Glance Color
     */
    fun toGlanceColor(colorProvider: ColorProvider, context: Context): Color {
        // 리소스 ID가 있으면 리소스에서 색상 가져오기
        if (colorProvider.resId != 0) {
            val colorRes = context.resources.getColor(colorProvider.resId, context.theme)
            return Color(colorRes)
        }

        // 다크 모드 감지
        val isDarkMode = (context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        // 다크 모드 색상이 있으면 사용, 없으면 일반 색상 사용
        val color = when {
            isDarkMode && colorProvider.hasDarkColor() -> colorProvider.darkColor
            colorProvider.hasColor() -> colorProvider.color
            else -> null
        }

        return if (color != null) {
            toGlanceColor(color)
        } else {
            // 기본 색상 (검정)
            Color(0xFF000000.toInt())
        }
    }

    /**
     * Proto ColorProvider를 Glance ColorProvider로 변환
     * @param colorProvider Proto ColorProvider
     * @return Glance ColorProvider
     */
    fun toGlanceColorProvider(colorProvider: ColorProvider): androidx.glance.unit.ColorProvider {
        if (colorProvider.resId != 0) {
            return androidx.glance.unit.ColorProvider(resId = colorProvider.resId)
        }

        if (colorProvider.hasDarkColor()) {
            val day = if (colorProvider.hasColor()) colorProvider.color else colorProvider.darkColor
            return androidx.glance.color.ColorProvider(
                day = toGlanceColor(day),
                night = toGlanceColor(colorProvider.darkColor)
            )
        }

        if (colorProvider.hasColor()) {
            return androidx.glance.unit.ColorProvider(color = toGlanceColor(colorProvider.color))
        }

        return androidx.glance.unit.ColorProvider(android.R.color.transparent)
    }

    /**
     * Proto Color를 Compose Color로 변환 (필요한 경우)
     */
    fun toComposeColor(protoColor: ProtoColor): androidx.compose.ui.graphics.Color {
        return androidx.compose.ui.graphics.Color(protoColor.argb)
    }

    /**
     * ARGB Int를 Glance Color로 변환
     */
    fun argbToGlanceColor(argb: Int): Color {
        return Color(argb)
    }
}
