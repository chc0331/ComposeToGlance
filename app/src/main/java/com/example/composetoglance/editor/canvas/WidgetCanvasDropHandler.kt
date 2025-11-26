package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.example.composetoglance.editor.draganddrop.DragTargetInfo
import com.example.composetoglance.editor.draganddrop.DropTarget
import com.example.composetoglance.editor.widget.Layout
import com.example.composetoglance.editor.widget.gridSpec
import com.example.composetoglance.editor.util.GridCalculator
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.widget.PositionedWidget
import com.example.composetoglance.editor.widget.toPixels
import com.example.widget.component.WidgetComponent
import com.example.widget.getSizeInCells

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
        // 디버깅용 로그
        println("DropTarget: isInBound=$isInBound, droppedItem=$droppedItem, itemDropped=${dragInfo.itemDropped}")
        
        if ((droppedItem !is WidgetComponent && droppedItem !is PositionedWidget) || dragInfo.itemDropped) {
            return@DropTarget
        }
        
        // 캔버스 밖으로 드래그된 PositionedWidget은 삭제
        if (!isInBound && droppedItem is PositionedWidget) {
            dragInfo.itemDropped = true
            viewModel.removePositionedWidget(droppedItem)
            return@DropTarget
        }
        
        // 캔버스 밖이거나 새 위젯인 경우 처리하지 않음
        if (!isInBound) {
            return@DropTarget
        }

        val widget = when (droppedItem) {
            is WidgetComponent -> droppedItem
            is PositionedWidget -> droppedItem.widget
            else -> return@DropTarget
        }
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

        val occupiedIndices = if (droppedItem is PositionedWidget) {
            viewModel.getOccupiedCells(excluding = droppedItem)
        } else {
            viewModel.getOccupiedCells()
        }

        if (indices.any { it in occupiedIndices }) {
            return@DropTarget
        }

        dragInfo.itemDropped = true

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

        when (droppedItem) {
            is WidgetComponent -> viewModel.addPositionedWidget(
                widget = widget,
                offset = adjustedOffset,
                startRow = startRow,
                startCol = startCol,
                cellIndices = indices
            )
            is PositionedWidget -> {
                println("Moving PositionedWidget: from ${droppedItem.offset} to $adjustedOffset")
                viewModel.movePositionedWidget(
                    positionedWidget = droppedItem,
                    offset = adjustedOffset,
                    startRow = startRow,
                    startCol = startCol,
                    cellIndices = indices
                )
            }
        }
    }
}