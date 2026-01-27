package com.widgetworld.app.editor.widgetcanvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.view.HapticFeedbackConstants
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.widgetworld.app.editor.draganddrop.DragTargetInfo
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.GridCell
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.viewmodel.WidgetEditorViewModel
import com.widgetworld.app.editor.widgettab.PositionedWidget
import com.widgetworld.app.editor.widgettab.WidgetComponent
import com.widgetworld.app.editor.widgettab.toPixels
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.getSizeInCells
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import kotlin.math.roundToInt

@Composable
fun DragStateOverlay(
    viewModel: WidgetEditorViewModel,
    gridCells: List<GridCell>,
    occupiedCells: Set<Int>,
    selectedLayout: LayoutType?,
    layoutBounds: LayoutBounds?,
    canvasPosition: Offset,
    density: Density,
    dragInfo: DragTargetInfo
) {
    if (!dragInfo.isDragging || gridCells.isEmpty()) {
        return
    }

    val (draggedWidget, draggedPositionedWidget) = when (val item = dragInfo.dataToDrop) {
        is WidgetComponent -> item to null
        is PositionedWidget -> item.widget to item
        else -> return
    }

    val hoveredCellIndices = rememberHoveredCellIndices(
        viewModel = viewModel,
        dragInfo = dragInfo,
        draggedWidget = draggedWidget!!,
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

//    if (hoveredCellIndices.isNotEmpty()) {
//        WidgetPreview(
//            draggedWidget = draggedWidget,
//            hoveredCellIndices = hoveredCellIndices,
//            layoutBounds = layoutBounds,
//            selectedLayout = selectedLayout,
//            canvasPosition = canvasPosition,
//            density = density
//        )
//    }

    val currentOccupiedCells =
        remember(draggedPositionedWidget?.id, occupiedCells, viewModel.positionedWidgets) {
            derivedStateOf {
                if (draggedPositionedWidget != null) {
                    viewModel.getOccupiedCells(excluding = draggedPositionedWidget)
                } else {
                    occupiedCells
                }
            }
        }.value

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
    draggedWidget: WidgetComponent,
    draggedPositionedWidget: PositionedWidget?,
    gridCells: List<GridCell>,
    selectedLayout: LayoutType?,
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

        val spec = selectedLayout.getGridCell() ?: return@remember emptyList()
        val currentLayout = selectedLayout ?: return@remember emptyList()
        val widgetSizeInCells = draggedWidget.getSizeInCellsForLayout(
            currentLayout.name,
            currentLayout.getDivide()
        )
        val widgetWidthCells = widgetSizeInCells.first
        val widgetHeightCells = widgetSizeInCells.second
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

        // remember 블록 안에서는 직접 계산만 수행
        // 충돌 검사: 드래그 중인 위젯의 원래 위치(모든 행 포함)는 제외하고 다른 위젯과의 충돌만 확인
        val occupied = if (draggedPositionedWidget != null) {
            // 드래그 중인 위젯을 제외한 점유된 셀들 (모든 행의 셀 포함)
            val occupiedByOthers = viewModel.getOccupiedCells(excluding = draggedPositionedWidget)
            // 원래 위치의 모든 셀 인덱스 (모든 행 포함)를 명시적으로 제외 (부분 겹침 허용을 위해)
            val originalIndices = if (draggedPositionedWidget.cellIndices.isNotEmpty()) {
                // cellIndices는 이미 모든 행의 셀 인덱스를 포함함
                draggedPositionedWidget.cellIndices.toSet()
            } else {
                // 단일 셀인 경우
                draggedPositionedWidget.cellIndex?.let { setOf(it) } ?: emptySet()
            }
            // 원래 위치의 모든 셀(모든 행 포함)을 제외하여 부분 겹침 이동 허용
            // 예: (1,1) (2,1)에서 (2,1) (3,1)로 이동 가능, 또는 2행 이상 위젯도 동일하게 적용
            occupiedByOthers - originalIndices
        } else {
            viewModel.getOccupiedCells()
        }
        if (indices.any { it in occupied }) emptyList() else indices
    }
}

@Composable
private fun WidgetPreview(
    draggedWidget: WidgetComponent,
    hoveredCellIndices: List<Int>,
    layoutBounds: LayoutBounds?,
    selectedLayout: LayoutType?,
    canvasPosition: Offset,
    density: Density
) {
    val bounds = layoutBounds ?: return
    val spec = selectedLayout?.getGridCell() ?: return
    val startCellIndex = hoveredCellIndices.first()
    val startRow = startCellIndex / spec.column
    val startCol = startCellIndex % spec.column
    val (widgetWidthCells, widgetHeightCells) = draggedWidget.getSizeInCells()
    val (widgetWidthPx, widgetHeightPx) = draggedWidget.toPixels(density, selectedLayout)

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
        WidgetComponent(
            data = draggedWidget,
            modifier = Modifier.alpha(0.6f)
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
    // hoveredCellIndices를 Set으로 변환하여 O(1) 조회 최적화
    val hoveredCellSet = remember(hoveredCellIndices) {
        hoveredCellIndices.toSet()
    }

    // 호버된 셀들이 있으면 전체 영역을 하나로 그리기
    if (hoveredCellIndices.isNotEmpty()) {
        val hoveredCells = gridCells.filter { it.index in hoveredCellSet }
        val isOccupied = hoveredCells.any { it.index in occupiedCells }
        HighlightedArea(
            cells = hoveredCells,
            canvasPosition = canvasPosition,
            density = density,
            isOccupied = isOccupied
        )
    }
}

@Composable
private fun HighlightedArea(
    cells: List<GridCell>,
    canvasPosition: Offset,
    density: Density,
    isOccupied: Boolean
) {
    if (cells.isEmpty()) return

    val context = LocalContext.current
    val cornerRadius = context.getSystemBackgroundRadius()

    // 전체 영역의 bounds 계산
    val minLeft = cells.minOf { it.rect.left }
    val minTop = cells.minOf { it.rect.top }
    val maxRight = cells.maxOf { it.rect.right }
    val maxBottom = cells.maxOf { it.rect.bottom }

    val offset = IntOffset(
        (minLeft - canvasPosition.x).roundToInt(),
        (minTop - canvasPosition.y).roundToInt()
    )
    val widthDp = with(density) { (maxRight - minLeft).toDp() }
    val heightDp = with(density) { (maxBottom - minTop).toDp() }

    val backgroundColor = if (isOccupied) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
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
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
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
                    alpha = 0.05f
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.3f
                )
            )
    )
}