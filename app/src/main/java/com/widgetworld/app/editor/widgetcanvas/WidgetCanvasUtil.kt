package com.widgetworld.app.editor.widgetcanvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.GridCell
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.widgetcomponent.LayoutType


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
