package com.widgetkit.dsl.widget.node.component

import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.WidgetType
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.render.GlanceImage
import com.widgetkit.dsl.widget.render.remoteviews.RvAnimationImage
import com.widgetkit.dsl.widget.render.remoteviews.RvImage

internal class ImageNode : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (node.widgetType == WidgetType.WIDGET_TYPE_GLANCE) {
            GlanceImage.render(node, context, renderer)
        } else if (node.image.animation) {
            RvAnimationImage.render(node, context, renderer)
        } else if ((node.image.viewProperty.partiallyUpdate)) {
            RvImage.render(node, context, renderer)
        }
    }
}