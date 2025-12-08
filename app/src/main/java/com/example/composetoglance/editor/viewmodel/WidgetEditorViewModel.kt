package com.example.composetoglance.editor.viewmodel

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetoglance.editor.widget.Layout
import com.example.composetoglance.editor.widget.LayoutGridSpec
import com.example.composetoglance.editor.widget.PositionedWidget
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.getSizeInCells
import com.example.widget.proto.SizeType
import com.example.widget.WidgetComponentRegistry
import com.example.widget.initializeWidgetComponents
import com.example.widget.provider.LargeWidgetProvider
import com.example.widget.repository.WidgetLayoutRepository
import kotlinx.coroutines.launch

class WidgetEditorViewModel(
    private val repository: WidgetLayoutRepository
) : ViewModel() {

    init {
        // 위젯 컴포넌트 초기화
        initializeWidgetComponents()
    }

    val categories = WidgetCategory.entries.toList()
    val widgets = WidgetComponentRegistry.getAllComponents()

    // 선택된 레이아웃
    var selectedLayout by mutableStateOf<Layout?>(null)
        private set

    // 캔버스에 배치된 위젯들 (셀 기반 위치 정보 포함)
    val positionedWidgets = mutableStateListOf<PositionedWidget>()

    /**
     * 레이아웃 선택
     */
    fun selectLayout(layout: Layout?) {
        selectedLayout = layout
        // 레이아웃이 변경되면 배치된 위젯들을 초기화
        if (layout != null) {
            clearPositionedWidgets()
        }
    }

    /**
     * 배치된 위젯들 초기화
     */
    fun clearPositionedWidgets() {
        positionedWidgets.clear()
    }

    /**
     * 위젯을 캔버스에 배치 (셀 정보 기반)
     * offset은 UI에서 계산하여 전달받음
     */
    fun addPositionedWidget(
        widget: WidgetComponent,
        offset: Offset,
        startRow: Int,
        startCol: Int,
        cellIndices: List<Int>
    ) {
        val startCellIndex = cellIndices.firstOrNull()
        positionedWidgets.add(
            PositionedWidget(
                widget = widget,
                offset = offset,
                cellIndex = startCellIndex,
                cellIndices = cellIndices
            )
        )
    }

    fun movePositionedWidget(
        positionedWidget: PositionedWidget,
        offset: Offset,
        startRow: Int,
        startCol: Int,
        cellIndices: List<Int>
    ) {
        // ID 기반으로 인덱스 찾기 (copy()로 인한 새 인스턴스 생성 문제 해결)
        val index = positionedWidgets.indexOfFirst { it.id == positionedWidget.id }
        if (index != -1) {
            // ID를 유지하면서 offset과 cellIndices만 업데이트
            val updatedWidget = positionedWidget.copy(
                offset = offset,
                cellIndex = cellIndices.firstOrNull(),
                cellIndices = cellIndices,
                id = positionedWidget.id // 기존 ID 명시적으로 유지
            )
            positionedWidgets[index] = updatedWidget
        }
    }

    /**
     * 배치된 위젯 제거 (ID 기반)
     */
    fun removePositionedWidget(positionedWidget: PositionedWidget) {
        val index = positionedWidgets.indexOfFirst { it.id == positionedWidget.id }
        if (index != -1) {
            positionedWidgets.removeAt(index)
        }
    }

    /**
     * 특정 셀 인덱스들에 위젯을 배치할 수 있는지 검사
     * @param cellIndices 배치하려는 셀 인덱스 리스트
     * @return 배치 가능하면 true, 충돌이 있으면 false
     */
    fun canPlaceWidget(cellIndices: List<Int>): Boolean {
        val occupiedCells = getOccupiedCells()
        return cellIndices.all { !occupiedCells.contains(it) }
    }

    /**
     * 현재 배치된 위젯들이 차지하는 셀 인덱스 집합을 반환
     */
    fun getOccupiedCells(excluding: PositionedWidget? = null): Set<Int> {
        return positionedWidgets
            .filter { it != excluding }
            .flatMap { positionedWidget ->
                if (positionedWidget.cellIndices.isNotEmpty()) {
                    positionedWidget.cellIndices
                } else {
                    listOfNotNull(positionedWidget.cellIndex)
                }
            }.toSet()
    }

    /**
     * 위젯을 배치할 수 있는 첫 번째 사용 가능한 위치를 찾음
     * @param widget 배치할 위젯
     * @param spec 레이아웃 그리드 스펙
     * @return Pair<startRow, startCol> 또는 null (배치할 수 없으면 null)
     */
    fun findFirstAvailablePosition(
        widget: WidgetComponent,
        spec: LayoutGridSpec
    ): Pair<Int, Int>? {
        val (widgetWidthCells, widgetHeightCells) = widget.getSizeInCells()
        val occupiedCells = getOccupiedCells()

        // 모든 가능한 위치를 순회하면서 첫 번째 사용 가능한 위치 찾기
        for (row in 0 until spec.rows) {
            for (col in 0 until spec.columns) {
                // 위젯이 그리드 범위를 벗어나는지 확인
                if (row + widgetHeightCells > spec.rows || col + widgetWidthCells > spec.columns) {
                    continue
                }

                // 위젯이 차지할 셀 인덱스 계산
                val cellIndices = mutableListOf<Int>()
                for (r in row until row + widgetHeightCells) {
                    for (c in col until col + widgetWidthCells) {
                        val index = r * spec.columns + c
                        cellIndices.add(index)
                    }
                }

                // 모든 셀이 사용 가능한지 확인
                if (cellIndices.all { !occupiedCells.contains(it) }) {
                    return row to col
                }
            }
        }

        return null
    }

    /**
     * 위젯을 첫 번째 사용 가능한 위치에 배치
     * @param widget 배치할 위젯
     * @param offset 위젯의 오프셋 (UI에서 계산하여 전달)
     * @param startRow 시작 행
     * @param startCol 시작 열
     * @param cellIndices 위젯이 차지하는 셀 인덱스 리스트
     */
    fun addWidgetToFirstAvailablePosition(
        widget: WidgetComponent,
        offset: Offset,
        startRow: Int,
        startCol: Int,
        cellIndices: List<Int>
    ) {
        addPositionedWidget(
            widget = widget,
            offset = offset,
            startRow = startRow,
            startCol = startCol,
            cellIndices = cellIndices
        )
    }

    /**
     * 저장 기능 (나중에 구현)
     */
    fun save(context: Context) {
        viewModelScope.launch {
            repository.updateData(
                sizeType = com.example.widget.SizeType.getSizeType(
                    selectedLayout?.sizeType ?: "Large"
                )?.toProto() ?: SizeType.SIZE_TYPE_LARGE,
                positionedWidgets = positionedWidgets.map {
                    it.toProto()
                }
            )
            AppWidgetManager.getInstance(context).requestPinAppWidget(
                ComponentName(
                    context.packageName,
                    LargeWidgetProvider::class.java.name
                ), null, null
            )
        }
    }
}

