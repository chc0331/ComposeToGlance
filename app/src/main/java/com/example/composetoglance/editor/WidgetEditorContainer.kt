package com.example.composetoglance.editor

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
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val currentTouchWindowPosition = state.dragPosition + state.dragOffset
                            val relativeOffset = currentTouchWindowPosition - canvasPosition
                            val scaleFactor = DragAndDropConstants.SCALE_FACTOR
                            val scaledWidth = targetSize.width * scaleFactor
                            val scaledHeight = targetSize.height * scaleFactor
                            val centerX = relativeOffset.x - scaledWidth / 2
                            val centerY = relativeOffset.y - scaledHeight / 2

                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            alpha = if (targetSize == IntSize.Zero) 0f else DragAndDropConstants.DRAG_ALPHA
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

