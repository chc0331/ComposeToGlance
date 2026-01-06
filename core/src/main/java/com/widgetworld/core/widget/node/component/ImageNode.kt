package com.widgetworld.core.widget.node.component

import androidx.compose.runtime.Composable
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.WidgetType
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.render.GlanceImage
import com.widgetworld.core.widget.render.remoteviews.RvAnimationImage
import com.widgetworld.core.widget.render.remoteviews.RvImage

internal class ImageNode : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (node.image.animation) {
            RvAnimationImage.render(node, context, renderer)
        } else if ((node.image.viewProperty.partiallyUpdate)) {
            RvImage.render(node, context, renderer)
        } else {
            GlanceImage.render(node, context, renderer)
        }
    }
}