package com.example.dsl.widget.renderer.strategy

import android.content.res.ColorStateList
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressBackgroundTintList
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressTintList
import androidx.glance.LocalContext
import androidx.glance.appwidget.LinearProgressIndicator
import com.example.dsl.R
import com.example.dsl.syntax.builder.Color
import com.example.dsl.syntax.builder.ColorProvider
import com.example.dsl.proto.ProgressProperty
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.converter.ColorConverter
import com.example.dsl.widget.renderer.RemoteViewsBuilder

/**
 * Progress 노드 렌더링 전략
 */
object ProgressRenderStrategy {
    /**
     * Glance 전략
     */
    object Glance : GlanceRenderStrategy() {
        @Composable
        override fun renderGlance(
            node: WidgetNode,
            context: RenderContext,
            renderer: GlanceRenderer
        ) {
            if (!node.hasProgress()) {
                androidx.glance.layout.Box {}
                return
            }

            val progressProperty = node.progress
            val viewProperty = progressProperty.viewProperty

            // Modifier 생성
            val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
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
            modifier: androidx.glance.GlanceModifier,
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
                color = ColorConverter.toGlanceColorProvider(ColorProvider(color = Color(progressColor.toArgb()))),
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
            modifier: androidx.glance.GlanceModifier,
            context: RenderContext
        ) {
            val progressColor = if (progressProperty.progressColor.resId != 0) {
                context.context.getColor(progressProperty.progressColor.resId)
            } else progressProperty.progressColor.color.argb
            val backgroundColor = if (progressProperty.backgroundColor.resId != 0) {
                context.context.getColor(progressProperty.backgroundColor.resId)
            } else progressProperty.backgroundColor.color.argb
            
            androidx.glance.appwidget.AndroidRemoteViews(
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

    /**
     * RemoteViews 전략
     */
    object RemoteViews : RemoteViewsRenderStrategy() {
        override fun createRemoteViews(
            node: WidgetNode,
            context: RenderContext
        ): android.widget.RemoteViews? {
            if (!node.hasProgress()) {
                return null
            }

            val progressProperty = node.progress
            val viewProperty = progressProperty.viewProperty

            // Progress 타입에 따라 렌더링
            return when (progressProperty.progressType) {
                ProgressType.PROGRESS_TYPE_LINEAR -> {
                    renderLinearProgressToRemoteViews(progressProperty, viewProperty, context.context)
                }
                ProgressType.PROGRESS_TYPE_CIRCULAR -> {
                    renderCircularProgressToRemoteViews(progressProperty, viewProperty, context.context)
                }
                else -> {
                    renderLinearProgressToRemoteViews(progressProperty, viewProperty, context.context)
                }
            }
        }

        private fun renderLinearProgressToRemoteViews(
            progressProperty: ProgressProperty,
            viewProperty: com.example.dsl.proto.ViewProperty,
            context: android.content.Context
        ): android.widget.RemoteViews {
            val remoteViews = android.widget.RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
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
            context: android.content.Context
        ): android.widget.RemoteViews {
            val viewId = viewProperty.viewId
            val remoteViews = android.widget.RemoteViews(context.packageName, R.layout.circular_progress_component, viewId)
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
    }
}

