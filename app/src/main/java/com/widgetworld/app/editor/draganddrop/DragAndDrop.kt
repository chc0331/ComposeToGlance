package com.widgetworld.app.editor.draganddrop

import android.content.Context
import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun DragTarget(
    context: Context,
    modifier: Modifier,
    dataToDrop: Any? = null,
    onComponentClick: () -> Unit = {},
    onDragStart: () -> Unit = {},
    dragContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .wrapContentSize()
            .onGloballyPositioned { layoutCoordinates ->
                val newPosition = layoutCoordinates.localToWindow(Offset.Zero)
                // 값이 실제로 변경되었을 때만 상태 업데이트하여 불필요한 재구성 방지
                if (newPosition != currentPosition) {
                    currentPosition = newPosition
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onComponentClick() })
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        onDragStart()
                        currentState.dragOffset = offset
                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition
                        currentState.draggableComposable = {
                            if (dragContent != null) {
                                dragContent()
                            } else {
                                content()
                            }
                        }
                    }, onDrag = { change, dragAmount ->
                        change.consume()
                        currentState.itemDropped = false
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)

                    }, onDragEnd = {
                        // isDragging을 false로 설정하여 DropTarget이 데이터를 전달할 수 있도록 함
                        currentState.isDragging = false
                        // dragOffset과 dataToDrop은 DropTarget이 처리할 수 있도록 유지
                        // 드롭 위치 계산을 위해 dragOffset을 유지해야 함
                        // itemDropped가 true이면 WidgetEditorContainer에서 페이드아웃 후 초기화
                        // itemDropped가 false이면 WidgetEditorContainer에서 지연 후 초기화
                    }, onDragCancel = {
                        currentState.isDragging = false
                        currentState.dragOffset = Offset.Zero
                        currentState.itemDropped = false
                        currentState.dataToDrop = null
                        currentState.draggableComposable = null
                    })
            }, contentAlignment = Alignment.Center
    ) {
        content() // render positioned content with animation
    }
}


data class DropTargetState(
    val isInBound: Boolean,
    val droppedData: Any?,
    val dropPositionInWindow: Offset
)

@Composable
fun DropTarget(
    onDrop: (DropTargetState) -> Unit
) {
    // isDragging을 명시적으로 읽어서 재구성 트리거
    var dropTargetBounds by remember { mutableStateOf<Rect?>(null) }
    val dragState = LocalDragTargetInfo.current

    fun isDropItemInBound(): Boolean {
        val currentPos = dragState.dragPosition + dragState.dragOffset
        return dropTargetBounds?.contains(currentPos) ?: false
    }

    val isInBound = isDropItemInBound()

    /**
     * Drop condition
     * 1) not dragging
     * 2) not item dropped
     * 3) drop data exist
     * 4) in drop target bound
     * */
    val canAcceptDrop = !dragState.isDragging && !dragState.itemDropped &&
            dragState.dataToDrop != null && isInBound

    val droppedData = if (canAcceptDrop) dragState.dataToDrop else null
    val state = DropTargetState(
        isInBound = isInBound,
        droppedData = droppedData,
        dropPositionInWindow = dragState.dragPosition + dragState.dragOffset
    )

    LaunchedEffect(key1 = state) {
        if (state.isInBound && state.droppedData != null) {
            onDrop(state)
        }
    }

    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                dropTargetBounds = it.boundsInWindow()
                Log.i(TAG, "DropTargetBounds : $dropTargetBounds")
            }
    )
}

private const val TAG = "DropTarget"