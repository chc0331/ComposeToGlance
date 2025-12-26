package com.widgetkit.dsl.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import com.widgetkit.dsl.proto.WidgetLayoutDocument
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.node.RenderNodeRegistry
import com.widgetkit.dsl.widget.node.RenderContext

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