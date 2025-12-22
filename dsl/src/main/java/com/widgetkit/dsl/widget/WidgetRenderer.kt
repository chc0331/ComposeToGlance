package com.widgetkit.dsl.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import com.widgetkit.dsl.proto.WidgetLayoutDocument
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.rendernode.NodeRenderer
import com.widgetkit.dsl.widget.rendernode.NodeRendererRegistry
import com.widgetkit.dsl.widget.rendernode.RenderContext

class WidgetRenderer(private val context: Context) {

    @Composable
    fun render(document: WidgetLayoutDocument) {
        if (!document.hasRoot()) {
            return Box {}
        }

        val rootContext = RenderContext(context = context)
        renderNode(document.root, rootContext)
    }

    @Composable
    internal fun renderNode(node: WidgetNode, context: RenderContext) {
        val renderer = getRenderer(node) ?: return Box {}
        return renderer.render(node, context, this)
    }

    private fun getRenderer(node: WidgetNode): NodeRenderer? {
        return NodeRendererRegistry.getRendererForNode(node)
    }
}