package com.widgetkit.dsl.widget.render.glance.render

import android.content.res.ColorStateList
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressBackgroundTintList
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressTintList
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.layout.Box
import com.widgetkit.dsl.R
import com.widgetkit.dsl.proto.ProgressProperty
import com.widgetkit.dsl.proto.ProgressType
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.util.Color
import com.widgetkit.dsl.proto.util.ColorProvider
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.render.glance.converter.ColorConverter

/**
 * Progress 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceProgress : RenderNode {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasProgress()) {
            Box {}
            return
        }

        val progressProperty = node.progress
        val viewProperty = progressProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
            .then(context.modifier)

        // Progress 타입에 따라 렌더링
        when (progressProperty.progressType) {
            ProgressType.PROGRESS_TYPE_LINEAR -> {
                renderLinearProgress(progressProperty, modifier, context)
            }

            ProgressType.PROGRESS_TYPE_CIRCULAR -> {
                renderCircularProgress(progressProperty, modifier, context)
            }

            else -> {
                renderLinearProgress(progressProperty, modifier, context)
            }
        }
    }

    @Composable
    private fun renderLinearProgress(
        progressProperty: ProgressProperty,
        modifier: GlanceModifier,
        context: RenderContext
    ) {
        val progressColor = ColorConverter.toGlanceColor(
            progressProperty.progressColor,
            context.context
        )

        val backgroundColor = ColorConverter.toGlanceColor(
            progressProperty.backgroundColor,
            context.context
        )

        val progress = if (progressProperty.maxValue > 0) {
            progressProperty.progressValue / progressProperty.maxValue
        } else {
            0f
        }

        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = modifier,
            color = ColorConverter.toGlanceColorProvider(
                ColorProvider(
                    color = Color(
                        progressColor.toArgb()
                    )
                )
            ),
            backgroundColor = ColorConverter.toGlanceColorProvider(
                ColorProvider(
                    color = Color(
                        backgroundColor.toArgb()
                    )
                )
            )
        )
    }

    @Composable
    private fun renderCircularProgress(
        progressProperty: ProgressProperty,
        modifier: GlanceModifier,
        context: RenderContext
    ) {
        val progressColor = if (progressProperty.progressColor.resId != 0) {
            context.context.getColor(progressProperty.progressColor.resId)
        } else progressProperty.progressColor.color.argb
        val backgroundColor = if (progressProperty.backgroundColor.resId != 0) {
            context.context.getColor(progressProperty.backgroundColor.resId)
        } else progressProperty.backgroundColor.color.argb

        AndroidRemoteViews(
            modifier = modifier,
            remoteViews = RemoteViews(
                LocalContext.current.packageName,
                R.layout.circular_progress_component
            ).apply {
                setProgressBar(
                    R.id.progress_bar,
                    progressProperty.maxValue.toInt(),
                    progressProperty.progressValue.toInt(),
                    false
                )
                setProgressBarProgressTintList(
                    R.id.progress_bar,
                    ColorStateList.valueOf(progressColor)
                )
                setProgressBarProgressBackgroundTintList(
                    R.id.progress_bar,
                    ColorStateList.valueOf(backgroundColor)
                )
            }
        )
    }
}

