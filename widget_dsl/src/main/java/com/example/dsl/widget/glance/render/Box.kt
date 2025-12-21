package com.example.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.glance.GlanceModifierBuilder
import com.example.dsl.widget.WidgetRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.glance.converter.AlignmentConverter
import com.example.dsl.widget.NodeRenderer

/**
 * Box 노드 렌더러
 */
internal object Box : NodeRenderer {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasBox()) {
            Box {}
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

        Box(
            modifier = modifier,
            contentAlignment = alignment
        ) {
            // 자식 노드들을 재귀적으로 렌더링
            children.forEach { child -> renderer.renderNode(child, context) }
        }
    }
}

