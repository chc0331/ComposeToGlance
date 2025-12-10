package com.example.dsl.widget.renderer.strategy

import com.example.dsl.proto.WidgetNode

/**
 * 렌더링 전략을 선택하는 Factory
 * 노드 속성에 따라 적절한 Strategy를 반환
 */
object RenderStrategyFactory {
    /**
     * Text 노드에 대한 적절한 Strategy 선택
     */
    fun getTextStrategy(node: WidgetNode): RenderStrategy {
        return if (node.hasText() && node.text.viewProperty.partiallyUpdate) {
            TextRenderStrategy.RemoteViews
        } else {
            TextRenderStrategy.Glance
        }
    }

    /**
     * Image 노드에 대한 적절한 Strategy 선택
     */
    fun getImageStrategy(node: WidgetNode): RenderStrategy {
        if (!node.hasImage()) {
            return ImageRenderStrategy.Glance
        }

        // 애니메이션 이미지는 항상 RemoteViews 사용
        if (node.image.animation) {
            return ImageRenderStrategy.AnimationRemoteViews
        }

        // partiallyUpdate가 true이면 RemoteViews 사용
        if (node.image.viewProperty.partiallyUpdate) {
            return ImageRenderStrategy.RemoteViews
        }

        return ImageRenderStrategy.Glance
    }

    /**
     * Progress 노드에 대한 적절한 Strategy 선택
     */
    fun getProgressStrategy(node: WidgetNode): RenderStrategy {
        return if (node.hasProgress() && node.progress.viewProperty.partiallyUpdate) {
            ProgressRenderStrategy.RemoteViews
        } else {
            ProgressRenderStrategy.Glance
        }
    }
}

