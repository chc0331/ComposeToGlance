package com.widgetworld.app.editor.widgetcanvas

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetworld.app.editor.PlacedWidgetIdGenerator
import com.widgetworld.app.editor.widgettab.getCellIndices
import com.widgetworld.app.repository.WidgetCanvasStateRepository
import com.widgetworld.widgetcomponent.GridSpec
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetDropViewModel @Inject constructor(
    private val widgetCanvasStateRepository: WidgetCanvasStateRepository,
    private val idGenerator: PlacedWidgetIdGenerator
) : ViewModel() {

    val placedWidgetsState = widgetCanvasStateRepository.dataStoreFlow.map { widgetCanvas ->
        widgetCanvas.placedWidgetComponentList
    }.stateIn(
        viewModelScope, started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

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
        viewModelScope.launch {
            val gridIndex = startCellIndex ?: 0
            val offsetX = offset.x
            val offsetY = offset.y
            val rowSpan = spanY
            val colSpan = spanX
            val widgetCategory = widget.getWidgetCategory().toProto()
            val widgetTag = widget.getWidgetTag()
            val placedWidgetComponent = PlacedWidgetComponent.newBuilder()
                .setId(idGenerator.generateId())
                .setGridIndex(gridIndex)
                .setOffsetX(offsetX).setOffsetY(offsetY)
                .setRowSpan(rowSpan).setColSpan(colSpan).setWidgetCategory(widgetCategory)
                .addAllOccupiedGridIndices(cellIndices)
                .setWidgetTag(widgetTag).build()

            widgetCanvasStateRepository.addPlacedWidgets(placedWidgetComponent)
        }
    }

    fun movePositionedWidget(
        placedWidget: PlacedWidgetComponent,
        offset: Offset,
        startRow: Int,
        startCol: Int,
        cellIndices: List<Int>
    ) {
        viewModelScope.launch {
            val startCellIndex = cellIndices.firstOrNull() ?: 0
            val newPlacedWidget = placedWidget.toBuilder()
                .setId(placedWidget.id) // ID 보존
                .setGridIndex(startCellIndex)
                .clearOccupiedGridIndices()
                .addAllOccupiedGridIndices(cellIndices)
                .setOffsetX(offset.x)
                .setOffsetY(offset.y)
                .build()

            widgetCanvasStateRepository.updatePlacedWidget(newPlacedWidget)
        }
    }

    fun removePositionedWidget(placedWidget: PlacedWidgetComponent) {
        viewModelScope.launch {
            widgetCanvasStateRepository.removePlacedWidget(placedWidget)
        }
    }

    /**
     * 현재 배치된 위젯들이 차지하는 셀 인덱스 집합을 반환
     */
    fun getOccupiedCells(excluding: PlacedWidgetComponent? = null, gridSpec: GridSpec): Set<Int> {
        return placedWidgetsState.value.filter {
            excluding == null || it.id != excluding.id
        }.flatMap { widget ->
            widget.getCellIndices(gridSpec.column)
        }.toSet()
    }
}