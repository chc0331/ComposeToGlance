package com.example.composetoglance.draganddrop

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import kotlin.math.abs

internal class DragTargetInfo {
    var isDragging: Boolean? by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
    var itemDropped: Boolean by mutableStateOf(false)
    var absolutePositionX: Float by mutableStateOf(0F)
    var absolutePositionY: Float by mutableStateOf(0F)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }


@Composable
fun LongPressDrawable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }

    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
            if (state.isDragging == true) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val offset = (state.dragPosition + state.dragOffset)
                            scaleX = 1.5f
                            scaleY = 1.5f
                            alpha = if (targetSize == IntSize.Zero) 0f else .9f
                            translationX = offset.x.minus(targetSize.width / 2)
                            translationY = offset.y.minus(targetSize.height / 2)
                        }
                        .onGloballyPositioned {
                            targetSize = it.size
                            it.let { coordinates ->
                                state.absolutePositionX = coordinates.positionInRoot().x
                                state.absolutePositionY = coordinates.positionInRoot().y
                            }
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
    val coroutineScope = rememberCoroutineScope()

    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier.wrapContentSize()
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {

                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition + it
                        currentState.draggableComposable = {
                            content(false) // render scaled item without animation
                        }
                    }, onDrag = { change, dragAmount ->
                        change.consume()

                        currentState.itemDropped = false // used to prevent drop target from multiple re-renders

                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)

                        val xOffset = abs(currentState.dragOffset.x)
                        val yOffset = abs(currentState.dragOffset.y)

                        coroutineScope.launch {
                        }

                    }, onDragEnd = {
                        currentState.isDragging = false
                        currentState.dragOffset = Offset.Zero
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
fun <T> DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: T?) -> Unit)
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
                    isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
                }
            }
    ) {
        val data =
            if (isCurrentDropTarget && dragInfo.isDragging == false) dragInfo.dataToDrop as T? else null
        content(isCurrentDropTarget, data)
    }
}

