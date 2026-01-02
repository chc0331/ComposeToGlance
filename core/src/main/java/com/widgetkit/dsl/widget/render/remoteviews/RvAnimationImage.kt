package com.widgetkit.dsl.widget.render.remoteviews

import android.content.res.ColorStateList
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.widget.RemoteViewsCompat.setProgressBarIndeterminate
import androidx.core.widget.RemoteViewsCompat.setProgressBarIndeterminateTintList
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressTintList
import androidx.core.widget.RemoteViewsCompat.setViewBackgroundTintList
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderNode

internal object RvAnimationImage : RenderNode {

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
        if (imageProperty.hasTintColor()) {
            val tintColor = imageProperty.tintColor.argb
            remoteViews.setProgressBarIndeterminateTintList(
                viewId,
                ColorStateList.valueOf(tintColor)
            )
        }
        // ViewProperty 속성 적용
        RemoteViewsBuilder.applyViewProperties(
            remoteViews,
            viewId,
            viewProperty,
            context.context,
            context.document.widgetMode
        )
        AndroidRemoteViews(
            remoteViews = remoteViews,
            modifier = GlanceModifier
        )
    }
}