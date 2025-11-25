package com.example.dsl.glance

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.dsl.glance.renderer.BoxRenderer
import com.example.dsl.glance.renderer.ButtonRenderer
import com.example.dsl.glance.renderer.ColumnRenderer
import com.example.dsl.glance.renderer.ImageRenderer
import com.example.dsl.glance.renderer.NodeRenderer
import com.example.dsl.glance.renderer.ProgressRenderer
import com.example.dsl.glance.renderer.RowRenderer
import com.example.dsl.glance.renderer.SpacerRenderer
import com.example.dsl.glance.renderer.TextRenderer
import com.example.dsl.proto.WidgetLayoutDocument
import com.example.dsl.proto.WidgetNode

/**
 * Proto WidgetLayoutDocument를 Jetpack Glance Composable로 렌더링하는 메인 렌더러
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
     */
    private fun getRenderer(node: WidgetNode): NodeRenderer? {
        return when {
            node.hasBox() -> BoxRenderer
            node.hasColumn() -> ColumnRenderer
            node.hasRow() -> RowRenderer
            node.hasText() -> TextRenderer
            node.hasImage() -> ImageRenderer
            node.hasButton() -> ButtonRenderer
            node.hasProgress() -> ProgressRenderer
            node.hasSpacer() -> SpacerRenderer
            else -> null
        }
    }
}

