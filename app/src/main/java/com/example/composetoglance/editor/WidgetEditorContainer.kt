package com.example.composetoglance.editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.composetoglance.editor.draganddrop.DragAndDropConstants
import com.example.composetoglance.editor.draganddrop.DragTargetInfo
import com.example.composetoglance.editor.draganddrop.LocalDragTargetInfo

/**
 * 위젯 편집 컨테이너 Composable
 * 드래그 앤 드롭 기능을 제공하며, 드래그 중인 아이템을 확대하여 표시합니다.
 */
@Composable
fun WidgetEditorContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }

    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    canvasPosition = it.localToWindow(Offset.Zero)
                }) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                val hasValidSize = targetSize != IntSize.Zero
                
                // 스케일 애니메이션: 드래그 시작 시 1.0에서 1.5로 부드럽게 확대
                val animatedScale by animateFloatAsState(
                    targetValue = if (hasValidSize) DragAndDropConstants.SCALE_FACTOR else 1f,
                    animationSpec = tween(durationMillis = 200),
                    label = "scale_animation"
                )
                
                // alpha 애니메이션: 크기가 측정되면 부드럽게 나타남
                val animatedAlpha by animateFloatAsState(
                    targetValue = if (hasValidSize) DragAndDropConstants.DRAG_ALPHA else 0f,
                    animationSpec = tween(durationMillis = 200),
                    label = "alpha_animation"
                )
                
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val currentTouchWindowPosition = state.dragPosition + state.dragOffset
                            val relativeOffset = currentTouchWindowPosition - canvasPosition
                            val scaledWidth = targetSize.width * animatedScale
                            val scaledHeight = targetSize.height * animatedScale
                            val centerX = relativeOffset.x - scaledWidth / 2
                            val centerY = relativeOffset.y - scaledHeight / 2

                            scaleX = animatedScale
                            scaleY = animatedScale
                            alpha = animatedAlpha
                            translationX = centerX
                            translationY = centerY
                        }
                        .onGloballyPositioned {
                            targetSize = it.size
                        }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

