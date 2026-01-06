package com.widgetworld.core.widget.node.component

import androidx.compose.runtime.Composable
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.WidgetType
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.render.GlanceText
import com.widgetworld.core.widget.render.remoteviews.RvText

internal class TextNode : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (node.text.viewProperty.partiallyUpdate) {
            RvText.render(node, context, renderer)
        } else {
            GlanceText.render(node, context, renderer)
        }
    }
}