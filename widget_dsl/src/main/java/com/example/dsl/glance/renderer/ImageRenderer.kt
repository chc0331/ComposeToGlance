package com.example.dsl.glance.renderer

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.RenderContext
import com.example.dsl.glance.renderer.strategy.RenderStrategyFactory

/**
 * Image 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
object ImageRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        val strategy = RenderStrategyFactory.getImageStrategy(node)
        strategy.render(node, context, renderer)
    }
}