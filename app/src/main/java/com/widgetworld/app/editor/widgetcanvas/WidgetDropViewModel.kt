package com.widgetworld.app.editor.widgetcanvas

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetworld.app.editor.widgettab.PositionedWidget
import com.widgetworld.app.repository.WidgetCanvasStateRepository
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.first

@HiltViewModel
class WidgetDropViewModel @Inject constructor(
    private val widgetCanvasStateRepository: WidgetCanvasStateRepository
) : ViewModel() {

//    fun onDrop(result: DropResult) {
//        when (result) {
//            is DropResult.Add -> addPositionedWidget(
//                result.widget,
//                result.offset,
//                result.startRow,
//                result.startCol,
//                result.cellIndices,
//                result.widthCells,
//                result.heightCells
//            )
//
//            is DropResult.Move -> movePositionedWidget(
//                result.positionedWidget,
//                result.offset,
//                result.startRow,
//                result.startCol,
//                result.cellIndices
//            )
//
//            is DropResult.Remove -> removePositionedWidget(result.widget)
//
//            DropResult.Ignore -> Unit
//        }
//        clearDragState()
//    }

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
            Log.i("heec.choi", "addPositionedWidget-$widget $offset $startRow $startCol")
            val gridIndex = startCellIndex ?: 0
            val offsetX = offset.x
            val offsetY = offset.y
            val rowSpan = spanY
            val colSpan = spanX
            val widgetCategory = widget.getWidgetCategory().toProto()
            val widgetTag = widget.getWidgetTag()
            val placedWidgetComponent = PlacedWidgetComponent.newBuilder().setGridIndex(gridIndex)
                .setOffsetX(offsetX).setOffsetY(offsetY)
                .setRowSpan(rowSpan).setColSpan(colSpan).setWidgetCategory(widgetCategory)
                .setWidgetTag(widgetTag).build()

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
        viewModelScope.launch {
            // ID 기반으로 인덱스 찾기 (copy()로 인한 새 인스턴스 생성 문제 해결)
            val data = widgetCanvasStateRepository.dataStoreFlow.first()
            val positionedWidgets = data.placedWidgetComponentList
            val index =
                positionedWidgets.indexOfFirst { it.gridIndex == positionedWidget.gridIndex }
            if (index != -1) {
                // ID를 유지하면서 offset과 cellIndices만 업데이트
                val placedWidgetComponent = PlacedWidgetComponent.newBuilder()
                    .setGridIndex(cellIndices.first())
                    .setOffsetX(offset.x)
                    .setOffsetY(offset.y)
                    .setRowSpan(positionedWidget.rowSpan)
                    .setColSpan(positionedWidget.colSpan)
                    .setWidgetCategory(positionedWidget.widgetCategory)
                    .setWidgetTag(positionedWidget.widgetTag).build()
                widgetCanvasStateRepository.updatePlacedWidget(placedWidgetComponent)
            }
        }
    }

    fun removePositionedWidget(positionedWidget: PositionedWidget) {
        viewModelScope.launch {
            val data = widgetCanvasStateRepository.dataStoreFlow.first()
            val positionedWidgets = data.placedWidgetComponentList
            val index =
                positionedWidgets.indexOfFirst { it.gridIndex == positionedWidget.gridIndex }
            if (index != -1) {
                val placedWidgetComponent = PlacedWidgetComponent.newBuilder()
                    .setGridIndex(positionedWidget.gridIndex)
                    .setOffsetX(positionedWidget.offset.x)
                    .setOffsetY(positionedWidget.offset.y)
                    .setRowSpan(positionedWidget.rowSpan)
                    .setColSpan(positionedWidget.colSpan)
                    .setWidgetCategory(positionedWidget.widgetCategory)
                    .setWidgetTag(positionedWidget.widgetTag).build()
                widgetCanvasStateRepository.removePlacedWidget(placedWidgetComponent)
            }
        }
    }


}