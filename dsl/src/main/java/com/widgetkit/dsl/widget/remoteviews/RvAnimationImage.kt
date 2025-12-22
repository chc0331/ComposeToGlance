package com.widgetkit.dsl.widget.remoteviews

import androidx.compose.runtime.Composable
import androidx.core.widget.RemoteViewsCompat.setProgressBarIndeterminate
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.NodeRenderer
import com.widgetkit.dsl.widget.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer

internal object RvAnimationImage : NodeRenderer {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasImage()) {
            return
        }

        val imageProperty = node.image
        val viewProperty = imageProperty.viewProperty

        val viewId = viewProperty.viewId
        val remoteViews = android.widget.RemoteViews(
            context.context.packageName,
            imageProperty.provider.drawableResId,
            viewId
        )
        remoteViews.setProgressBarIndeterminate(viewId, imageProperty.infiniteLoop)

        // ViewProperty 속성 적용
        RemoteViewsBuilder.applyViewProperties(
            remoteViews,
            viewId,
            viewProperty,
            context.context
        )
        AndroidRemoteViews(
            remoteViews = remoteViews,
            modifier = GlanceModifier
        )
    }
}