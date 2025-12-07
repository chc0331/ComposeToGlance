package com.example.dsl.glance.renderer.remoteviews

import android.content.Context
import android.content.res.ColorStateList
import android.widget.RemoteViews
import com.example.dsl.R
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.glance.renderer.ProgressRenderer
import com.example.dsl.glance.renderer.RemoteViewsBuilder
import com.example.dsl.proto.ProgressProperty
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.WidgetNode

/**
 * ProgressRenderer의 RemoteViews 확장 함수
 */
fun ProgressRenderer.renderToRemoteViews(
    node: WidgetNode,
    context: Context
): RemoteViews? {
    if (!node.hasProgress()) {
        return null
    }

    val progressProperty = node.progress
    val viewProperty = progressProperty.viewProperty

    // Progress 타입에 따라 렌더링
    return when (progressProperty.progressType) {
        ProgressType.PROGRESS_TYPE_LINEAR -> {
            renderLinearProgressToRemoteViews(progressProperty, viewProperty, context)
        }
        ProgressType.PROGRESS_TYPE_CIRCULAR -> {
            renderCircularProgressToRemoteViews(progressProperty, viewProperty, context)
        }
        else -> {
            renderLinearProgressToRemoteViews(progressProperty, viewProperty, context)
        }
    }
}

private fun renderLinearProgressToRemoteViews(
    progressProperty: ProgressProperty,
    viewProperty: com.example.dsl.proto.ViewProperty,
    context: Context
): RemoteViews {
    // progress_horizontal 레이아웃이 없으므로 simple_list_item_1 사용
    val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
    val progressBarId = android.R.id.text1

    val max = progressProperty.maxValue.toInt()
    val progress = progressProperty.progressValue.toInt()

    remoteViews.setProgressBar(progressBarId, max, progress, false)

    // Progress color
    val progressColor = ColorConverter.toGlanceColor(
        progressProperty.progressColor,
        context
    )
    remoteViews.setInt(progressBarId, "setProgressTint", progressColor.value.toInt())

    // Background color
    val backgroundColor = ColorConverter.toGlanceColor(
        progressProperty.backgroundColor,
        context
    )
    remoteViews.setInt(progressBarId, "setProgressBackgroundTint", backgroundColor.value.toInt())

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, progressBarId, viewProperty, context)

    return remoteViews
}

private fun renderCircularProgressToRemoteViews(
    progressProperty: ProgressProperty,
    viewProperty: com.example.dsl.proto.ViewProperty,
    context: Context
): RemoteViews {
    // progress_horizontal 레이아웃이 없으므로 simple_list_item_1 사용
    val viewId = viewProperty.viewId
    val remoteViews = RemoteViews(context.packageName, R.layout.circular_progress_component, viewId)
    val max = progressProperty.maxValue.toInt()
    val progress = progressProperty.progressValue.toInt()

    remoteViews.setProgressBar(viewId, max, progress, false)

    val progressColor = if (progressProperty.progressColor.resId != 0) {
        context.getColor(progressProperty.progressColor.resId)
    } else progressProperty.progressColor.color.argb
    val backgroundColor = if (progressProperty.backgroundColor.resId != 0) {
        context.getColor(progressProperty.backgroundColor.resId)
    } else progressProperty.backgroundColor.color.argb

    remoteViews.setColorStateList(
        viewId,
        "setProgressTintList",
        ColorStateList.valueOf(progressColor)
    )
    remoteViews.setColorStateList(
        viewId, "setProgressBackgroundTintList",
        ColorStateList.valueOf(backgroundColor)
    )
    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context)

    return remoteViews
}

