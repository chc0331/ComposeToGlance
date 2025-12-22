package com.widgetkit.dsl.widget.remoteviews

import android.util.TypedValue
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import com.widgetkit.dsl.R
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.rendernode.NodeRenderer
import com.widgetkit.dsl.widget.rendernode.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.glance.converter.ColorConverter

internal object RvText : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasText()) {
            return
        }

        with(node.text) {
            // 텍스트 내용
            val textContent = when {
                text.text.isNotEmpty() -> text.text
                text.resId != 0 -> {
                    context.context.resources.getString(text.resId)
                }

                else -> ""
            }

            // RemoteViews 생성 (TextView)
            val viewId = viewProperty.viewId
            val remoteViews =
                android.widget.RemoteViews(
                    context.context.packageName,
                    R.layout.text_component,
                    viewId
                )
            remoteViews.setTextViewText(viewId, textContent)
            remoteViews.setTextColor(
                viewId,
                ColorConverter.toGlanceColor(fontColor, context.context).toArgb()
            )
            remoteViews.setTextViewTextSize(viewId, TypedValue.COMPLEX_UNIT_SP, fontSize)

            // 텍스트 정렬
            val gravity = when (textAlign) {
                TextAlign.TEXT_ALIGN_START -> Gravity.START
                TextAlign.TEXT_ALIGN_CENTER -> Gravity.CENTER
                TextAlign.TEXT_ALIGN_END -> Gravity.END
                else -> Gravity.START
            }
            remoteViews.setInt(viewId, "setGravity", gravity)

            if (maxLine > 0) {
                remoteViews.setInt(viewId, "setMaxLines", maxLine)
            }

            RemoteViewsBuilder.applyViewProperties(
                remoteViews,
                viewId,
                viewProperty,
                context.context
            )

            AndroidRemoteViews(
                remoteViews = remoteViews,
                modifier = GlanceModifier
            )
        }
    }
}