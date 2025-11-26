package com.example.composetoglance.editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
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
            // 드래그 중이거나 드롭 직후 페이드아웃 중일 때 오버레이 표시
            // 단, dataToDrop이 null이면 즉시 숨김 (삭제 시 잔상 방지)
            if ((state.isDragging || state.itemDropped) && state.dataToDrop != null) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                val hasValidSize = targetSize != IntSize.Zero
                
                // 드롭 시 페이드아웃을 위한 alpha 타겟 값
                val targetAlpha = when {
                    !hasValidSize -> 0f
                    state.itemDropped -> 0f // 드롭되면 페이드아웃
                    else -> DragAndDropConstants.DRAG_ALPHA
                }
                
                // 스케일 애니메이션: 드래그 시작 시 1.0에서 1.5로 부드럽게 확대, 드롭 시 즉시 축소
                val targetScale = when {
                    !hasValidSize -> 1f
                    state.itemDropped -> 1f // 드롭되면 즉시 원래 크기로
                    else -> DragAndDropConstants.SCALE_FACTOR
                }
                val animatedScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = tween(durationMillis = if (state.itemDropped) 100 else 200),
                    label = "scale_animation"
                )
                
                // alpha 애니메이션: 크기가 측정되면 부드럽게 나타남, 드롭 시 빠르게 페이드아웃
                val fadeOutDuration = if (state.itemDropped) 100 else 200
                val animatedAlpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = tween(durationMillis = fadeOutDuration),
                    label = "alpha_animation"
                )
                
                // 페이드아웃 완료 후 상태 초기화
                LaunchedEffect(state.itemDropped) {
                    if (state.itemDropped) {
                        // 페이드아웃 애니메이션 시간만큼 대기
                        delay(fadeOutDuration.toLong() + 50)
                        println("Cleaning up drag state - drop completed")
                        state.itemDropped = false
                        state.isDragging = false
                        state.dragOffset = Offset.Zero
                        state.dataToDrop = null
                        state.draggableComposable = null
                    }
                }
                
                // 드롭되지 않은 경우 상태 초기화 (DropTarget이 데이터를 처리할 시간을 주기 위해 약간의 지연)
                LaunchedEffect(state.isDragging, state.itemDropped) {
                    if (!state.isDragging && !state.itemDropped && state.dataToDrop != null) {
                        // DropTarget이 재구성되어 데이터를 처리할 시간을 줌
                        delay(300)
                        if (!state.itemDropped && !state.isDragging) {
                            println("Cleaning up drag state - no drop detected")
                            state.dragOffset = Offset.Zero
                            state.dataToDrop = null
                            state.draggableComposable = null
                        }
                    }
                }
                
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

