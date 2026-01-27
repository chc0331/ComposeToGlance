package com.widgetworld.app.editor.viewmodel

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.widgetworld.app.editor.settings.GridSettings
import com.widgetworld.app.editor.settings.GridSettingsDataStore
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.widgettab.PositionedWidget
import com.widgetworld.app.editor.widgettab.toPixels
import com.widgetworld.app.repository.WidgetCanvasStateRepository

import com.widgetworld.widgetcomponent.GridSpec
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import com.widgetworld.widgetcomponent.provider.ExtraLargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.LargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.MediumWidgetProvider
import com.widgetworld.widgetcomponent.repository.WidgetLayoutRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WidgetEditorViewModel @Inject constructor(
    private val widgetLayoutRepository: WidgetLayoutRepository,
    private val widgetCanvasStateRepository: WidgetCanvasStateRepository
) : ViewModel() {

    val selectedLayoutState = widgetCanvasStateRepository.dataStoreFlow.map { widgetCanvas ->
        val layoutSize = widgetCanvas.sizeType
        when (layoutSize) {
            com.widgetworld.widgetcomponent.proto.SizeType.SIZE_TYPE_MEDIUM -> LayoutType.Medium
            com.widgetworld.widgetcomponent.proto.SizeType.SIZE_TYPE_LARGE -> LayoutType.Large
            com.widgetworld.widgetcomponent.proto.SizeType.SIZE_TYPE_EXTRA_LARGE -> LayoutType.ExtraLarge
            else -> LayoutType.Large
        }
    }.stateIn(
        viewModelScope, started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LayoutType.Large
    )

    val positionedWidgetsState = widgetCanvasStateRepository.dataStoreFlow.map { widgetCanvas ->
        widgetCanvas.placedWidgetComponentList.map {
            val index = it.gridIndex
            val colSpan = it.colSpan
            val rowSpan = it.rowSpan
            val widgetCategory = it.widgetCategory
            val widgetTag = it.widgetTag
            val offsetX = it.offsetX
            val offsetY = it.offsetY
            PositionedWidget(
                gridIndex = index,
                offset = Offset(offsetX, offsetY),
                colSpan = colSpan,
                rowSpan = rowSpan,
                widgetCategory = widgetCategory,
                widgetTag = widgetTag
            )
        }
    }.stateIn(
        viewModelScope, started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun initializeGridSettings(context: Context) {
        viewModelScope.launch {
            // 초기 설정 로드
            val initialSettings = GridSettingsDataStore.loadSettings(context)
            _gridSettings.value = initialSettings

            // 현재 레이아웃에 그리드 배수 적용
            selectedLayout?.let { layout ->
                selectedLayout = layout.apply {
                    setDivide(initialSettings.globalMultiplier)
                }
            }

            // 설정 변경 감지
            GridSettingsDataStore.getSettingsFlow(context).collect { newSettings ->
                _gridSettings.value = newSettings

                // 레이아웃의 그리드 배수 업데이트
                selectedLayout?.let { layout ->
                    layout.setDivide(newSettings.globalMultiplier)
                    selectedLayout = layout
                }
            }
        }
    }

    val categories = WidgetCategory.entries.toList()
    val widgets = WidgetComponentRegistry.getAllComponents()

    // 그리드 설정 상태
    private val _gridSettings = MutableStateFlow(GridSettings.DEFAULT)
    val gridSettings: StateFlow<GridSettings> = _gridSettings.asStateFlow()

    var addedWidget by mutableStateOf<WidgetComponent?>(null)

    // 선택된 레이아웃 (기본값: Medium)
    var selectedLayout by mutableStateOf<LayoutType?>(LayoutType.Large)
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
     * 그리드 설정 업데이트 및 위젯 클리어
     * 그리드 배수가 변경될 때 호출됨
     */
    fun updateGridSettingsAndClearWidgets(settings: GridSettings) {
        // 그리드 설정 업데이트
        _gridSettings.value = settings

        // 레이아웃의 그리드 배수 업데이트
        selectedLayout?.let { layout ->
            layout.setDivide(settings.globalMultiplier)
            selectedLayout = layout
        }

        // 배치된 위젯들 클리어
        clearPositionedWidgets()
    }

    /**
     * 레이아웃 선택 (그리드 설정 고려)
     */
    fun selectLayout(layout: LayoutType?, migrateWidgets: Boolean = false) {
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
        cellIndices: List<Int>,
        spanX: Int = 0,
        spanY: Int = 0
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
        viewModelScope.launch {
            Log.i("heec.choi","addPositionedWidget-$widget $offset $startRow $startCol")
            val gridIndex = startCellIndex ?: 0
            val offsetX = offset.x
            val offsetY = offset.y
            val rowSpan = spanY
            val colSpan = spanX
            val widgetCategory = widget.getWidgetCategory().toProto()
            val widgetTag = widget.getWidgetTag()
            val placedWidgetComponent = PlacedWidgetComponent.newBuilder().setGridIndex(gridIndex)
                .setOffsetX(offsetX).setOffsetY(offsetY)
                .setRowSpan(rowSpan).setColSpan(colSpan).setWidgetCategory(widgetCategory).setWidgetTag(widgetTag).build()

            widgetCanvasStateRepository.addPlacedWidgets(placedWidgetComponent)
        }
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
        spec: GridSpec
    ): Pair<Int, Int>? {
        val currentLayout = selectedLayout ?: return null

        // 레이아웃 타입과 그리드 배수를 고려한 위젯 사이즈 계산
        val widgetSizeInCells = widget.getSizeInCellsForLayout(
            currentLayout.name,
            currentLayout.getDivide()
        )
        val widgetWidthCells = widgetSizeInCells.first
        val widgetHeightCells = widgetSizeInCells.second
        val occupiedCells = getOccupiedCells()

        // 모든 가능한 위치를 순회하면서 첫 번째 사용 가능한 위치 찾기
        for (row in 0 until spec.row) {
            for (col in 0 until spec.column) {
                // 위젯이 그리드 범위를 벗어나는지 확인
                if (row + widgetHeightCells > spec.row || col + widgetWidthCells > spec.column) {
                    continue
                }

                // 위젯이 차지할 셀 인덱스 계산
                val cellIndices = mutableListOf<Int>()
                for (r in row until row + widgetHeightCells) {
                    for (c in col until col + widgetWidthCells) {
                        val index = r * spec.column + c
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
    private fun migratePositionedWidgets(oldLayout: LayoutType, newLayout: LayoutType) {
        val oldSpec = oldLayout.getGridCell()
        val newSpec = newLayout.getGridCell()

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

    fun addWidgetToCanvas(
        density: Density,
        canvasPosition: Offset, layoutBounds: LayoutBounds,
        selectedLayout: LayoutType
    ) {
        addedWidget?.let { widget ->
            val gridSpec = selectedLayout.getGridCell()
            val (startRow, startCol) = findFirstAvailablePosition(widget, gridSpec) ?: (0 to 0)
            val widgetSizeInCells = widget.getSizeInCellsForLayout(
                selectedLayout.name,
                selectedLayout.getDivide()
            )
            val widgetWidthCells = widgetSizeInCells.first
            val widgetHeightCells = widgetSizeInCells.second
            val cellIndices = GridCalculator.calculateCellIndices(
                startRow, startCol,
                widgetWidthCells, widgetHeightCells, gridSpec
            )

            val (widgetWidthPx, widgetHeightPx) = widget.toPixels(density, selectedLayout)
            val adjustedOffset = GridCalculator.calculateWidgetOffset(
                startRow,
                startCol,
                widgetWidthCells,
                widgetHeightCells,
                widgetWidthPx,
                widgetHeightPx,
                layoutBounds, gridSpec, canvasPosition
            )

            addWidgetToFirstAvailablePosition(
                widget, adjustedOffset, startRow, startCol, cellIndices
            )
            addedWidget = null
        }
    }

    /**
     * 저장 기능
     */
    fun save(context: Context) {
        viewModelScope.launch {
            selectedLayout?.let { layout ->
                val layoutType = layout.name
                val gridColumns = layout.getGridCell().column
                val layoutSizeType = SizeType.getSizeType(layoutType).toProto()
                val positionedWidgets = positionedWidgets.map { it.toProto(gridColumns) }
                val provider = when (layoutType) {
                    "Medium" -> MediumWidgetProvider::class.java.name
                    "Large" -> LargeWidgetProvider::class.java.name
                    "Extra Large" -> ExtraLargeWidgetProvider::class.java.name
                    else -> LargeWidgetProvider::class.java.name
                }
                widgetLayoutRepository.updateData(
                    sizeType = layoutSizeType,
                    positionedWidgets = positionedWidgets
                )
                delay(200)
                AppWidgetManager.getInstance(context).requestPinAppWidget(
                    ComponentName(
                        context.packageName,
                        provider
                    ), null, null
                )
            }
        }
    }
}

