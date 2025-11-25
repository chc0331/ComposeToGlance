package com.example.composetoglance.editor.canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import com.example.composetoglance.editor.layout.Layout
import com.example.composetoglance.editor.layout.gridSpec
import com.example.composetoglance.editor.util.GridCalculator
import com.example.composetoglance.editor.util.GridCell
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.widget.PositionedWidget
import com.example.composetoglance.editor.widget.Widget
import com.example.composetoglance.editor.widget.getSizeInDp

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
