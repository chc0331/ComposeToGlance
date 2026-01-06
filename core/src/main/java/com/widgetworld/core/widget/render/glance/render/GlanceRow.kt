package com.widgetworld.core.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.GlanceModifierBuilder
import com.widgetworld.core.widget.render.glance.converter.AlignmentConverter

/**
 * Row 노드 렌더러
 */
internal object GlanceRow : RenderNode {
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
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
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

