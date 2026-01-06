package com.widgetworld.core.widget.node.component

import androidx.compose.runtime.Composable
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.render.GlanceProgress
import com.widgetworld.core.widget.render.remoteviews.RvProgress

internal class ProgressNode : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (node.progress.viewProperty.partiallyUpdate) {
            RvProgress.render(node, context, renderer)
        } else {
            GlanceProgress.render(node, context, renderer)
        }
    }
}