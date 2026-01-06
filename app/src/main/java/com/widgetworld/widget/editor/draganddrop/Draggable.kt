package com.widgetworld.widget.editor.draganddrop

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
    modifier: Modifier = Modifier,
    dataToDrop: T,
    content: @Composable BoxScope.() -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        currentState.dragOffset = offset
                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.itemDropped = false
                        currentState.dragPosition = currentPosition
                        currentState.draggableComposable = {
                            Box {
                                content()
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentState.itemDropped = false
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = {
                        // isDragging을 false로 설정하여 DropTarget이 데이터를 전달할 수 있도록 함
                        currentState.isDragging = false
                        // dragOffset과 dataToDrop은 DropTarget이 처리할 수 있도록 유지
                        // 드롭 위치 계산을 위해 dragOffset을 유지해야 함
                        // itemDropped가 true이면 WidgetEditorContainer에서 페이드아웃 후 초기화
                        // itemDropped가 false이면 WidgetEditorContainer에서 지연 후 초기화
                    },
                    onDragCancel = {
                        currentState.isDragging = false
                        currentState.dragOffset = Offset.Zero
                        currentState.itemDropped = false
                        currentState.dataToDrop = null
                        currentState.draggableComposable = null
                    }
                )
            }
    ) {
        content()
    }
}
