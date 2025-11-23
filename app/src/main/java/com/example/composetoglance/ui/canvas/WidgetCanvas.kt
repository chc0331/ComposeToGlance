package com.example.composetoglance.ui.canvas

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.composetoglance.ui.DropTarget
import com.example.composetoglance.ui.LocalDragTargetInfo
import com.example.composetoglance.ui.layout.Layout
import com.example.composetoglance.ui.layout.LayoutComponent
import com.example.composetoglance.ui.layout.LayoutGridSpec
import com.example.composetoglance.ui.layout.gridSpec
import com.example.composetoglance.ui.widget.PositionedWidget
import com.example.composetoglance.ui.widget.Widget
import com.example.composetoglance.ui.widget.WidgetItem
import com.example.composetoglance.ui.widget.getSizeInCells
import com.example.composetoglance.ui.widget.getSizeInDp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

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
                    val sizeInCells = droppedItem.getSizeInCells()
                    val widgetWidthCells = sizeInCells.first
                    val widgetHeightCells = sizeInCells.second
                    
                    val bounds = layoutBounds
                    val spec = selectedLayout?.gridSpec()
                    if (bounds != null && spec != null) {
                        val gridCells = calculateGridCells(spec, bounds)
                        val occupied = positionedWidgets.flatMap { 
                            if (it.cellIndices.isNotEmpty()) it.cellIndices else listOfNotNull(it.cellIndex)
                        }.toSet()
                        
                        val bestStart = calculateBestCellPosition(
                            dropPositionInWindow,
                            widgetWidthCells,
                            widgetHeightCells,
                            gridCells,
                            spec,
                            bounds
                        )
                        
                        if (bestStart != null) {
                            val (startRow, startCol) = bestStart
                            val indices = calculateCellIndices(
                                startRow,
                                startCol,
                                widgetWidthCells,
                                widgetHeightCells,
                                spec
                            )
                                
                                // 모든 셀이 비어있는지 확인
                                if (indices.all { !occupied.contains(it) }) {
                                    // 위젯의 실제 크기를 dp에서 픽셀로 변환
                                val (widgetWidthDp, widgetHeightDp) = droppedItem.getSizeInDp()
                                    val widgetWidthPx = with(density) { widgetWidthDp.toPx() }
                                    val widgetHeightPx = with(density) { widgetHeightDp.toPx() }
                                    
                                val adjustedOffset = calculateWidgetOffset(
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
                                    
                                    val startCellIndex = indices.first()
                        positionedWidgets.add(
                            PositionedWidget(
                                widget = droppedItem,
                                offset = adjustedOffset,
                                            cellIndex = startCellIndex,
                                            cellIndices = indices
                            )
                        )
                        dragInfo.itemDropped = true
                            }
                        }
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
            val occupiedCells = positionedWidgets.flatMap { 
                if (it.cellIndices.isNotEmpty()) it.cellIndices else listOfNotNull(it.cellIndex)
            }.toSet()
            
            // 드래그 중인 위젯의 사이즈 타입 확인
            val draggedWidget = dragInfo.dataToDrop as? Widget
            val draggedSizeInCells = draggedWidget?.getSizeInCells() ?: (1 to 1)
            val widgetWidthCells = draggedSizeInCells.first
            val widgetHeightCells = draggedSizeInCells.second
            
            val hoveredCellIndices = run {
                if (dragInfo.isDragging && gridCells.isNotEmpty() && draggedWidget != null && selectedLayout != null) {
                    val bounds = layoutBounds
                    if (bounds != null) {
                        val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
                        val spec = selectedLayout.gridSpec()
                        if (spec != null) {
                            val bestStart = calculateBestCellPosition(
                                dropPositionInWindow,
                                widgetWidthCells,
                                widgetHeightCells,
                                gridCells,
                                spec,
                                bounds
                            )
                                
                                if (bestStart != null) {
                                    val (startRow, startCol) = bestStart
                                val indices = calculateCellIndices(
                                    startRow,
                                    startCol,
                                    widgetWidthCells,
                                    widgetHeightCells,
                                    spec
                                )
                                    // 모든 셀이 비어있는지 확인
                                    if (indices.all { !occupiedCells.contains(it) }) {
                                        indices
                                } else {
                                    emptyList()
                                }
                            } else {
                                emptyList()
                            }
                        } else {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }

            // 드래그 중 위젯 미리보기 표시
            if (dragInfo.isDragging && hoveredCellIndices.isNotEmpty() && draggedWidget != null) {
                WidgetPreview(
                    draggedWidget = draggedWidget,
                    hoveredCellIndices = hoveredCellIndices,
                    widgetWidthCells = widgetWidthCells,
                    widgetHeightCells = widgetHeightCells,
                    layoutBounds = layoutBounds,
                    selectedLayout = selectedLayout,
                    canvasPosition = canvasPosition,
                    density = density
                )
            }
            
            // 그리드 셀 하이라이트 (보조 표시)
            if (gridCells.isNotEmpty() && dragInfo.isDragging) {
                GridCellHighlight(
                    gridCells = gridCells,
                    occupiedCells = occupiedCells,
                    hoveredCellIndices = hoveredCellIndices,
                    canvasPosition = canvasPosition,
                    density = density
                )
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

/**
 * 드롭 위치를 기반으로 위젯을 배치할 최적의 셀 위치를 계산
 * @return Pair<startRow, startCol> 또는 null
 */
private fun calculateBestCellPosition(
    dropPositionInWindow: Offset,
    widgetWidthCells: Int,
    widgetHeightCells: Int,
    gridCells: List<GridCell>,
    spec: LayoutGridSpec,
    bounds: LayoutBounds
): Pair<Int, Int>? {
    // 터치 위치가 포함된 셀 찾기
    val touchedCell = gridCells.firstOrNull { cell ->
        cell.rect.contains(dropPositionInWindow)
    } ?: return null
    
    val touchedRow = touchedCell.index / spec.columns
    val touchedCol = touchedCell.index % spec.columns
    
    // 위젯이 차지할 영역의 가능한 시작 위치들을 계산
    val possibleStarts = mutableListOf<Pair<Int, Int>>()
    
    // 터치 위치가 위젯 영역의 어느 부분에 있어도 배치 가능하도록
    for (rowOffset in 0 until widgetHeightCells) {
        for (colOffset in 0 until widgetWidthCells) {
            val startRow = touchedRow - rowOffset
            val startCol = touchedCol - colOffset
            
            if (startRow >= 0 && startCol >= 0) {
    val endRow = startRow + widgetHeightCells - 1
    val endCol = startCol + widgetWidthCells - 1
    
                if (endRow < spec.rows && endCol < spec.columns) {
                    possibleStarts.add(startRow to startCol)
                }
            }
        }
    }
    
    // 가장 가까운 시작 위치 선택
    return possibleStarts.minByOrNull { (row, col) ->
        val cellWidth = bounds.size.width.toFloat() / spec.columns
        val cellHeight = bounds.size.height.toFloat() / spec.rows
        val widgetAreaCenterX = (col + widgetWidthCells / 2f) * cellWidth + bounds.position.x
        val widgetAreaCenterY = (row + widgetHeightCells / 2f) * cellHeight + bounds.position.y
        val distance = sqrt(
            (dropPositionInWindow.x - widgetAreaCenterX).pow(2) +
            (dropPositionInWindow.y - widgetAreaCenterY).pow(2)
        )
        distance
    }
}

/**
 * 시작 위치를 기반으로 위젯이 차지하는 셀 인덱스 리스트를 계산
 */
private fun calculateCellIndices(
    startRow: Int,
    startCol: Int,
    widgetWidthCells: Int,
    widgetHeightCells: Int,
    spec: LayoutGridSpec
): List<Int> {
    val indices = mutableListOf<Int>()
    for (row in startRow until startRow + widgetHeightCells) {
        for (col in startCol until startCol + widgetWidthCells) {
            val index = row * spec.columns + col
            indices.add(index)
        }
    }
    return indices
}

/**
 * 위젯의 오프셋을 계산하여 셀 영역의 중심에 배치
 */
private fun calculateWidgetOffset(
    startRow: Int,
    startCol: Int,
    widgetWidthCells: Int,
    widgetHeightCells: Int,
    widgetWidthPx: Float,
    widgetHeightPx: Float,
    bounds: LayoutBounds,
    spec: LayoutGridSpec,
    canvasPosition: Offset
): Offset {
    val cellWidth = bounds.size.width.toFloat() / spec.columns
    val cellHeight = bounds.size.height.toFloat() / spec.rows
    
    // 위젯이 차지하는 셀 영역의 중심 계산
    val cellAreaLeft = bounds.position.x + startCol * cellWidth
    val cellAreaTop = bounds.position.y + startRow * cellHeight
    val cellAreaWidth = cellWidth * widgetWidthCells
    val cellAreaHeight = cellHeight * widgetHeightCells
    val cellAreaCenter = Offset(
        cellAreaLeft + cellAreaWidth / 2f,
        cellAreaTop + cellAreaHeight / 2f
    )
    
    // 셀 영역의 중심에서 위젯의 실제 크기의 절반을 빼서 정중앙 배치
    val relativeCenter = cellAreaCenter - canvasPosition
    return Offset(
        relativeCenter.x - widgetWidthPx / 2f,
        relativeCenter.y - widgetHeightPx / 2f
    )
}

/**
 * 드래그 중인 위젯의 미리보기를 표시하는 Composable
 */
@Composable
private fun WidgetPreview(
    draggedWidget: Widget,
    hoveredCellIndices: List<Int>,
    widgetWidthCells: Int,
    widgetHeightCells: Int,
    layoutBounds: LayoutBounds?,
    selectedLayout: Layout?,
    canvasPosition: Offset,
    density: androidx.compose.ui.unit.Density
) {
    val bounds = layoutBounds
    val spec = selectedLayout?.gridSpec()
    if (bounds != null && spec != null) {
        val startCellIndex = hoveredCellIndices.first()
        val startRow = startCellIndex / spec.columns
        val startCol = startCellIndex % spec.columns
        
        // 위젯의 실제 크기를 dp에서 픽셀로 변환
        val (widgetWidthDp, widgetHeightDp) = draggedWidget.getSizeInDp()
        val widgetWidthPx = with(density) { widgetWidthDp.toPx() }
        val widgetHeightPx = with(density) { widgetHeightDp.toPx() }
        
        val previewOffset = calculateWidgetOffset(
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
        
        // 반투명 위젯 미리보기 표시
        Box(
            modifier = Modifier.offset {
                IntOffset(previewOffset.x.roundToInt(), previewOffset.y.roundToInt())
            }
        ) {
            WidgetItem(
                data = draggedWidget,
                shouldAnimate = false,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}

/**
 * 그리드 셀 하이라이트를 표시하는 Composable
 */
@Composable
private fun GridCellHighlight(
    gridCells: List<GridCell>,
    occupiedCells: Set<Int>,
    hoveredCellIndices: List<Int>,
    canvasPosition: Offset,
    density: androidx.compose.ui.unit.Density
) {
    gridCells.forEach { cell ->
        val isOccupied = occupiedCells.contains(cell.index)
        val isHovered = hoveredCellIndices.contains(cell.index)
        if (isHovered) {
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
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
            )
        } else if (!isOccupied) {
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
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
            )
        }
    }
}
