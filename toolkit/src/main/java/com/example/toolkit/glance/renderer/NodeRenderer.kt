package com.example.toolkit.glance.renderer

import androidx.compose.runtime.Composable
import com.example.toolkit.proto.WidgetNode
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.glance.RenderContext

/**
 * 노드 렌더러 인터페이스
 * 각 노드 타입별로 구현
 */
interface NodeRenderer {
    /**
     * WidgetNode를 Glance Composable로 렌더링
     * @param node WidgetNode
     * @param context RenderContext
     * @param renderer GlanceRenderer (자식 노드 렌더링용)
     * @return GlanceComposable
     */
    @Composable
    fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    )
}

