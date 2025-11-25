package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.composetoglance.editor.widget.WidgetItem
import com.example.composetoglance.editor.widget.toPixels
import com.example.composetoglance.editor.widget.gridSpec
import com.example.widget.Widget
import com.example.widget.getSizeInCells
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(
    viewModel: WidgetEditorViewModel,
    modifier: Modifier = Modifier,
    widgetToAdd: Widget? = null,
    onWidgetAddProcessed: () -> Unit = {}
) {
    val selectedLayout = viewModel.selectedLayout
    val positionedWidgets = viewModel.positionedWidgets
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

    // 위젯 추가 요청 처리
    LaunchedEffect(widgetToAdd, layoutBounds, selectedLayout, canvasPosition) {
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
        val adjustedOffset = GridCalculator.calculateWidgetOffset(
            startRow,
            startCol,
            widgetWidthCells,
            widgetHeightCells,
            widgetWidthPx,
            widgetHeightPx,
            bounds,
            spec,
            canvasPosition
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
            .onGloballyPositioned {
                canvasPosition = it.positionInWindow()
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
            Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
        } else {
            LayoutDisplay(
                selectedLayout = selectedLayout,
                onLayoutBoundsChanged = { bounds ->
                    layoutBounds = bounds
                }
            )

            val gridCells = rememberGridCells(selectedLayout, layoutBounds)
            val occupiedCells = viewModel.getOccupiedCells()

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
            positionedWidgets.forEachIndexed { index, item ->
                val isDragging = dragInfo.isDragging && dragInfo.dataToDrop == item
                key("${item.widget.name}_${item.offset.x}_${item.offset.y}_$index") {
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