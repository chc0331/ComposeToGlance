package com.example.dsl.widget.renderer.strategy

import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.dsl.R
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.converter.ColorConverter
import com.example.dsl.widget.renderer.RemoteViewsBuilder

/**
 * Text 노드 렌더링 전략
 */
object TextRenderStrategy {
    /**
     * Glance 전략
     */
    object Glance : GlanceRenderStrategy() {
        @Composable
        override fun renderGlance(
            node: WidgetNode,
            context: RenderContext,
            renderer: GlanceRenderer
        ) {
            if (!node.hasText()) {
                androidx.glance.layout.Box {}
                return
            }

            val textProperty = node.text
            val viewProperty = textProperty.viewProperty

            // Modifier 생성
            val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
                .then(context.modifier)

            // 텍스트 내용
            val textContent = when {
                textProperty.text.text.isNotEmpty() -> textProperty.text.text
                textProperty.text.resId != 0 -> {
                    context.context.resources.getString(textProperty.text.resId)
                }
                else -> ""
            }

            // 색상
            val textColor = ColorConverter.toGlanceColor(
                textProperty.fontColor,
                context.context
            )

            // 텍스트 스타일
            val textStyle = TextStyle(
                color = androidx.glance.unit.ColorProvider(textColor),
                fontSize = textProperty.fontSize.sp,
                fontWeight = textProperty.fontWeight.toGlanceFontWeight(),
                textAlign = textProperty.textAlign.toGlanceTextAlign()
            )

            Text(
                text = textContent,
                modifier = modifier,
                style = textStyle
            )
        }

        private fun FontWeight.toGlanceFontWeight(): androidx.glance.text.FontWeight {
            return when (this) {
                FontWeight.FONT_WEIGHT_NORMAL -> androidx.glance.text.FontWeight.Normal
                FontWeight.FONT_WEIGHT_MEDIUM -> androidx.glance.text.FontWeight.Medium
                FontWeight.FONT_WEIGHT_BOLD -> androidx.glance.text.FontWeight.Bold
                else -> androidx.glance.text.FontWeight.Normal
            }
        }

        private fun TextAlign.toGlanceTextAlign(): androidx.glance.text.TextAlign {
            return when (this) {
                TextAlign.TEXT_ALIGN_START -> androidx.glance.text.TextAlign.Start
                TextAlign.TEXT_ALIGN_CENTER -> androidx.glance.text.TextAlign.Center
                TextAlign.TEXT_ALIGN_END -> androidx.glance.text.TextAlign.End
                else -> androidx.glance.text.TextAlign.Start
            }
        }
    }

    /**
     * RemoteViews 전략
     */
    object RemoteViews : RemoteViewsRenderStrategy() {
        override fun createRemoteViews(
            node: WidgetNode,
            context: RenderContext
        ): android.widget.RemoteViews? {
            if (!node.hasText()) {
                return null
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
                    android.widget.RemoteViews(context.context.packageName, R.layout.text_component, viewId)
                remoteViews.setTextViewText(viewId, textContent)
                remoteViews.setTextColor(
                    viewId,
                    ColorConverter.toGlanceColor(fontColor, context.context).toArgb()
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
                
                if (maxLine > 0) {
                    remoteViews.setInt(viewId, "setMaxLines", maxLine)
                }
                
                RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context.context)
                return remoteViews
            }
        }
    }
}

