package com.example.dsl.widget.renderer.remoteviews

import android.content.Context
import android.widget.RemoteViews
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.converter.ColorConverter
import com.example.dsl.widget.renderer.ButtonRenderer
import com.example.dsl.widget.renderer.RemoteViewsBuilder
import com.example.dsl.proto.WidgetNode

/**
 * ButtonRenderer의 RemoteViews 확장 함수
 */
fun ButtonRenderer.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: GlanceRenderer
): RemoteViews? {
    if (!node.hasButton()) {
        return null
    }

    val buttonProperty = node.button
    val viewProperty = buttonProperty.viewProperty

    // 텍스트 내용
    val textContent = when {
        buttonProperty.text.text.isNotEmpty() -> buttonProperty.text.text
        buttonProperty.text.resId != 0 -> {
            context.resources.getString(buttonProperty.text.resId)
        }
        else -> ""
    }

    // RemoteViews 생성 (Button)
    val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
    val buttonId = android.R.id.text1

    // 텍스트 설정
    remoteViews.setTextViewText(buttonId, textContent)

    // 텍스트 색상
    val textColor = ColorConverter.toGlanceColor(
        buttonProperty.fontColor,
        context
    )
    remoteViews.setTextColor(buttonId, textColor.value.toInt())

    // 텍스트 크기
    remoteViews.setTextViewTextSize(buttonId, android.util.TypedValue.COMPLEX_UNIT_SP, buttonProperty.fontSize)

    // Font Weight
    val typeface = when (buttonProperty.fontWeight) {
        com.example.dsl.proto.FontWeight.FONT_WEIGHT_BOLD -> android.graphics.Typeface.DEFAULT_BOLD
        com.example.dsl.proto.FontWeight.FONT_WEIGHT_MEDIUM -> android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
        else -> android.graphics.Typeface.DEFAULT
    }
    remoteViews.setInt(buttonId, "setTypeface", typeface.style)

    // 배경 색상
    if (buttonProperty.hasBackgroundColor()) {
        val backgroundColor = ColorConverter.toGlanceColor(
            buttonProperty.backgroundColor,
            context
        )
        remoteViews.setInt(buttonId, "setBackgroundColor", backgroundColor.value.toInt())
    }

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, buttonId, viewProperty, context)

    return remoteViews
}

