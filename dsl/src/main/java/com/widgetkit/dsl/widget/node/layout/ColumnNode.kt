package com.widgetkit.dsl.widget.node.layout

import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.WidgetType
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.render.GlanceColumn

internal class ColumnNode : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (node.widgetType == WidgetType.WIDGET_TYPE_GLANCE) {
            GlanceColumn.render(node, context, renderer)
        } else {

        }
    }
}