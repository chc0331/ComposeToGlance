package com.widgetkit.widget.editor.canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.widgetkit.widget.editor.widget.Layout
import com.widgetkit.widget.editor.widget.gridSpec
import com.widgetkit.widget.editor.util.GridCalculator
import com.widgetkit.widget.editor.util.GridCell
import com.widgetkit.widget.editor.util.LayoutBounds
import com.widgetkit.widget.editor.widget.PositionedWidget

// PositionedWidget의 모든 셀 인덱스 반환
fun PositionedWidget.getAllCellIndices(): Set<Int> =
    if (cellIndices.isNotEmpty()) cellIndices.toSet() else listOfNotNull(cellIndex).toSet()


// 여러 PositionedWidget 리스트의 모든 셀 인덱스 반환
fun List<PositionedWidget>.getOccupiedCells(): Set<Int> =
    flatMap { it.getAllCellIndices() }.toSet()

// 그리드 셀 계산을 기억하는 Composable
@Composable
fun rememberGridCells(
    selectedLayout: Layout?,
    layoutBounds: LayoutBounds?
): List<GridCell> {
    return remember(selectedLayout, layoutBounds) {
        if (selectedLayout == null || layoutBounds == null) return@remember emptyList()
        val spec = selectedLayout.gridSpec() ?: return@remember emptyList()
        GridCalculator.calculateGridCells(spec, layoutBounds)
    }
}
