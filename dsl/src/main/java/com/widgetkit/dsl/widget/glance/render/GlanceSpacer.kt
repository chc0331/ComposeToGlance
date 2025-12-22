package com.widgetkit.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.rendernode.RenderContext
import com.widgetkit.dsl.widget.rendernode.NodeRenderer

/**
 * Spacer 노드 렌더러
 */
internal object GlanceSpacer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasSpacer()) {
            Box {}
            return
        }

        val spacerProperty = node.spacer
        val viewProperty = spacerProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        // Spacer는 빈 Box로 렌더링
        Spacer(modifier = modifier)
    }
}

