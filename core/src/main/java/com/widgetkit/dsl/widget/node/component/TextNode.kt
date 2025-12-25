package com.widgetkit.dsl.widget.node.component

import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.WidgetType
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.render.GlanceText
import com.widgetkit.dsl.widget.render.remoteviews.RvText

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