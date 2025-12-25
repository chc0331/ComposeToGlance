package com.widgetkit.dsl.widget.node.component

import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.render.GlanceProgress
import com.widgetkit.dsl.widget.render.remoteviews.RvProgress

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