package com.example.dsl.widget.renderer.strategy

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext

/**
 * 렌더링 전략 인터페이스
 * Glance와 RemoteViews를 통합하는 추상화
 */
internal interface RenderStrategy {
    /**
     * WidgetNode를 렌더링
     * @param node WidgetNode
     * @param context RenderContext
     * @param renderer GlanceRenderer (자식 노드 렌더링용)
     * @return Glance Composable (Glance 컴포저블 또는 AndroidRemoteViews로 감싼 RemoteViews)
     */
    @Composable
    fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    )
}

