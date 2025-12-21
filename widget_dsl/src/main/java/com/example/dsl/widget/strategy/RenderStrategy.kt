package com.example.dsl.widget.strategy

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.WidgetRenderer

/**
 * 렌더링 전략 인터페이스
 * Glance와 RemoteViews를 통합하는 추상화
 */
internal interface RenderStrategy {
    @Composable
    fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    )
}

