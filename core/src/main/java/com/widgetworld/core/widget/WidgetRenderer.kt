package com.widgetworld.core.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import com.widgetworld.core.proto.WidgetLayoutDocument
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.node.RenderNodeRegistry
import com.widgetworld.core.widget.node.RenderContext

class WidgetRenderer(private val context: Context) {

    @Composable
    fun render(document: WidgetLayoutDocument) {
        if (!document.hasRoot()) {
            return Box {}
        }

        val rootContext = RenderContext(context = context, document = document)
        renderNode(document.root, rootContext)
    }

    @Composable
    internal fun renderNode(node: WidgetNode, context: RenderContext) {
        val renderer = getRenderer(node) ?: return Box {}
        return renderer.render(node, context, this)
    }

    private fun getRenderer(node: WidgetNode): RenderNode? {
        return RenderNodeRegistry.getRendererForNode(node)
    }
}