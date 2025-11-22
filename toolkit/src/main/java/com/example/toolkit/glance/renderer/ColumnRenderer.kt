package com.example.toolkit.glance.renderer

import androidx.compose.runtime.Composable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import com.example.composetoglance.proto.ColumnLayoutProperty
import com.example.composetoglance.proto.WidgetNode
import com.example.toolkit.glance.GlanceModifierBuilder
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.glance.RenderContext
import com.example.toolkit.glance.converter.AlignmentConverter

/**
 * Column 노드 렌더러
 */
object ColumnRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasColumn()) {
            androidx.glance.layout.Box {}
            return
        }

        val columnProperty = node.column
        val viewProperty = columnProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        // Horizontal Alignment
        val horizontalAlignment = AlignmentConverter.toGlanceHorizontalAlignment(
            columnProperty.horizontalAlignment
        )

        // Vertical Alignment
        val verticalAlignment = AlignmentConverter.toGlanceVerticalAlignment(
            columnProperty.verticalAlignment
        )

        // 자식 노드 렌더링
        val children = node.childrenList

        Column(
            modifier = modifier,
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment
        ) {
            // 자식 노드들을 재귀적으로 렌더링
            children.forEach { child -> renderer.renderNode(child, context) }
        }
    }
}

