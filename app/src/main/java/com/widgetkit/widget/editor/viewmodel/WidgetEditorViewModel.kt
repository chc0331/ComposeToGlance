package com.widgetkit.widget.editor.viewmodel

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
import com.widgetkit.widget.editor.widget.Layout
import com.widgetkit.widget.editor.widget.LayoutGridSpec
import com.widgetkit.widget.editor.widget.PositionedWidget
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.getSizeInCells
import com.widgetkit.core.getSizeInCellsForLayout
import com.widgetkit.core.proto.SizeType
import com.widgetkit.core.WidgetComponentRegistry
import com.widgetkit.core.provider.LargeWidgetProvider
import com.widgetkit.core.repository.WidgetLayoutRepository
import com.widgetkit.widget.editor.settings.GridSettings
import com.widgetkit.widget.editor.settings.GridSettingsDataStore
import com.widgetkit.widget.editor.util.GridCalculator
import com.widgetkit.widget.editor.widget.gridSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WidgetEditorViewModel(
    private val repository: WidgetLayoutRepository
) : ViewModel() {

    init {
        // 위젯 컴포넌트 초기화는 MainActivity에서 수행됨
        // (Lifecycle 관리를 위해 Application Context가 필요)
    }
    
    /**
     * 그리드 설정 초기화 및 변경 감지
     */
    fun initializeGridSettings(context: Context) {
        viewModelScope.launch {
            // 초기 설정 로드
            val initialSettings = GridSettingsDataStore.loadSettings(context)
            _gridSettings.value = initialSettings
            
            // 현재 레이아웃에 그리드 배수 적용
            selectedLayout?.let { layout ->
                selectedLayout = layout.copy(gridMultiplier = initialSettings.globalMultiplier)
            }
            
            // 설정 변경 감지
            GridSettingsDataStore.getSettingsFlow(context).collect { newSettings ->
                _gridSettings.value = newSettings
                
                // 레이아웃의 그리드 배수 업데이트
                selectedLayout?.let { layout ->
                    val updatedLayout = layout.copy(gridMultiplier = newSettings.globalMultiplier)
                    selectedLayout = updatedLayout
                }
            }
        }
    }

    val categories = WidgetCategory.entries.toList()
    val widgets = WidgetComponentRegistry.getAllComponents()

    // 그리드 설정 상태
    private val _gridSettings = MutableStateFlow(GridSettings.DEFAULT)
    val gridSettings: StateFlow<GridSettings> = _gridSettings.asStateFlow()

    // 선택된 레이아웃 (기본값: Medium)
    var selectedLayout by mutableStateOf<Layout?>(Layout("Medium"))
        private set

    // 캔버스에 배치된 위젯들 (셀 기반 위치 정보 포함)
    val positionedWidgets = mutableStateListOf<PositionedWidget>()
    
    // 그리드 설정 패널 표시 상태
    var showGridSettings by mutableStateOf(false)
        private set

    
    /**
     * 그리드 설정 패널 표시/숨김
     */
    fun showGridSettingsPanel() {
        showGridSettings = true
    }
    
    fun hideGridSettingsPanel() {
        showGridSettings = false
    }
    
    /**
     * 레이아웃 선택 (그리드 설정 고려)
     */
    fun selectLayout(layout: Layout?, migrateWidgets: Boolean = false) {
        val previousLayout = selectedLayout
        selectedLayout = layout
        
        if (layout != null) {
            if (migrateWidgets && previousLayout != null) {
                // 기존 위젯들을 새로운 그리드에 맞게 마이그레이션
                migratePositionedWidgets(previousLayout, layout)
            } else {
                // 레이아웃이 변경되면 배치된 위젯들을 초기화
                clearPositionedWidgets()
            }
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
            .filter { 
                // ID 기반 비교로 더 안전하게 제외 (참조 비교와 ID 비교 모두 지원)
                excluding == null || it.id != excluding.id
            }
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
        val currentLayout = selectedLayout ?: return null
        
        // 레이아웃 타입과 그리드 배수를 고려한 위젯 사이즈 계산
        val widgetSizeInCells = widget.getSizeInCellsForLayout(
            currentLayout.sizeType, 
            currentLayout.gridMultiplier
        )
        val widgetWidthCells = widgetSizeInCells.first
        val widgetHeightCells = widgetSizeInCells.second
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
     * 기존 위젯들을 새로운 그리드에 맞게 마이그레이션
     */
    private fun migratePositionedWidgets(oldLayout: Layout, newLayout: Layout) {
        val oldSpec = oldLayout.gridSpec() ?: return
        val newSpec = newLayout.gridSpec() ?: return
        
        val migratedWidgets = positionedWidgets.mapNotNull { positionedWidget ->
            val oldIndices = positionedWidget.cellIndices
            if (oldIndices.isEmpty()) return@mapNotNull null
            
            // 셀 인덱스 마이그레이션
            val newIndices = GridCalculator.migrateCellIndices(oldIndices, oldSpec, newSpec)
            if (newIndices.isEmpty()) return@mapNotNull null
            
            // 새로운 위치 계산 (첫 번째 셀 인덱스 기준)
            val firstIndex = newIndices.first()
            
            // 새로운 오프셋 계산은 UI에서 수행되므로 임시값 사용
            positionedWidget.copy(
                cellIndices = newIndices,
                cellIndex = firstIndex,
                offset = Offset.Zero // UI에서 재계산됨
            )
        }
        
        positionedWidgets.clear()
        positionedWidgets.addAll(migratedWidgets)
    }
    
    
    /**
     * 저장 기능 (나중에 구현)
     */
    fun save(context: Context) {
        viewModelScope.launch {
            val gridColumns = selectedLayout?.gridSpec()?.columns ?: 2
            repository.updateData(
                sizeType = com.widgetkit.core.SizeType.getSizeType(
                    selectedLayout?.sizeType ?: "Large"
                )?.toProto() ?: SizeType.SIZE_TYPE_LARGE,
                positionedWidgets = positionedWidgets.map {
                    it.toProto(gridColumns)
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

