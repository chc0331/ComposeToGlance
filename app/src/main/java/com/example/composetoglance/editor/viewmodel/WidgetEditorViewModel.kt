package com.example.composetoglance.editor.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.composetoglance.editor.layout.Layout
import com.example.composetoglance.editor.widget.Category
import com.example.composetoglance.editor.widget.PositionedWidget
import com.example.composetoglance.editor.widget.Widget

class WidgetEditorViewModel : ViewModel() {
    
    // 카테고리 목록
    val categories: List<Category> = listOf(
        Category("cat1", "카테고리 1"),
        Category("cat2", "카테고리 2"),
        Category("cat3", "카테고리 3")
    )
    
    // 위젯 목록
    val widgets = mutableStateListOf(
        Widget("위젯 1", "설명 1", "1x1", "cat1"),
        Widget("위젯 2", "설명 2", "2x1", "cat1"),
        Widget("위젯 3", "설명 3", "2x2", "cat2"),
        Widget("위젯 4", "설명 4", "1x1", "cat2"),
        Widget("위젯 5", "설명 5", "1x1", "cat3"),
        Widget("위젯 6", "설명 6", "2x1", "cat3")
    )
    
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
     * 위젯 추가
     */
    fun addWidget(widget: Widget) {
        widgets.add(widget)
    }
    
    /**
     * 위젯 제거
     */
    fun removeWidget(widget: Widget) {
        widgets.remove(widget)
    }
    
    /**
     * 위젯 업데이트
     */
    fun updateWidget(oldWidget: Widget, newWidget: Widget) {
        val index = widgets.indexOf(oldWidget)
        if (index != -1) {
            widgets[index] = newWidget
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
        widget: Widget,
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
    
    /**
     * 배치된 위젯 제거
     */
    fun removePositionedWidget(positionedWidget: PositionedWidget) {
        positionedWidgets.remove(positionedWidget)
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
    fun getOccupiedCells(): Set<Int> {
        return positionedWidgets.flatMap { positionedWidget ->
            if (positionedWidget.cellIndices.isNotEmpty()) {
                positionedWidget.cellIndices
            } else {
                listOfNotNull(positionedWidget.cellIndex)
            }
        }.toSet()
    }
    
    /**
     * 저장 기능 (나중에 구현)
     */
    fun save() {
        // TODO: 저장 기능 구현
    }
}

