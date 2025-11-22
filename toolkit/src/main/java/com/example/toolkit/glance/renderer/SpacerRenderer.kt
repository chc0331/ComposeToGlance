package com.example.toolkit.glance.renderer

import androidx.compose.runtime.Composable
import androidx.glance.layout.Spacer
import com.example.composetoglance.proto.WidgetNode
import com.example.toolkit.glance.GlanceModifierBuilder
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.glance.RenderContext

/**
 * Spacer 노드 렌더러
 */
object SpacerRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasSpacer()) {
            androidx.glance.layout.Box {}
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

