package com.example.dsl.widget.renderer

import androidx.compose.runtime.Composable
import androidx.glance.layout.Spacer
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext

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

