package com.widgetkit.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.rendernode.RenderContext
import com.widgetkit.dsl.widget.glance.converter.AlignmentConverter
import com.widgetkit.dsl.widget.rendernode.NodeRenderer

/**
 * Row 노드 렌더러
 */
internal object GlanceRow : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasRow()) {
            Box {}
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

