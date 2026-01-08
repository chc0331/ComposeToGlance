package com.widgetworld.widget.editor.canvas

import android.text.Layout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.widgetworld.widget.editor.util.GridCalculator
import com.widgetworld.widget.editor.util.GridCell
import com.widgetworld.widget.editor.util.LayoutBounds
import com.widgetworld.widget.editor.widget.PositionedWidget
import com.widgetworld.widgetcomponent.LayoutType

// PositionedWidget의 모든 셀 인덱스 반환
fun PositionedWidget.getAllCellIndices(): Set<Int> =
    if (cellIndices.isNotEmpty()) cellIndices.toSet() else listOfNotNull(cellIndex).toSet()


// 여러 PositionedWidget 리스트의 모든 셀 인덱스 반환
fun List<PositionedWidget>.getOccupiedCells(): Set<Int> =
    flatMap { it.getAllCellIndices() }.toSet()

// 그리드 셀 계산을 기억하는 Composable
@Composable
fun rememberGridCells(
    selectedLayout: LayoutType?,
    layoutBounds: LayoutBounds?
): List<GridCell> {
    return remember(selectedLayout, layoutBounds) {
        if (selectedLayout == null || layoutBounds == null) return@remember emptyList()
        val spec = selectedLayout.getGridCell() ?: return@remember emptyList()
        GridCalculator.calculateGridCells(spec, layoutBounds)
    }
}
