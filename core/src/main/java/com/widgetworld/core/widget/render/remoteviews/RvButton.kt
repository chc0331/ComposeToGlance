package com.widgetworld.core.widget.render.remoteviews

import android.R
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.RemoteViews
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.render.glance.converter.ColorConverter
import com.widgetworld.core.widget.render.glance.render.GlanceButton

/**
 * ButtonRenderer의 RemoteViews 확장 함수
 */
internal fun GlanceButton.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: WidgetRenderer
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
    val remoteViews = RemoteViews(context.packageName, R.layout.simple_list_item_1)
    val buttonId = R.id.text1

    // 텍스트 설정
    remoteViews.setTextViewText(buttonId, textContent)

    // 텍스트 색상
    val textColor = ColorConverter.toGlanceColor(
        buttonProperty.fontColor,
        context
    )
    remoteViews.setTextColor(buttonId, textColor.value.toInt())

    // 텍스트 크기
    remoteViews.setTextViewTextSize(buttonId, TypedValue.COMPLEX_UNIT_SP, buttonProperty.fontSize)

    // Font Weight
    val typeface = when (buttonProperty.fontWeight) {
        FontWeight.FONT_WEIGHT_BOLD -> Typeface.DEFAULT_BOLD
        FontWeight.FONT_WEIGHT_MEDIUM -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        else -> Typeface.DEFAULT
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

