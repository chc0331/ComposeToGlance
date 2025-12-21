package com.widgetkit.dsl.widget.strategy

import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Box
import androidx.glance.layout.wrapContentSize
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer

/**
 * RemoteViews 기반 렌더링 전략 추상 클래스
 * RemoteViews를 생성하고 AndroidRemoteViews로 감싸서 반환
 */
internal abstract class RemoteViewsRenderStrategy : RenderStrategy {
    /**
     * RemoteViews 객체를 생성
     * 하위 클래스에서 구현
     * 
     * @param node WidgetNode
     * @param context RenderContext
     * @return 생성된 RemoteViews 또는 null
     */
    protected abstract fun createRemoteViews(
        node: WidgetNode,
        context: RenderContext
    ): RemoteViews?

    /**
     * AndroidRemoteViews로 감싸서 반환
     * partiallyUpdate가 true일 때 사용되는 렌더링 방식
     */
    @Composable
    final override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        createRemoteViews(node, context)?.let { remoteViews ->
            AndroidRemoteViews(
                modifier = GlanceModifier.wrapContentSize(),
                remoteViews = remoteViews
            )
        } ?: run {
            // RemoteViews 생성 실패 시 빈 Box 반환
            Box {}
        }
    }
}

