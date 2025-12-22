package com.widgetkit.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.layout.Box
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.rendernode.NodeRenderer
import com.widgetkit.dsl.widget.rendernode.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.glance.converter.ColorConverter

/**
 * Text 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceText : NodeRenderer {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasText()) {
            Box {}
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
            color = ColorProvider(textColor),
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

