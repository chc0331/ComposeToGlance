package com.example.toolkit.glance.renderer

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.LinearProgressIndicator
import com.example.composetoglance.proto.ProgressProperty
import com.example.composetoglance.proto.ProgressType
import com.example.composetoglance.proto.WidgetNode
import com.example.toolkit.glance.GlanceModifierBuilder
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.glance.RenderContext
import com.example.toolkit.glance.converter.ColorConverter

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

        LinearProgressIndicator(progress = progress.coerceIn(0f, 1f), modifier = modifier)
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
//        CircularProgressIndicator(progress = progress.coerceIn(0f, 1f), modifier = modifier)
        CircularProgressIndicator(modifier = modifier)
    }
}

