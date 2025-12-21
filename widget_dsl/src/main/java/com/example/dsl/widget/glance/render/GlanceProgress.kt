package com.example.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.WidgetRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.NodeRenderer
import com.example.dsl.widget.strategy.RenderStrategyFactory

/**
 * Progress 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceProgress : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        val strategy = RenderStrategyFactory.getProgressStrategy(node)
        strategy.render(node, context, renderer)
    }
}

