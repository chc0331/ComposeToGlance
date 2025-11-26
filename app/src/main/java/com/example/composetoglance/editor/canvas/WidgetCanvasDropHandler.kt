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
        if ((droppedItem !is WidgetComponent && droppedItem !is PositionedWidget) || dragInfo.itemDropped) {
            return@DropTarget
        }
        
        val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
        
        // 레이아웃이 있고 PositionedWidget이 레이아웃 밖으로 드래그된 경우 삭제
        if (droppedItem is PositionedWidget && layoutBounds != null) {
            val bounds = layoutBounds
            val isWithinLayoutBounds = dropPositionInWindow.x >= bounds.position.x &&
                    dropPositionInWindow.x <= bounds.position.x + bounds.size.width &&
                    dropPositionInWindow.y >= bounds.position.y &&
                    dropPositionInWindow.y <= bounds.position.y + bounds.size.height
            
            if (!isWithinLayoutBounds) {
                // 즉시 드래그 상태 정리하여 잔상 방지
                dragInfo.itemDropped = true
                dragInfo.isDragging = false
                dragInfo.dragOffset = Offset.Zero
                dragInfo.dataToDrop = null
                dragInfo.draggableComposable = null
                viewModel.removePositionedWidget(droppedItem)
                return@DropTarget
            }
        }
        
        // 캔버스 밖으로 드래그된 PositionedWidget은 삭제
        if (!isInBound && droppedItem is PositionedWidget) {
            // 즉시 드래그 상태 정리하여 잔상 방지
            dragInfo.itemDropped = true
            dragInfo.isDragging = false
            dragInfo.dragOffset = Offset.Zero
            dragInfo.dataToDrop = null
            dragInfo.draggableComposable = null
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