package com.widgetworld.core.widget.node.layout

import androidx.compose.runtime.Composable
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.render.GlanceList

internal class ListNode : RenderNode {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        GlanceList.render(node, context, renderer)
    }
}