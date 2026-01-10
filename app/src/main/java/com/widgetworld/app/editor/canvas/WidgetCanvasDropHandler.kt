package com.widgetworld.app.editor.canvas

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.widgetworld.app.editor.draganddrop.DragTargetInfo
import com.widgetworld.app.editor.draganddrop.DropTarget
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.viewmodel.WidgetEditorViewModel
import com.widgetworld.app.editor.widget.PositionedWidget
import com.widgetworld.app.editor.widget.toPixels
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout

@Composable
fun WidgetDropHandler(
    viewModel: WidgetEditorViewModel,
    layoutBounds: LayoutBounds?,
    selectedLayout: LayoutType?,
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
        val spec = selectedLayout?.getGridCell()
        if (bounds == null || spec == null) {
            return@DropTarget
        }
        val currentLayout = selectedLayout ?: return@DropTarget
        val widgetSizeInCells = widget.getSizeInCellsForLayout(
            currentLayout.name,
            currentLayout.getDivide()
        )
        val widgetWidthCells = widgetSizeInCells.first
        val widgetHeightCells = widgetSizeInCells.second
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

        // 충돌 검사: 드래그 중인 위젯의 원래 위치(모든 행 포함)는 제외하고 다른 위젯과의 충돌만 확인
        val occupiedIndices = if (droppedItem is PositionedWidget) {
            // 드래그 중인 위젯을 제외한 점유된 셀들 (모든 행의 셀 포함)
            val occupiedByOthers = viewModel.getOccupiedCells(excluding = droppedItem)
            // 원래 위치의 모든 셀 인덱스 (모든 행 포함)를 명시적으로 제외 (부분 겹침 허용을 위해)
            val originalIndices = if (droppedItem.cellIndices.isNotEmpty()) {
                // cellIndices는 이미 모든 행의 셀 인덱스를 포함함
                droppedItem.cellIndices.toSet()
            } else {
                // 단일 셀인 경우
                droppedItem.cellIndex?.let { setOf(it) } ?: emptySet()
            }
            // 원래 위치의 모든 셀(모든 행 포함)을 제외하여 부분 겹침 이동 허용
            // 예: (1,1) (2,1)에서 (2,1) (3,1)로 이동 가능
            occupiedByOthers - originalIndices
        } else {
            viewModel.getOccupiedCells()
        }

        if (indices.any { it in occupiedIndices }) {
            return@DropTarget
        }

        dragInfo.itemDropped = true

        val (widgetWidthPx, widgetHeightPx) = widget.toPixels(density, selectedLayout)
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