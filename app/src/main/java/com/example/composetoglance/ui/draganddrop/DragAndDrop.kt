package com.example.composetoglance.ui.draganddrop

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
    var itemDropped: Boolean by mutableStateOf(false)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun LongPressDrawable(
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
                            val scaleFactor = 1.5f
                            val scaledWidth = targetSize.width * scaleFactor
                            val scaledHeight = targetSize.height * scaleFactor
                            val centerX = relativeOffset.x - scaledWidth / 2
                            val centerY = relativeOffset.y - scaledHeight / 2

                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            alpha = if (targetSize == IntSize.Zero) 0f else .9f
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragTarget(
    context: Context,
    modifier: Modifier,
    dataToDrop: Any? = null, // change type here to your data model class
    content: @Composable (shouldAnimate: Boolean) -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .wrapContentSize()
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        currentState.dragOffset = offset
                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition
                        currentState.draggableComposable = {
                            content(false) // render scaled item without animation
                        }
                    }, onDrag = { change, dragAmount ->
                        change.consume()
                        currentState.itemDropped = false
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)

                    }, onDragEnd = {
                        currentState.isDragging = false
                    }, onDragCancel = {
                        currentState.isDragging = false
                        currentState.dragOffset = Offset.Zero
                    })
            }, contentAlignment = Alignment.Center
    ) {
        content(true) // render positioned content with animation
    }
}

@Composable
fun DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: Any?) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                it.boundsInWindow().let { rect ->
                    val currentPos = dragPosition + dragOffset
                    isCurrentDropTarget = rect.contains(currentPos)
                }
            }
    ) {
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop else null
        content(isCurrentDropTarget, data)
    }
}
