package com.example.composetoglance.ui.draganddrop

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * 드래그 가능한 타겟을 생성하는 Composable
 * 롱 프레스 후 드래그 제스처를 감지합니다.
 *
 * @param context Android Context
 * @param modifier Modifier
 * @param dataToDrop 드롭될 데이터 (현재는 Any? 타입이지만, 향후 제네릭으로 개선 가능)
 * @param content 드래그 중인 아이템을 렌더링하는 콘텐츠
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragTarget(
    context: Context,
    modifier: Modifier,
    dataToDrop: Any? = null,
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

/**
 * 드롭 가능한 타겟 영역을 생성하는 Composable
 * 드래그 중인 아이템이 이 영역에 들어왔는지 감지합니다.
 *
 * @param modifier Modifier
 * @param content 드롭 영역의 콘텐츠. isInBound는 드래그 아이템이 영역 내부에 있는지 여부, data는 드롭된 데이터
 */
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

