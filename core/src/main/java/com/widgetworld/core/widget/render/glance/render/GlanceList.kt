package com.widgetworld.core.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import com.widgetworld.core.proto.WidgetMode
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.GlanceModifierBuilder
import com.widgetworld.core.widget.render.glance.converter.AlignmentConverter

internal object GlanceList : RenderNode {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasList()) {
            Box { }
            return
        }

        val listProperty = node.list
        val viewProperty = listProperty.viewProperty
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
            .then(context.modifier)
        val horizontalAlignment = AlignmentConverter.toGlanceHorizontalAlignment(
            listProperty.horizontalAlignment
        )
        val children = node.childrenList

        if (context.document.widgetMode == WidgetMode.WIDGET_MODE_PREVIEW) {
            Column(
                modifier = modifier,
                horizontalAlignment = horizontalAlignment
            ) {
                children.forEach { child ->
                    renderer.renderNode(child, context)
                }
            }
        } else {
            LazyColumn(
                modifier = modifier,
                horizontalAlignment = horizontalAlignment
            ) {
                children.forEach { child ->
                    item {
                        renderer.renderNode(child, context)
                    }
                }
            }
        }
    }
}