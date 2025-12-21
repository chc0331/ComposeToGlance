package com.widgetkit.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.RenderContext
import com.widgetkit.dsl.widget.NodeRenderer
import com.widgetkit.dsl.widget.strategy.RenderStrategyFactory

/**
 * Image 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceImage : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        val strategy = RenderStrategyFactory.getImageStrategy(node)
        strategy.render(node, context, renderer)
    }
}