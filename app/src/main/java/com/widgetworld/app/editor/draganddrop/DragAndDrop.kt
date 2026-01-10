package com.widgetworld.app.editor.draganddrop

import android.content.Context
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * 드래그 가능한 타겟을 생성하는 Composable
 * 롱 프레스 후 드래그 제스처와 탭 제스처를 감지합니다.
 *
 * @param context Android Context
 * @param modifier Modifier
 * @param dataToDrop 드롭될 데이터
 * @param onComponentClick 탭 이벤트 콜백
 * @param onDragStart 드래그 시작 콜백
 * @param dragContent 드래그 시 보여줄 콘텐츠. null이면 content를 사용.
 * @param content 드래그 중인 아이템을 렌더링하는 콘텐츠
 */
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
    // isDragging을 명시적으로 읽어서 재구성 트리거
    val isDragging = dragInfo.isDragging
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    val dataToDrop = dragInfo.dataToDrop
    val itemDropped = dragInfo.itemDropped
    var dropTargetBounds by remember {
        mutableStateOf<Rect?>(null)
    }
    var wasInBounds by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                dropTargetBounds = it.boundsInWindow()
            }
    ) {
        // 매 재구성마다 현재 위치를 확인하여 드롭 여부 판단
        val currentPos = dragPosition + dragOffset
        val isCurrentDropTarget = dropTargetBounds?.contains(currentPos) ?: false
        
        // 드래그 중일 때 경계 내부에 있었는지 기록
        if (isDragging) {
            wasInBounds = isCurrentDropTarget
        }
        
        // 드래그가 시작되지 않았거나 데이터가 없으면 wasInBounds 리셋
        if (dataToDrop == null || (!isDragging && itemDropped)) {
            wasInBounds = false
        }
        
        val data =
            if ((isCurrentDropTarget || wasInBounds) && !isDragging && dataToDrop != null && !itemDropped) {
                dataToDrop
            } else {
                null
            }
        
        content(isCurrentDropTarget, data)
    }
}
