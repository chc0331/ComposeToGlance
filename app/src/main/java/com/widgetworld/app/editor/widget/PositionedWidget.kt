package com.widgetworld.app.editor.widget

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

data class PositionedWidget(
    val widget: WidgetComponent,
    val offset: Offset,
    val cellIndex: Int? = null,
    val cellIndices: List<Int> = emptyList(), // 여러 셀을 차지하는 경우
    val id: String = java.util.UUID.randomUUID().toString() // 고유 ID for stable key
) {
    /**
     * Proto로 변환 시 실제 배치된 셀 정보를 기반으로 row_span과 col_span 계산
     * @param gridColumns 현재 그리드의 열 수 (cellIndices를 row/col로 변환하기 위해 필요)
     */
    fun toProto(gridColumns: Int): PlacedWidgetComponent {
        // cellIndices가 있으면 실제 배치된 셀 정보를 기반으로 span 계산
        val (colSpan, rowSpan) = if (cellIndices.isNotEmpty() && gridColumns > 0) {
            calculateSpansFromIndices(cellIndices, gridColumns)
        } else {
            // fallback: 기본 1x 사이즈 사용
            widget.getSizeInCells()
        }

        return PlacedWidgetComponent.newBuilder()
            .setGridIndex((cellIndex?.plus(1)) ?: 1)
            .setRowSpan(rowSpan)
            .setColSpan(colSpan)
            .setWidgetTag(widget.getWidgetTag())
            .setWidgetCategory(widget.getWidgetCategory().toProto())
            .build()
    }

    /**
     * cellIndices로부터 실제 row_span과 col_span 계산
     */
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