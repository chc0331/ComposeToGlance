package com.example.composetoglance.editor.canvas

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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.example.composetoglance.editor.draganddrop.Draggable
import com.example.composetoglance.editor.draganddrop.LocalDragTargetInfo
import com.example.composetoglance.editor.util.GridCalculator
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.widget.PositionedWidget
import com.example.composetoglance.editor.widget.WidgetItem
import com.example.composetoglance.editor.widget.toPixels
import com.example.composetoglance.editor.widget.gridSpec
import com.example.widget.component.WidgetComponent
import com.example.widget.getSizeInCells
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(
    viewModel: WidgetEditorViewModel,
    modifier: Modifier = Modifier,
    widgetToAdd: WidgetComponent? = null,
    onWidgetAddProcessed: () -> Unit = {}
) {
    val selectedLayout = viewModel.selectedLayout
    val positionedWidgets = viewModel.positionedWidgets
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

    // 위젯 추가 요청 처리
    LaunchedEffect(widgetToAdd, layoutBounds, selectedLayout) {
        val widget = widgetToAdd ?: return@LaunchedEffect
        
        val bounds = layoutBounds
        val spec = selectedLayout?.gridSpec()
        if (bounds == null || spec == null) {
            onWidgetAddProcessed()
            return@LaunchedEffect
        }
        
        // 첫 번째 사용 가능한 위치 찾기
        val position = viewModel.findFirstAvailablePosition(widget, spec)
        if (position == null) {
            // 배치할 수 없음
            onWidgetAddProcessed()
            return@LaunchedEffect
        }
        
        val (startRow, startCol) = position
        val (widgetWidthCells, widgetHeightCells) = widget.getSizeInCells()
        val cellIndices = GridCalculator.calculateCellIndices(
            startRow,
            startCol,
            widgetWidthCells,
            widgetHeightCells,
            spec
        )
        
        // 위젯 실제 크기 DP→픽셀 변환
        val (widgetWidthPx, widgetHeightPx) = widget.toPixels(density)
        // canvasPosition은 LaunchedEffect 내부에서 직접 읽어서 사용
        val currentCanvasPosition = canvasPosition
        val adjustedOffset = GridCalculator.calculateWidgetOffset(
            startRow,
            startCol,
            widgetWidthCells,
            widgetHeightCells,
            widgetWidthPx,
            widgetHeightPx,
            bounds,
            spec,
            currentCanvasPosition
        )
        
        // ViewModel에 위젯 추가
        viewModel.addWidgetToFirstAvailablePosition(
            widget = widget,
            offset = adjustedOffset,
            startRow = startRow,
            startCol = startCol,
            cellIndices = cellIndices
        )
        
        // 위젯 추가 처리 완료
        onWidgetAddProcessed()
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                val newPosition = layoutCoordinates.positionInWindow()
                // 값이 실제로 변경되었을 때만 상태 업데이트하여 불필요한 재구성 방지
                if (newPosition != canvasPosition) {
                    canvasPosition = newPosition
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
                color = MaterialTheme.colorScheme.primary
            )
        } else {
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
                        WidgetItem(data = item.widget)
                    }
                }
            }
        }
    }
}