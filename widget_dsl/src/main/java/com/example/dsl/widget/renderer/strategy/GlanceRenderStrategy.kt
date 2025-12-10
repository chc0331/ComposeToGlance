package com.example.dsl.widget.renderer.strategy

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext

/**
 * Glance 기반 렌더링 전략 추상 클래스
 * 순수 Glance 컴포저블을 사용하는 렌더링 전략의 기본 구현
 */
abstract class GlanceRenderStrategy : RenderStrategy {
    /**
     * Glance 컴포저블로 렌더링
     * 하위 클래스에서 구현
     */
    @Composable
    protected abstract fun renderGlance(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    )

    @Composable
    final override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        renderGlance(node, context, renderer)
    }
}

