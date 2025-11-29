package com.example.dsl.glance.renderer.remoteviews

import android.content.Context
import android.widget.RemoteViews
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.glance.renderer.RemoteViewsBuilder
import com.example.dsl.glance.renderer.TextRenderer
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.WidgetNode

/**
 * TextRenderer의 RemoteViews 확장 함수
 */
fun TextRenderer.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: GlanceRenderer
): RemoteViews? {
    if (!node.hasText()) {
        return null
    }

    val textProperty = node.text
    val viewProperty = textProperty.viewProperty

    // 텍스트 내용
    val textContent = when {
        textProperty.text.text.isNotEmpty() -> textProperty.text.text
        textProperty.text.resId != 0 -> {
            context.resources.getString(textProperty.text.resId)
        }
        else -> ""
    }

    // RemoteViews 생성 (TextView)
    val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
    val textViewId = android.R.id.text1

    // 텍스트 설정
    remoteViews.setTextViewText(textViewId, textContent)

    // 텍스트 색상
    val textColor = ColorConverter.toGlanceColor(
        textProperty.fontColor,
        context
    )
    remoteViews.setTextColor(textViewId, textColor.value.toInt())

    // 텍스트 크기
    val textSize = textProperty.fontSize
    remoteViews.setTextViewTextSize(textViewId, android.util.TypedValue.COMPLEX_UNIT_SP, textSize)

    // 텍스트 정렬
    val gravity = when (textProperty.textAlign) {
        TextAlign.TEXT_ALIGN_START -> android.view.Gravity.START
        TextAlign.TEXT_ALIGN_CENTER -> android.view.Gravity.CENTER
        TextAlign.TEXT_ALIGN_END -> android.view.Gravity.END
        else -> android.view.Gravity.START
    }
    remoteViews.setInt(textViewId, "setGravity", gravity)

    // Font Weight (Typeface)
    val typeface = when (textProperty.fontWeight) {
        com.example.dsl.proto.FontWeight.FONT_WEIGHT_BOLD -> android.graphics.Typeface.DEFAULT_BOLD
        com.example.dsl.proto.FontWeight.FONT_WEIGHT_MEDIUM -> android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
        else -> android.graphics.Typeface.DEFAULT
    }
    remoteViews.setInt(textViewId, "setTypeface", typeface.style)

    // 최대 라인 수
    if (textProperty.maxLine > 0) {
        remoteViews.setInt(textViewId, "setMaxLines", textProperty.maxLine)
    }

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, textViewId, viewProperty, context)

    return remoteViews
}

