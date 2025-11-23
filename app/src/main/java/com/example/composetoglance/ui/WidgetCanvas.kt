package com.example.composetoglance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.composetoglance.ui.draganddrop.DragTargetInfo
import com.example.composetoglance.ui.draganddrop.DropTarget
import com.example.composetoglance.ui.draganddrop.LocalDragTargetInfo
import com.example.composetoglance.ui.layout.Layout
import com.example.composetoglance.ui.layout.LayoutComponent
import com.example.composetoglance.ui.layout.LayoutGridSpec
import com.example.composetoglance.ui.layout.gridSpec
import com.example.composetoglance.ui.widget.PositionedWidget
import com.example.composetoglance.ui.widget.Widget
import com.example.composetoglance.ui.widget.WidgetItem
import com.example.composetoglance.ui.widget.getSizeInCells
import com.example.composetoglance.ui.widget.getSizeInDp
import com.example.composetoglance.viewmodel.WidgetEditorViewModel
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

// 상수 정의
private object WidgetCanvasConstants {
    const val PREVIEW_ALPHA = 0.6f
    const val HOVERED_CELL_BACKGROUND_ALPHA = 0.2f
    const val EMPTY_CELL_BACKGROUND_ALPHA = 0.05f
    const val EMPTY_CELL_BORDER_ALPHA = 0.3f
    const val HOVERED_CELL_BORDER_WIDTH_DP = 2
    const val EMPTY_CELL_BORDER_WIDTH_DP = 1
}

// 데이터 클래스
private data class LayoutBounds(val position: Offset, val size: IntSize)
private data class GridCell(val index: Int, val rect: Rect)

// PositionedWidget 확장 함수
private fun PositionedWidget.getAllCellIndices(): Set<Int> {
    return if (cellIndices.isNotEmpty()) {
        cellIndices.toSet()
    } else {
        listOfNotNull(cellIndex).toSet()
    }
}

// 그리드 계산 유틸리티 클래스
private object GridCalculator {
    fun calculateGridCells(spec: LayoutGridSpec, bounds: LayoutBounds): List<GridCell> {
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
    fun calculateBestCellPosition(
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
    fun calculateCellIndices(
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
    fun calculateWidgetOffset(
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
}

// 위젯 크기 변환 헬퍼 함수
private fun Widget.toPixels(density: Density): Pair<Float, Float> {
    val (widthDp, heightDp) = getSizeInDp()
    return with(density) {
        widthDp.toPx() to heightDp.toPx()
    }
}

// occupiedCells 계산 헬퍼 함수 (UI에서만 사용, ViewModel의 메서드 사용 권장)
private fun List<PositionedWidget>.getOccupiedCells(): Set<Int> {
    return flatMap { it.getAllCellIndices() }.toSet()
}

@Composable
fun WidgetCanvas(
    viewModel: WidgetEditorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLayout = viewModel.selectedLayout
    val positionedWidgets = viewModel.positionedWidgets
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

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
            positionedWidgets.forEach { item ->
                Box(
                    modifier = Modifier.offset {
                        IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                    }
                ) {
                    WidgetItem(data = item.widget)
                }
            }
        }
    }
}

@Composable
private fun rememberGridCells(
    selectedLayout: Layout?,
    layoutBounds: LayoutBounds?
): List<GridCell> {
    return remember(selectedLayout, layoutBounds) {
        if (selectedLayout == null || layoutBounds == null) return@remember emptyList()
        val spec = selectedLayout.gridSpec() ?: return@remember emptyList()
        GridCalculator.calculateGridCells(spec, layoutBounds)
    }
}

@Composable
private fun BoxScope.LayoutDisplay(
    selectedLayout: Layout?,
    onLayoutBoundsChanged: (LayoutBounds) -> Unit
) {
    selectedLayout?.let { layout ->
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned { layoutCoordinates ->
                    onLayoutBoundsChanged(
                        LayoutBounds(
                            position = layoutCoordinates.positionInWindow(),
                            size = layoutCoordinates.size
                        )
                    )
                }
        ) {
            LayoutComponent(
                type = layout.type,
                layoutType = layout.sizeType,
                shouldAnimate = false,
                showText = false
            )
        }
    }
}

@Composable
private fun WidgetDropHandler(
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

        // 위젯의 실제 크기를 dp에서 픽셀로 변환
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

        // ViewModel에 위젯 추가 (비즈니스 로직)
        viewModel.addPositionedWidget(
            widget = widget,
            offset = adjustedOffset,
            startRow = startRow,
            startCol = startCol,
            cellIndices = indices
        )
        dragInfo.itemDropped = true
    }
}

@Composable
private fun DragStateOverlay(
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

    // 드래그 중 위젯 미리보기 표시
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

    // 그리드 셀 하이라이트 (보조 표시)
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

        // ViewModel에서 셀 충돌 검사
        if (viewModel.canPlaceWidget(indices)) {
            indices
        } else {
            emptyList()
        }
    }
}

/**
 * 드래그 중인 위젯의 미리보기를 표시하는 Composable
 */
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

        // 반투명 위젯 미리보기 표시
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

/**
 * 그리드 셀 하이라이트를 표시하는 Composable
 */
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
                modifier = Modifier
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
                modifier = Modifier
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
