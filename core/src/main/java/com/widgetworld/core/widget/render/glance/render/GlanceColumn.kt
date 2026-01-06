package com.widgetworld.core.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.GlanceModifierBuilder
import com.widgetworld.core.widget.render.glance.converter.AlignmentConverter

/**
 * Column 노드 렌더러
 */
internal object GlanceColumn : RenderNode {
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
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
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

