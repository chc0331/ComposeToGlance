package com.example.dsl.widget

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.dsl.widget.renderer.NodeRenderer
import com.example.dsl.widget.renderer.NodeRendererRegistry
import com.example.dsl.proto.WidgetLayoutDocument
import com.example.dsl.proto.WidgetNode

/**
 * Proto WidgetLayoutDocument를 Jetpack Glance Composable로 렌더링하는 메인 렌더러
 * 
 * 이 클래스는 NodeRendererRegistry를 사용하여 등록된 Renderer를 조회합니다.
 * 새로운 컴포넌트를 추가할 때는 NodeRendererRegistry에 해당 Renderer를 등록하면 됩니다.
 */
class GlanceRenderer(private val context: Context) {
    /**
     * WidgetLayoutDocument를 Glance Composable로 렌더링
     * @param document WidgetLayoutDocument
     * @return GlanceComposable
     */
    @Composable
    fun render(document: WidgetLayoutDocument) {
        if (!document.hasRoot()) {
            return androidx.glance.layout.Box {}
        }

        val rootContext = RenderContext(context = context)
        renderNode(document.root, rootContext)
    }

    /**
     * WidgetNode를 적절한 렌더러를 사용하여 Glance Composable로 렌더링
     * @param node WidgetNode
     * @param context RenderContext
     * @return GlanceComposable
     */
    @Composable
    internal fun renderNode(node: WidgetNode, context: RenderContext) {
        val renderer = getRenderer(node) ?: return androidx.glance.layout.Box {}
        return renderer.render(node, context, this)
    }

    /**
     * 노드 타입에 따라 적절한 렌더러 반환
     * NodeRendererRegistry를 사용하여 등록된 Renderer를 조회합니다.
     * 
     * @param node WidgetNode
     * @return 해당 노드 타입의 NodeRenderer 또는 null
     */
    private fun getRenderer(node: WidgetNode): NodeRenderer? {
        return NodeRendererRegistry.getRendererForNode(node)
    }
}

