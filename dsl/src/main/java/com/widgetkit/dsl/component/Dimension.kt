package com.widgetkit.dsl.component

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.Padding

// ==================== 편의 확장 함수 ===================

/**
 * Padding을 간단하게 생성하는 확장 함수
 * WidgetScope에서 사용할 수 있는 편의 함수
 */
fun WidgetScope.Padding(
    all: Float? = null,
    horizontal: Float? = null,
    vertical: Float? = null,
    start: Float = 0f,
    top: Float = 0f,
    end: Float = 0f,
    bottom: Float = 0f
): Padding {
    val finalStart = all ?: horizontal ?: start
    val finalTop = all ?: vertical ?: top
    val finalEnd = all ?: horizontal ?: end
    val finalBottom = all ?: vertical ?: bottom

    return Padding(
        start = finalStart,
        top = finalTop,
        end = finalEnd,
        bottom = finalBottom
    )
}