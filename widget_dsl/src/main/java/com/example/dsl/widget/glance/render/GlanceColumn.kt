package com.example.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.glance.GlanceModifierBuilder
import com.example.dsl.widget.WidgetRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.glance.converter.AlignmentConverter
import com.example.dsl.widget.NodeRenderer

/**
 * Column 노드 렌더러
 */
internal object GlanceColumn : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasColumn()) {
            Box {}
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

