package com.example.toolkit.glance.renderer

import androidx.compose.runtime.Composable
import com.example.toolkit.proto.WidgetNode
import com.example.toolkit.glance.GlanceModifierBuilder
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.glance.RenderContext
import com.example.toolkit.glance.converter.AlignmentConverter

/**
 * Box 노드 렌더러
 */
object BoxRenderer : NodeRenderer {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasBox()) {
            androidx.glance.layout.Box {}
            return
        }

        val boxProperty = node.box
        val viewProperty = boxProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        // Alignment
        val alignment = AlignmentConverter.toGlanceAlignment(boxProperty.contentAlignment)

        // 자식 노드 렌더링
        val children = node.childrenList

        androidx.glance.layout.Box(
            modifier = modifier,
            contentAlignment = alignment
        ) {
            // 자식 노드들을 재귀적으로 렌더링
            children.forEach { child -> renderer.renderNode(child, context) }
        }
    }
}

