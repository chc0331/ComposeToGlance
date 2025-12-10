package com.example.dsl.widget.renderer

import androidx.compose.runtime.Composable
import androidx.glance.layout.Row
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.converter.AlignmentConverter

/**
 * Row 노드 렌더러
 */
internal object RowRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasRow()) {
            androidx.glance.layout.Box {}
            return
        }

        val rowProperty = node.row
        val viewProperty = rowProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        // Horizontal Alignment
        val horizontalAlignment = AlignmentConverter.toGlanceHorizontalAlignment(
            rowProperty.horizontalAlignment
        )

        // Vertical Alignment
        val verticalAlignment = AlignmentConverter.toGlanceVerticalAlignment(
            rowProperty.verticalAlignment
        )

        // 자식 노드 렌더링
        val children = node.childrenList

        Row(
            modifier = modifier,
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        ) {
            // 자식 노드들을 재귀적으로 렌더링
            children.forEach { child -> renderer.renderNode(child, context) }
        }
    }
}

