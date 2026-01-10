package com.widgetworld.app.editor.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.widgetworld.app.editor.draganddrop.Draggable
import com.widgetworld.app.editor.draganddrop.LocalDragTargetInfo
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.viewmodel.WidgetEditorViewModel
import com.widgetworld.app.editor.widget.PositionedWidget
import com.widgetworld.app.editor.widget.WidgetComponent
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout
import com.widgetworld.app.editor.widget.toPixels
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.component.WidgetComponent
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(
    viewModel: WidgetEditorViewModel,
    selectedLayout: LayoutType? = null,
    positionedWidgets: List<PositionedWidget> = emptyList(),
    modifier: Modifier = Modifier,
    widgetToAdd: WidgetComponent? = null,
    onWidgetAddProcessed: (Offset, LayoutBounds, LayoutType) -> Unit
) {
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasBounds by remember { mutableStateOf<Rect?>(null) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

    // 위젯 추가 요청 처리
    LaunchedEffect(widgetToAdd, layoutBounds, selectedLayout) {
        // 위젯 추가 처리 완료
        if (layoutBounds != null && selectedLayout != null) {
            onWidgetAddProcessed(canvasPosition, layoutBounds!!, selectedLayout)
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                val newPosition = layoutCoordinates.positionInWindow()
                val newBounds = layoutCoordinates.boundsInWindow()
                // 값이 실제로 변경되었을 때만 상태 업데이트하여 불필요한 재구성 방지
                if (newPosition != canvasPosition) {
                    canvasPosition = newPosition
                }
                if (newBounds != canvasBounds) {
                    canvasBounds = newBounds
                }
            }
    ) {
        WidgetDropHandler(
            viewModel = viewModel,
            layoutBounds = layoutBounds,
            selectedLayout = selectedLayout,
            canvasPosition = canvasPosition,
            density = density,
            dragInfo = dragInfo
        )

        if (selectedLayout == null && positionedWidgets.isEmpty()) {
            Text(
                "위젯 캔버스",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            DeleteZoneIndicator(
                layoutBounds = layoutBounds,
                canvasPosition = canvasPosition,
                canvasBounds = canvasBounds,
                dragInfo = dragInfo,
                density = density
            )
            LayoutDisplay(
                selectedLayout = selectedLayout,
                onLayoutBoundsChanged = { bounds ->
                    layoutBounds = bounds
                }
            )

            val gridCells = rememberGridCells(selectedLayout, layoutBounds)
            val occupiedCells = remember(viewModel.positionedWidgets) {
                derivedStateOf {
                    viewModel.getOccupiedCells()
                }
            }.value

            DragStateOverlay(
                viewModel = viewModel,
                gridCells = gridCells,
                occupiedCells = occupiedCells,
                selectedLayout = selectedLayout,
                layoutBounds = layoutBounds,
                canvasPosition = canvasPosition,
                density = density,
                dragInfo = dragInfo
            )

            // Display dropped widgets
            val draggedItemId = remember(dragInfo.isDragging, dragInfo.dataToDrop) {
                if (dragInfo.isDragging && dragInfo.dataToDrop is PositionedWidget) {
                    (dragInfo.dataToDrop as PositionedWidget).id
                } else {
                    null
                }
            }

            positionedWidgets.forEach { item ->
                val isDragging = item.id == draggedItemId
                // 고유 ID를 key로 사용하여 위젯 이동 시 재생성되지 않도록 함
                // 이렇게 하면 위젯이 재배치될 때 기존 컴포저블이 유지되고 offset만 업데이트됨
                key(item.id) {
                    Draggable(
                        dataToDrop = item,
                        modifier = Modifier
                            .offset {
                                IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                            }
                            .alpha(if (isDragging) 0f else 1f)
                    ) {
                        WidgetComponent(
                            data = item.widget,
                            layout = selectedLayout
                        )
                    }
                }
            }
        }
    }
}