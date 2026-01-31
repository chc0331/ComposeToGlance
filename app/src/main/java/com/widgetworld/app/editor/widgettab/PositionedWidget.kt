package com.widgetworld.app.editor.widgettab

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.getSizeInCells
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import com.widgetworld.widgetcomponent.proto.WidgetCategory


/**
 *
 * 0 1 2 3
 * 4 5 6 7
 *
 * gridIndex = 4
 * */
fun PlacedWidgetComponent.getCellIndices(): List<Int> {
    var indices = mutableListOf<Int>()

    Log.i("heec.choi","Index : $rowSpan $colSpan $gridIndex")
    (0 until rowSpan).forEach { row ->
        (0 until colSpan).forEach { colSpan ->
            val index = gridIndex + (row * colSpan)
            indices.add(index)
        }
    }

    return indices
}

private fun calculateSpansFromIndices(indices: List<Int>, gridColumns: Int): Pair<Int, Int> {
    if (indices.isEmpty()) return 1 to 1

    // 각 셀의 row와 col 계산
    val rows = indices.map { it / gridColumns }
    val cols = indices.map { it % gridColumns }

    // span = max - min + 1
    val rowSpan = (rows.maxOrNull() ?: 0) - (rows.minOrNull() ?: 0) + 1
    val colSpan = (cols.maxOrNull() ?: 0) - (cols.minOrNull() ?: 0) + 1

    return colSpan to rowSpan
}

/**
 * 위젯 사이즈 타입에 따른 실제 크기를 Dp 단위로 반환
 * @return Pair<width in dp, height in dp>
 */
fun WidgetComponent.getSizeInDp(layout: LayoutType?): DpSize {
    return getDpSizeByLayoutType(layout)
}

fun WidgetComponent.toPixels(density: Density, layout: LayoutType): Pair<Float, Float> {
    return with(density) {
        val (widthDp, heightDp) = getDpSizeByLayoutType(layout)
        widthDp.toPx() to heightDp.toPx()
    }
}

private fun WidgetComponent.getDpSizeByLayoutType(layout: LayoutType?): DpSize {
    if (layout == null) {
        return when (getSizeType()) {
            SizeType.TINY -> DpSize(90.dp, 90.dp)      // 1x1
            SizeType.SMALL -> DpSize(180.dp, 90.dp)    // 2x1
            SizeType.MEDIUM -> DpSize(180.dp, 180.dp)  // 2x2
            SizeType.MEDIUM_PLUS -> DpSize(270.dp, 180.dp)  // 3x2
            else -> DpSize(400.dp, 180.dp)             // 4x2 (LARGE)
        }
    }

    val gridSpec = layout.getGridCell()
    val rowCell = gridSpec?.row ?: 1
    val colCell = gridSpec?.column ?: 1
    val containerSize = layout.getDpSize()
    val cellWidth = (containerSize.width) / colCell
    val cellHeight = (containerSize.height) / rowCell

    // 레이아웃 타입과 그리드 배수를 고려한 동적 사이즈 계산
    val sizeInCells = this.getSizeInCellsForLayout(layout.name, layout.getDivide())
    val widthCells: Int = sizeInCells.first
    val heightCells: Int = sizeInCells.second

    return DpSize(
        (cellWidth * widthCells),
        (cellHeight * heightCells)
    )
}