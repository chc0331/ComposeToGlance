package com.example.composetoglance.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.example.composetoglance.ui.layout.LayoutGridSpec
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 레이아웃의 경계 정보를 담는 데이터 클래스
 */
data class LayoutBounds(val position: Offset, val size: IntSize)

/**
 * 그리드 셀 정보를 담는 데이터 클래스
 */
data class GridCell(val index: Int, val rect: Rect)

/**
 * 그리드 계산 유틸리티 클래스
 */
object GridCalculator {
    /**
     * 레이아웃 스펙과 경계를 기반으로 그리드 셀들을 계산
     */
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
