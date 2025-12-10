package com.example.dsl.widget.renderer

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.renderer.strategy.RenderStrategyFactory

/**
 * Text 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
object TextRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        val strategy = RenderStrategyFactory.getTextStrategy(node)
        strategy.render(node, context, renderer)
    }
}

