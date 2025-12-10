package com.example.dsl.widget.renderer

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.converter.AlignmentConverter

/**
 * Box 노드 렌더러
 */
internal object BoxRenderer : NodeRenderer {

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

