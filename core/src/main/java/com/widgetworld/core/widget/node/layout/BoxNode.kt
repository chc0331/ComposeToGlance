package com.widgetworld.core.widget.node.layout

import androidx.compose.runtime.Composable
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.WidgetType
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.render.GlanceBox

internal class BoxNode : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (node.widgetType == WidgetType.WIDGET_TYPE_GLANCE) {
            GlanceBox.render(node, context, renderer)
        } else {

        }
    }
}