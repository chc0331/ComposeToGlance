package com.example.dsl.glance.renderer.remoteviews

import android.R.attr.typeface
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.RemoteViews
import com.example.dsl.R
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.glance.renderer.RemoteViewsBuilder
import com.example.dsl.glance.renderer.TextRenderer
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextProperty
import com.example.dsl.proto.WidgetNode

/**
 * TextRenderer의 RemoteViews 확장 함수
 */
fun TextRenderer.renderToRemoteViews(
    node: WidgetNode,
    context: Context
): RemoteViews? {
    if (!node.hasText()) {
        return null
    }
    with(node.text) {
        // 텍스트 내용
        val textContent = when {
            text.text.isNotEmpty() -> text.text
            text.resId != 0 -> {
                context.resources.getString(text.resId)
            }

            else -> ""
        }
        // RemoteViews 생성 (TextView)
        val viewId = viewProperty.viewId
        val remoteViews =
            RemoteViews(context.packageName, R.layout.text_component, viewId)
        remoteViews.setTextViewText(viewId, textContent)
        remoteViews.setTextColor(
            viewId,
            ColorConverter.toGlanceColor(fontColor, context).value.toInt()
        )
        remoteViews.setTextViewTextSize(viewId, TypedValue.COMPLEX_UNIT_SP, fontSize)
        // 텍스트 정렬
        val gravity = when (textAlign) {
            TextAlign.TEXT_ALIGN_START -> android.view.Gravity.START
            TextAlign.TEXT_ALIGN_CENTER -> android.view.Gravity.CENTER
            TextAlign.TEXT_ALIGN_END -> android.view.Gravity.END
            else -> android.view.Gravity.START
        }
        remoteViews.setInt(viewId, "setGravity", gravity)
        // Font Weight (Typeface)
        val typeface = when (fontWeight) {
            FontWeight.FONT_WEIGHT_BOLD -> Typeface.DEFAULT_BOLD
            FontWeight.FONT_WEIGHT_MEDIUM -> Typeface.create(
                Typeface.DEFAULT, Typeface.NORMAL
            )

            else -> Typeface.DEFAULT
        }
        remoteViews.setInt(viewId, "setTypeface", typeface.style)
        if (maxLine > 0) {
            remoteViews.setInt(viewId, "setMaxLines", maxLine)
        }
        RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context)
        return remoteViews
    }
    return null
}

