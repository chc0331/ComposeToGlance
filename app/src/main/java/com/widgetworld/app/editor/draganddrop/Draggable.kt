package com.widgetworld.app.editor.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun <T> Draggable(
    dataToDrop: T,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val dragState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        dragState.dragOffset = offset
                        dragState.dataToDrop = dataToDrop
                        dragState.isDragging = true
                        dragState.itemDropped = false
                        dragState.dragPosition = currentPosition
                        dragState.draggableComposable = {
                            Box {
                                content()
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragState.itemDropped = false
                        dragState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = {
                        dragState.isDragging = false
                    },
                    onDragCancel = {
                        dragState.isDragging = false
                        dragState.dragOffset = Offset.Zero
                        dragState.itemDropped = false
                        dragState.dataToDrop = null
                        dragState.draggableComposable = null
                    }
                )
            }
    ) {
        content()
    }
}
