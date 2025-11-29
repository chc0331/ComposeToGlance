package com.example.dsl.glance.renderer

import android.content.res.ColorStateList
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressBackgroundTintList
import androidx.core.widget.RemoteViewsCompat.setProgressBarProgressTintList
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.LinearProgressIndicator
import com.example.dsl.R
import com.example.dsl.syntax.Color
import com.example.dsl.syntax.ColorProvider
import com.example.dsl.glance.GlanceModifierBuilder
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.RenderContext
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.proto.ProgressProperty
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.WidgetNode

/**
 * Progress 노드 렌더러
 */
object ProgressRenderer : NodeRenderer {
    @Composable
    override fun render(
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
        return when (progressProperty.progressType) {
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

        // Glance의 LinearProgressIndicator는 색상을 직접 지원하지 않을 수 있음
        // 기본 구현 사용
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
        val progress = if (progressProperty.maxValue > 0) {
            progressProperty.progressValue / progressProperty.maxValue
        } else {
            0f
        }
        AndroidRemoteViews(
            modifier = modifier,
            remoteViews = RemoteViews(
                LocalContext.current.packageName,
                R.layout.circular_progress_component
            ).apply {
                setProgressBar(
                    R.id.progress_bar,
                    100,
                    50,
                    false
                )
                setProgressBarProgressTintList(
                    R.id.progress_bar,
                    ColorStateList.valueOf(progressProperty.progressColor.color.argb)
                )
                setProgressBarProgressBackgroundTintList(
                    R.id.progress_bar,
                    ColorStateList.valueOf(progressProperty.backgroundColor.color.argb)
                )
            }
        )
    }
}

