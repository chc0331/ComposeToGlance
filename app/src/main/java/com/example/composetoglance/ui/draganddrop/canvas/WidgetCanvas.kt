package com.example.composetoglance.ui.draganddrop.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.composetoglance.ui.draganddrop.DropTarget
import com.example.composetoglance.ui.draganddrop.LocalDragTargetInfo
import com.example.composetoglance.ui.draganddrop.layout.Layout
import com.example.composetoglance.ui.draganddrop.layout.LayoutComponent
import com.example.composetoglance.ui.draganddrop.layout.LayoutGridSpec
import com.example.composetoglance.ui.draganddrop.layout.gridSpec
import com.example.composetoglance.ui.draganddrop.widget.PositionedWidget
import com.example.composetoglance.ui.draganddrop.widget.Widget
import com.example.composetoglance.ui.draganddrop.widget.WidgetItem
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(selectedLayout: Layout?, modifier: Modifier = Modifier) {
    val positionedWidgets = remember { mutableStateListOf<PositionedWidget>() }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

    // When a new layout is selected, clear the existing widgets.
    LaunchedEffect(selectedLayout) {
        if (selectedLayout != null) {
            positionedWidgets.clear()
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                canvasPosition = it.positionInWindow()
            }
    ) {
        DropTarget(modifier = Modifier.fillMaxSize()) { isInBound, droppedItem ->
            if (isInBound && droppedItem != null && !dragInfo.itemDropped) {
                val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
                if (droppedItem is Widget) {
                    val widgetSizePx = with(density) { 50.dp.toPx() }
                    val targetCell = findTargetCell(
                        dropPositionInWindow = dropPositionInWindow,
                        selectedLayout = selectedLayout,
                        layoutBounds = layoutBounds,
                        positionedWidgets = positionedWidgets
                    )
                    if (targetCell != null) {
                        val cellCenter = targetCell.rect.center
                        val relativeCenter = cellCenter - canvasPosition
                        val adjustedOffset = Offset(
                            relativeCenter.x - widgetSizePx / 2,
                            relativeCenter.y - widgetSizePx / 2
                        )
                        positionedWidgets.add(
                            PositionedWidget(
                                widget = droppedItem,
                                offset = adjustedOffset,
                                cellIndex = targetCell.index
                            )
                        )
                        dragInfo.itemDropped = true
                    }
                }
            }
        }

        if (selectedLayout == null && positionedWidgets.isEmpty()) {
            Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
        } else {
            // Display selected layout in the center
            selectedLayout?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .onGloballyPositioned { layoutCoordinates ->
                            layoutBounds = LayoutBounds(
                                position = layoutCoordinates.positionInWindow(),
                                size = layoutCoordinates.size
                            )
                        }
                ) {
                    LayoutComponent(
                        type = it.type,
                        layoutType = it.sizeType,
                        shouldAnimate = false,
                        showText = false
                    )
                }
            }

            val gridCells = rememberGridCells(selectedLayout, layoutBounds)
            val occupiedCells = positionedWidgets.mapNotNull { it.cellIndex }.toSet()
            val hoveredCellIndex =
                if (dragInfo.isDragging && gridCells.isNotEmpty()) {
                    val pointer = dragInfo.dragPosition + dragInfo.dragOffset
                    gridCells.firstOrNull { cell ->
                        cell.rect.contains(pointer) && !occupiedCells.contains(cell.index)
                    }?.index
                } else {
                    null
                }

            if (gridCells.isNotEmpty() && dragInfo.isDragging) {
                gridCells.forEach { cell ->
                    val isOccupied = occupiedCells.contains(cell.index)
                    if (!isOccupied || hoveredCellIndex == cell.index) {
                        val offset = IntOffset(
                            (cell.rect.left - canvasPosition.x).roundToInt(),
                            (cell.rect.top - canvasPosition.y).roundToInt()
                        )
                        val widthDp = with(density) { cell.rect.width.toDp() }
                        val heightDp = with(density) { cell.rect.height.toDp() }
                        val color = if (hoveredCellIndex == cell.index) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        }
                        Box(
                            modifier = Modifier
                                .offset { offset }
                                .size(widthDp, heightDp)
                                .background(color)
                                .border(
                                    width = 1.dp,
                                    color = if (hoveredCellIndex == cell.index) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    }
                                )
                        )
                    }
                }
            }

            // Display dropped widgets
            positionedWidgets.forEach { item ->
                Box(
                    modifier = Modifier.offset {
                        IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                    }
                ) {
                    WidgetItem(data = item.widget, shouldAnimate = false)
                }
            }
        }
    }
}

private data class LayoutBounds(val position: Offset, val size: IntSize)
private data class GridCell(val index: Int, val rect: Rect)

@Composable
private fun rememberGridCells(selectedLayout: Layout?, layoutBounds: LayoutBounds?): List<GridCell> {
    return remember(selectedLayout, layoutBounds) {
        if (selectedLayout == null || layoutBounds == null) return@remember emptyList()
        val spec = selectedLayout.gridSpec() ?: return@remember emptyList()
        calculateGridCells(spec, layoutBounds)
    }
}

private fun calculateGridCells(spec: LayoutGridSpec, bounds: LayoutBounds): List<GridCell> {
    val cellWidth = bounds.size.width.toFloat() / spec.columns
    val cellHeight = bounds.size.height.toFloat() / spec.rows
    val cells = mutableListOf<GridCell>()
    var index = 0
    repeat(spec.rows) { row ->
        repeat(spec.columns) { column ->
            val left = bounds.position.x + column * cellWidth
            val top = bounds.position.y + row * cellHeight
            val rect = Rect(
                left = left,
                top = top,
                right = left + cellWidth,
                bottom = top + cellHeight
            )
            cells.add(GridCell(index = index, rect = rect))
            index++
        }
    }
    return cells
}

private fun findTargetCell(
    dropPositionInWindow: Offset,
    selectedLayout: Layout?,
    layoutBounds: LayoutBounds?,
    positionedWidgets: List<PositionedWidget>
): GridCell? {
    if (selectedLayout == null || layoutBounds == null) return null
    val spec = selectedLayout.gridSpec() ?: return null
    val gridCells = calculateGridCells(spec, layoutBounds)
    val occupied = positionedWidgets.mapNotNull { it.cellIndex }.toSet()
    return gridCells.firstOrNull { cell ->
        cell.rect.contains(dropPositionInWindow) && !occupied.contains(cell.index)
    }
}
