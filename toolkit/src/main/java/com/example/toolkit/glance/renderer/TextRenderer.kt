package com.example.toolkit.glance.renderer


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.toolkit.proto.WidgetNode
import com.example.toolkit.glance.GlanceModifierBuilder
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.glance.RenderContext
import com.example.toolkit.glance.converter.ColorConverter
import com.example.toolkit.proto.TextAlign as ProtoTextAlign

/**
 * Text 노드 렌더러
 */
object TextRenderer : NodeRenderer {
    @Composable
    override fun render(
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
            fontWeight = toGlanceFontWeight(textProperty.fontWeight),
            textAlign = toGlanceTextAlign(textProperty.textAlign)
        )

        // 최대 라인 수 (Glance는 maxLines를 직접 지원하지 않으므로 제한적으로 처리)
        // 실제로는 Text 컴포넌트에 maxLines 파라미터가 없을 수 있음

        Text(
            text = textContent,
            modifier = modifier,
            style = textStyle
        )
    }

    private fun toGlanceFontWeight(protoWeight: com.example.toolkit.proto.FontWeight): FontWeight {
        return when (protoWeight) {
            com.example.toolkit.proto.FontWeight.FONT_WEIGHT_NORMAL -> FontWeight.Normal
            com.example.toolkit.proto.FontWeight.FONT_WEIGHT_MEDIUM -> FontWeight.Medium
            com.example.toolkit.proto.FontWeight.FONT_WEIGHT_BOLD -> FontWeight.Bold
            else -> FontWeight.Normal
        }
    }

    private fun toGlanceTextAlign(protoAlign: ProtoTextAlign): TextAlign {
        return when (protoAlign) {
            ProtoTextAlign.TEXT_ALIGN_START -> TextAlign.Start
            ProtoTextAlign.TEXT_ALIGN_CENTER -> TextAlign.Center
            ProtoTextAlign.TEXT_ALIGN_END -> TextAlign.End
            else -> TextAlign.Start
        }
    }
}

