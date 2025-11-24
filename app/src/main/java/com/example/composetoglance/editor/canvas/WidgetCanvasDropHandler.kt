package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.example.composetoglance.editor.draganddrop.DragTargetInfo
import com.example.composetoglance.editor.draganddrop.DropTarget
import com.example.composetoglance.editor.layout.Layout
import com.example.composetoglance.editor.layout.gridSpec
import com.example.composetoglance.editor.util.GridCalculator
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.widget.Widget
import com.example.composetoglance.editor.widget.getSizeInCells
import com.example.composetoglance.editor.canvas.toPixels

@Composable
fun WidgetDropHandler(
    viewModel: WidgetEditorViewModel,
    layoutBounds: LayoutBounds?,
    selectedLayout: Layout?,
    canvasPosition: Offset,
    density: Density,
    dragInfo: DragTargetInfo
) {
    DropTarget(modifier = Modifier.fillMaxSize()) { isInBound, droppedItem ->
        if (!isInBound || droppedItem !is Widget || dragInfo.itemDropped) {
            return@DropTarget
        }
        val widget = droppedItem
        val bounds = layoutBounds
        val spec = selectedLayout?.gridSpec()
        if (bounds == null || spec == null) {
            return@DropTarget
        }
        val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
        val (widgetWidthCells, widgetHeightCells) = widget.getSizeInCells()
        val gridCells = GridCalculator.calculateGridCells(spec, bounds)
        val bestStart = GridCalculator.calculateBestCellPosition(
            dropPositionInWindow,
            widgetWidthCells,
            widgetHeightCells,
            gridCells,
            spec,
            bounds
        ) ?: return@DropTarget
        val (startRow, startCol) = bestStart
        val indices = GridCalculator.calculateCellIndices(
            startRow,
            startCol,
            widgetWidthCells,
            widgetHeightCells,
            spec
        )
        // ViewModel에서 셀 충돌 검사
        if (!viewModel.canPlaceWidget(indices)) {
            return@DropTarget
        }
        // 드롭 처리 시작 시점에 플래그 설정 (페이드아웃 애니메이션을 위해)
        dragInfo.itemDropped = true
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
        viewModel.addPositionedWidget(
            widget = widget,
            offset = adjustedOffset,
            startRow = startRow,
            startCol = startCol,
            cellIndices = indices
        )
    }
}
