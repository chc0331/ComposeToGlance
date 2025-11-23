package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composetoglance.editor.draganddrop.DragTargetInfo
import com.example.composetoglance.editor.layout.Layout
import com.example.composetoglance.editor.layout.gridSpec
import com.example.composetoglance.editor.util.GridCalculator
import com.example.composetoglance.editor.util.GridCell
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.widget.Widget
import com.example.composetoglance.editor.widget.WidgetItem
import com.example.composetoglance.editor.widget.getSizeInCells
import com.example.composetoglance.editor.canvas.toPixels
import kotlin.math.roundToInt

@Composable
fun DragStateOverlay(
    viewModel: WidgetEditorViewModel,
    gridCells: List<GridCell>,
    occupiedCells: Set<Int>,
    selectedLayout: Layout?,
    layoutBounds: LayoutBounds?,
    canvasPosition: Offset,
    density: Density,
    dragInfo: DragTargetInfo
) {
    if (!dragInfo.isDragging || gridCells.isEmpty()) {
        return
    }
    val draggedWidget = dragInfo.dataToDrop as? Widget ?: return
    val hoveredCellIndices = rememberHoveredCellIndices(
        viewModel = viewModel,
        dragInfo = dragInfo,
        draggedWidget = draggedWidget,
        gridCells = gridCells,
        selectedLayout = selectedLayout,
        layoutBounds = layoutBounds
    )
    if (hoveredCellIndices.isNotEmpty()) {
        WidgetPreview(
            draggedWidget = draggedWidget,
            hoveredCellIndices = hoveredCellIndices,
            layoutBounds = layoutBounds,
            selectedLayout = selectedLayout,
            canvasPosition = canvasPosition,
            density = density
        )
    }
    GridCellHighlight(
        gridCells = gridCells,
        occupiedCells = occupiedCells,
        hoveredCellIndices = hoveredCellIndices,
        canvasPosition = canvasPosition,
        density = density
    )
}

@Composable
private fun rememberHoveredCellIndices(
    viewModel: WidgetEditorViewModel,
    dragInfo: DragTargetInfo,
    draggedWidget: Widget,
    gridCells: List<GridCell>,
    selectedLayout: Layout?,
    layoutBounds: LayoutBounds?
): List<Int> {
    return remember(
        viewModel.positionedWidgets,
        dragInfo.isDragging,
        dragInfo.dragPosition,
        dragInfo.dragOffset,
        draggedWidget,
        gridCells,
        selectedLayout,
        layoutBounds
    ) {
        if (!dragInfo.isDragging || selectedLayout == null || layoutBounds == null) {
            return@remember emptyList()
        }
        val spec = selectedLayout.gridSpec() ?: return@remember emptyList()
        val (widgetWidthCells, widgetHeightCells) = draggedWidget.getSizeInCells()
        val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
        val bestStart = GridCalculator.calculateBestCellPosition(
            dropPositionInWindow,
            widgetWidthCells,
            widgetHeightCells,
            gridCells,
            spec,
            layoutBounds
        ) ?: return@remember emptyList()
        val (startRow, startCol) = bestStart
        val indices = GridCalculator.calculateCellIndices(
            startRow,
            startCol,
            widgetWidthCells,
            widgetHeightCells,
            spec
        )
        if (viewModel.canPlaceWidget(indices)) indices else emptyList()
    }
}

@Composable
private fun WidgetPreview(
    draggedWidget: Widget,
    hoveredCellIndices: List<Int>,
    layoutBounds: LayoutBounds?,
    selectedLayout: Layout?,
    canvasPosition: Offset,
    density: Density
) {
    val bounds = layoutBounds ?: return
    val spec = selectedLayout?.gridSpec() ?: return
    val startCellIndex = hoveredCellIndices.first()
    val startRow = startCellIndex / spec.columns
    val startCol = startCellIndex % spec.columns
    val (widgetWidthCells, widgetHeightCells) = draggedWidget.getSizeInCells()
    val (widgetWidthPx, widgetHeightPx) = draggedWidget.toPixels(density)
    val previewOffset = GridCalculator.calculateWidgetOffset(
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
    Box(
        modifier = androidx.compose.ui.Modifier.offset {
            IntOffset(previewOffset.x.roundToInt(), previewOffset.y.roundToInt())
        }
    ) {
        WidgetItem(
            data = draggedWidget,
            modifier = androidx.compose.ui.Modifier.alpha(WidgetCanvasConstants.PREVIEW_ALPHA)
        )
    }
}

@Composable
private fun GridCellHighlight(
    gridCells: List<GridCell>,
    occupiedCells: Set<Int>,
    hoveredCellIndices: List<Int>,
    canvasPosition: Offset,
    density: Density
) {
    gridCells.forEach { cell ->
        val isOccupied = occupiedCells.contains(cell.index)
        val isHovered = hoveredCellIndices.contains(cell.index)
        if (isHovered) {
            HighlightedCell(
                cell = cell,
                canvasPosition = canvasPosition,
                density = density
            )
        } else if (!isOccupied) {
            EmptyCell(
                cell = cell,
                canvasPosition = canvasPosition,
                density = density
            )
        }
    }
}

@Composable
private fun HighlightedCell(
    cell: GridCell,
    canvasPosition: Offset,
    density: Density
) {
    val offset = IntOffset(
        (cell.rect.left - canvasPosition.x).roundToInt(),
        (cell.rect.top - canvasPosition.y).roundToInt()
    )
    val widthDp = with(density) { cell.rect.width.toDp() }
    val heightDp = with(density) { cell.rect.height.toDp() }
    Box(
        modifier = androidx.compose.ui.Modifier
            .offset { offset }
            .size(widthDp, heightDp)
            .background(
                MaterialTheme.colorScheme.primary.copy(
                    alpha = WidgetCanvasConstants.HOVERED_CELL_BACKGROUND_ALPHA
                )
            )
            .border(
                width = WidgetCanvasConstants.HOVERED_CELL_BORDER_WIDTH_DP.dp,
                color = MaterialTheme.colorScheme.primary
            )
    )
}

@Composable
private fun EmptyCell(
    cell: GridCell,
    canvasPosition: Offset,
    density: Density
) {
    val offset = IntOffset(
        (cell.rect.left - canvasPosition.x).roundToInt(),
        (cell.rect.top - canvasPosition.y).roundToInt()
    )
    val widthDp = with(density) { cell.rect.width.toDp() }
    val heightDp = with(density) { cell.rect.height.toDp() }
    Box(
        modifier = androidx.compose.ui.Modifier
            .offset { offset }
            .size(widthDp, heightDp)
            .background(
                MaterialTheme.colorScheme.primary.copy(
                    alpha = WidgetCanvasConstants.EMPTY_CELL_BACKGROUND_ALPHA
                )
            )
            .border(
                width = WidgetCanvasConstants.EMPTY_CELL_BORDER_WIDTH_DP.dp,
                color = MaterialTheme.colorScheme.outline.copy(
                    alpha = WidgetCanvasConstants.EMPTY_CELL_BORDER_ALPHA
                )
            )
    )
}
