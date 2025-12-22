package com.widgetkit.dsl.widget.node

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer

/**
 * 노드 렌더러 인터페이스
 * 각 노드 타입별로 구현
 */
internal interface RenderNode {
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
        renderer: WidgetRenderer
    )
}

/**
 * 렌더링 컨텍스트
 * 노드 렌더링 시 필요한 정보를 전달
 */
internal data class RenderContext(
    val context: Context,
    val modifier: GlanceModifier = GlanceModifier,
    val parentAlignment: Alignment? = null
)

