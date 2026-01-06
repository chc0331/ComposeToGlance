package com.widgetworld.core.widget.render.remoteviews

import android.content.Context
import android.content.res.ColorStateList
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import com.widgetworld.core.R
import com.widgetworld.core.proto.ProgressProperty
import com.widgetworld.core.proto.ProgressType
import com.widgetworld.core.proto.ViewProperty
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderNode

internal object RvProgress : RenderNode {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasProgress()) {
            return
        }

        val progressProperty = node.progress
        val viewProperty = progressProperty.viewProperty

        // Progress 타입에 따라 렌더링
        val remoteViews = when (progressProperty.progressType) {
            ProgressType.PROGRESS_TYPE_LINEAR -> {
                renderLinearProgressToRemoteViews(
                    progressProperty,
                    viewProperty,
                    context.context,
                    context.document.widgetMode
                )
            }

            ProgressType.PROGRESS_TYPE_CIRCULAR -> {
                renderCircularProgressToRemoteViews(
                    progressProperty,
                    viewProperty,
                    context.context,
                    context.document.widgetMode
                )
            }

            else -> {
                renderLinearProgressToRemoteViews(
                    progressProperty,
                    viewProperty,
                    context.context,
                    context.document.widgetMode
                )
            }
        }
        AndroidRemoteViews(
            modifier = GlanceModifier,
            remoteViews = remoteViews
        )
    }

    private fun renderLinearProgressToRemoteViews(
        progressProperty: ProgressProperty,
        viewProperty: ViewProperty,
        context: Context,
        widgetMode: com.widgetworld.core.proto.WidgetMode
    ): android.widget.RemoteViews {
        val viewId = viewProperty.viewId
        // RemoteViews 생성 시 viewId를 전달하여 레이아웃의 ProgressBar ID를 viewId로 설정
        val remoteViews = RemoteViews(
            context.packageName,
            R.layout.linear_progress_component,
            viewId
        )

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

        RemoteViewsBuilder.applyViewProperties(
            remoteViews,
            viewId,
            viewProperty,
            context,
            widgetMode
        )

        return remoteViews
    }

    private fun renderCircularProgressToRemoteViews(
        progressProperty: ProgressProperty,
        viewProperty: ViewProperty,
        context: Context,
        widgetMode: com.widgetworld.core.proto.WidgetMode
    ): android.widget.RemoteViews {
        val viewId = viewProperty.viewId
        val remoteViews = RemoteViews(
            context.packageName,
            R.layout.circular_progress_component,
            viewId
        )
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
        RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context, widgetMode)

        return remoteViews
    }
}