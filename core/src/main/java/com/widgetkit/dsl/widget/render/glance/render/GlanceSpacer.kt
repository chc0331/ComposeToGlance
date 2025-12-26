package com.widgetkit.dsl.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.GlanceModifierBuilder

/**
 * Spacer 노드 렌더러
 */
internal object GlanceSpacer : RenderNode {
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
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
            .then(context.modifier)

        // Spacer는 빈 Box로 렌더링
        Spacer(modifier = modifier)
    }
}

