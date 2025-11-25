package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.view.HapticFeedbackConstants
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import com.example.composetoglance.editor.draganddrop.DragTargetInfo
import com.example.composetoglance.editor.widget.Layout
import com.example.composetoglance.editor.widget.gridSpec
import com.example.composetoglance.editor.util.GridCalculator
import com.example.composetoglance.editor.util.GridCell
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.widget.PositionedWidget
import com.example.composetoglance.editor.widget.WidgetItem
import com.example.composetoglance.editor.widget.toPixels
import com.example.widget.Widget
import com.example.widget.getSizeInCells
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

    val (draggedWidget, draggedPositionedWidget) = when (val item = dragInfo.dataToDrop) {
        is Widget -> item to null
        is PositionedWidget -> item.widget to item
        else -> return
    }

    val hoveredCellIndices = rememberHoveredCellIndices(
        viewModel = viewModel,
        dragInfo = dragInfo,
        draggedWidget = draggedWidget,
        draggedPositionedWidget = draggedPositionedWidget, // Pass this to the remember function
        gridCells = gridCells,
        selectedLayout = selectedLayout,
        layoutBounds = layoutBounds
    )

    // 드롭 가능한 영역이 나타나거나 변경될 때 햅틱 피드백 제공
    val view = LocalView.current
    var previousHoveredIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    
    LaunchedEffect(hoveredCellIndices) {
        // 드롭 가능한 영역이 나타났거나 다른 위치로 변경되었을 때 햅틱 피드백
        val hasDropZone = hoveredCellIndices.isNotEmpty()
        val hadDropZone = previousHoveredIndices.isNotEmpty()
        val dropZoneChanged = hoveredCellIndices != previousHoveredIndices
        
        if (hasDropZone && (dropZoneChanged || (!hadDropZone && hasDropZone))) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
        previousHoveredIndices = hoveredCellIndices
    }

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

    val currentOccupiedCells = if (draggedPositionedWidget != null) {
        viewModel.getOccupiedCells(excluding = draggedPositionedWidget)
    } else {
        occupiedCells
    }

    GridCellHighlight(
        gridCells = gridCells,
        occupiedCells = currentOccupiedCells,
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
    draggedPositionedWidget: PositionedWidget?,
    gridCells: List<GridCell>,
    selectedLayout: Layout?,
    layoutBounds: LayoutBounds?
): List<Int> {
    return remember(
        dragInfo.isDragging,
        dragInfo.dragPosition,
        dragInfo.dragOffset,
        draggedWidget,
        gridCells,
        selectedLayout,
        layoutBounds,
        viewModel.positionedWidgets.size
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

        val occupied = viewModel.getOccupiedCells(excluding = draggedPositionedWidget)
        if (indices.any { it in occupied }) emptyList() else indices
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
        modifier = Modifier.offset {
            IntOffset(previewOffset.x.roundToInt(), previewOffset.y.roundToInt())
        }
    ) {
        WidgetItem(
            data = draggedWidget,
            modifier = Modifier.alpha(WidgetCanvasConstants.PREVIEW_ALPHA)
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
                density = density,
                isOccupied = isOccupied
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
    density: Density,
    isOccupied: Boolean
) {
    val offset = IntOffset(
        (cell.rect.left - canvasPosition.x).roundToInt(),
        (cell.rect.top - canvasPosition.y).roundToInt()
    )
    val widthDp = with(density) { cell.rect.width.toDp() }
    val heightDp = with(density) { cell.rect.height.toDp() }
    val backgroundColor = if (isOccupied) {
        MaterialTheme.colorScheme.error.copy(alpha = WidgetCanvasConstants.HOVERED_CELL_BACKGROUND_ALPHA)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = WidgetCanvasConstants.HOVERED_CELL_BACKGROUND_ALPHA)
    }
    val borderColor = if (isOccupied) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .offset { offset }
            .size(widthDp, heightDp)
            .background(backgroundColor)
            .border(
                width = WidgetCanvasConstants.HOVERED_CELL_BORDER_WIDTH_DP,
                color = borderColor
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
        modifier = Modifier
            .offset { offset }
            .size(widthDp, heightDp)
            .background(
                MaterialTheme.colorScheme.primary.copy(
                    alpha = WidgetCanvasConstants.EMPTY_CELL_BACKGROUND_ALPHA
                )
            )
            .border(
                width = WidgetCanvasConstants.EMPTY_CELL_BORDER_WIDTH_DP,
                color = MaterialTheme.colorScheme.outline.copy(
                    alpha = WidgetCanvasConstants.EMPTY_CELL_BORDER_ALPHA
                )
            )
    )
}