package com.widgetkit.dsl.widget.strategy

import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer

/**
 * Glance 기반 렌더링 전략 추상 클래스
 * 순수 Glance 컴포저블을 사용하는 렌더링 전략의 기본 구현
 */
internal abstract class GlanceRenderStrategy : RenderStrategy {
    /**
     * Glance 컴포저블로 렌더링
     * 하위 클래스에서 구현
     */
    @Composable
    protected abstract fun renderGlance(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    )

    @Composable
    final override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        renderGlance(node, context, renderer)
    }
}

