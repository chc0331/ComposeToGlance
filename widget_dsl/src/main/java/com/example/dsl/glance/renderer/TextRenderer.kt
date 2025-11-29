package com.example.dsl.glance.renderer


import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.dsl.proto.WidgetNode
import com.example.dsl.glance.GlanceModifierBuilder
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.RenderContext
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.glance.renderer.remoteviews.renderToRemoteViews
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.TextAlign

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
        if (node.text.viewProperty.partiallyUpdate) {
            createRemoteViews(node, context)?.let {
                AndroidRemoteViews(remoteViews = it)
            }
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

        // 최대 라인 수 (Glance는 maxLines를 직접 지원하지 않으므로 제한적으로 처리)
        // 실제로는 Text 컴포넌트에 maxLines 파라미터가 없을 수 있음

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

    private fun createRemoteViews(
        node: WidgetNode,
        context: RenderContext
    ): RemoteViews? {
        val remoteViews = renderToRemoteViews(node, context.context)
        return remoteViews
    }
}

