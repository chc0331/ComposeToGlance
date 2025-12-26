package com.widgetkit.dsl.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.text.Text
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.GlanceModifierBuilder

/**
 * Text 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceText : RenderNode {

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
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
            .then(context.modifier)

        val (textContent, textStyle) = TextRenderUtils.buildTextAndStyle(
            textProperty = textProperty,
            context = context.context
        )

        Text(
            text = textContent,
            modifier = modifier,
            style = textStyle
        )
    }

}

