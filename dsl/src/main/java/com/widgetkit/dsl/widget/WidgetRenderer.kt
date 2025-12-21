package com.widgetkit.dsl.widget

import android.content.Context
import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetLayoutDocument
import com.widgetkit.dsl.proto.WidgetNode

class WidgetRenderer(private val context: Context) {

    @Composable
    fun render(document: WidgetLayoutDocument) {
        if (!document.hasRoot()) {
            return androidx.glance.layout.Box {}
        }

        val rootContext = RenderContext(context = context)
        renderNode(document.root, rootContext)
    }

    @Composable
    internal fun renderNode(node: WidgetNode, context: RenderContext) {
        val renderer = getRenderer(node) ?: return androidx.glance.layout.Box {}
        return renderer.render(node, context, this)
    }

    private fun getRenderer(node: WidgetNode): NodeRenderer? {
        return NodeRendererRegistry.getRendererForNode(node)
    }
}

