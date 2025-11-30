package com.example.dsl.glance.renderer.remoteviews

import android.content.Context
import android.widget.RemoteViews
import androidx.core.widget.RemoteViewsCompat.setProgressBarIndeterminate
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.renderer.ImageRenderer
import com.example.dsl.glance.renderer.RemoteViewsBuilder
import com.example.dsl.proto.WidgetNode

/**
 * ProgressRenderer의 RemoteViews 확장 함수
 */
fun ImageRenderer.renderToAnimationRemoteViews(
    node: WidgetNode,
    context: Context,
): RemoteViews? {
    val imageProperty = node.image
    val viewProperty = imageProperty.viewProperty

    val viewId = viewProperty.viewId
    val remoteViews = RemoteViews(
        context.packageName, imageProperty.provider.drawableResId,
        viewId
    )
    remoteViews.setProgressBarIndeterminate(viewId, imageProperty.infiniteLoop)
    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context)
    return remoteViews
}