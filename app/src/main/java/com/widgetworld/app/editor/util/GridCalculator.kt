package com.widgetworld.app.editor.util

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.widgetworld.app.editor.settings.GridSettings
import com.widgetworld.widgetcomponent.GridSpec
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
 * 2N 배수 기반 동적 그리드 계산 지원
 */
object GridCalculator {
    /**
     * 레이아웃 스펙과 경계를 기반으로 그리드 셀들을 계산
     */
    fun calculateGridCells(spec: GridSpec, bounds: LayoutBounds): List<GridCell> {
        val cellWidth = bounds.size.width.toFloat() / spec.column
        val cellHeight = bounds.size.height.toFloat() / spec.row
        val cells = mutableListOf<GridCell>()
        var index = 0
        repeat(spec.row) { row ->
            repeat(spec.column) { column ->
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
     * 동적 그리드 스펙 계산 (기본 그리드 × 2N 배수)
     */
    fun calculateDynamicGridSpec(
        baseRows: Int,
        baseColumns: Int,
        multiplier: Int
    ): GridSpec {
        val validMultiplier = if (GridSettings.isValidMultiplier(multiplier)) multiplier else 1
        return GridSpec(
            row = baseRows * validMultiplier,
            column = baseColumns * validMultiplier
        )
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
        spec: GridSpec,
        bounds: LayoutBounds
    ): Pair<Int, Int>? {
        // 터치 위치가 포함된 셀 찾기
        val touchedCell = gridCells.firstOrNull { cell ->
            cell.rect.contains(dropPositionInWindow)
        } ?: return null

        val touchedRow = touchedCell.index / spec.column
        val touchedCol = touchedCell.index % spec.column

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

                    if (endRow < spec.row && endCol < spec.column) {
                        possibleStarts.add(startRow to startCol)
                    }
                }
            }
        }

        // 가장 가까운 시작 위치 선택
        return possibleStarts.minByOrNull { (row, col) ->
            val cellWidth = bounds.size.width.toFloat() / spec.column
            val cellHeight = bounds.size.height.toFloat() / spec.row
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
        spec: GridSpec
    ): List<Int> {
        val indices = mutableListOf<Int>()
        for (row in startRow until startRow + widgetHeightCells) {
            for (col in startCol until startCol + widgetWidthCells) {
                val index = row * spec.column + col
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
        spec: GridSpec,
        canvasPosition: Offset
    ): Offset {
        val cellWidth = bounds.size.width.toFloat() / spec.column
        val cellHeight = bounds.size.height.toFloat() / spec.row

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
     * 동적 그리드에서 위젯 사이즈를 2N 배수에 맞게 조정
     */
    fun calculateScaledWidgetSize(
        baseWidthCells: Int,
        baseHeightCells: Int,
        gridMultiplier: Int
    ): Pair<Int, Int> {
        val validMultiplier =
            if (GridSettings.isValidMultiplier(gridMultiplier)) gridMultiplier else 1
        return Pair(
            baseWidthCells * validMultiplier,
            baseHeightCells * validMultiplier
        )
    }

    /**
     * 그리드 배수 변경 시 기존 셀 인덱스를 새로운 그리드에 맞게 변환
     */
    fun migrateCellIndices(
        oldIndices: List<Int>,
        oldSpec: GridSpec,
        newSpec: GridSpec
    ): List<Int> {
        if (oldSpec.row == newSpec.row && oldSpec.column == newSpec.column) {
            return oldIndices // 변경사항 없음
        }

        val newIndices = mutableListOf<Int>()

        for (oldIndex in oldIndices) {
            // 기존 인덱스를 2D 좌표로 변환
            val oldRow = oldIndex / oldSpec.column
            val oldCol = oldIndex % oldSpec.column

            // 비율을 유지하여 새로운 그리드에서의 좌표 계산
            val newRow = (oldRow * newSpec.row) / oldSpec.row
            val newCol = (oldCol * newSpec.column) / oldSpec.column

            // 새로운 인덱스 계산
            val newIndex = newRow * newSpec.column + newCol

            // 유효한 범위 내에서만 추가
            if (newIndex < newSpec.row * newSpec.column) {
                newIndices.add(newIndex)
            }
        }

        return newIndices.distinct().sorted()
    }
}
