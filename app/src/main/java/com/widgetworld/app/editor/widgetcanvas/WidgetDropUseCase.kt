package com.widgetworld.app.editor.widgetcanvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import com.widgetworld.app.editor.draganddrop.DropTargetState
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.widgettab.PositionedWidget
import com.widgetworld.app.editor.widgettab.toPixels
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout
import javax.inject.Inject

class WidgetDropUseCase @Inject constructor() {

//    fun calculate(
//        dropState: DropTargetState,
//        payload: DragPayload,
//        dragPosition: Offset,
//        layoutBounds: LayoutBounds?,
//        selectedLayout: LayoutType?,
//        canvasPosition: Offset,
//        occupiedCellsProvider: (excluding: PositionedWidget?) -> Set<Int>,
//        density: Density
//    ): DropResult {
//        if (payload is DragPayload.ExistingWidget && layoutBounds != null) {
//            val isWithinLayoutBounds = isWithinLayoutBounds(dragPosition, layoutBounds)
//            if (!isWithinLayoutBounds) {
//                return DropResult.Remove(payload.positioned)
//            }
//        }
//
//        if (!dropState.isInBound) {
//            return when (payload) {
//                is DragPayload.ExistingWidget ->
//                    DropResult.Remove(payload.positioned)
//
//                else -> DropResult.Ignore
//            }
//        }
//
//        val bounds = layoutBounds ?: return DropResult.Ignore
//        val layout = selectedLayout ?: return DropResult.Ignore
//        val spec = layout.getGridCell()
//
//        val widget = when (payload) {
//            is DragPayload.NewWidget -> payload.widget
//            is DragPayload.ExistingWidget ->
//                payload.positioned.widget
//                    ?: payload.positioned.widgetTag?.let { WidgetComponentRegistry.getComponent(it) }
//        } ?: return DropResult.Ignore
//
//        val (wCells, hCells) = widget.getSizeInCellsForLayout(
//            layout.name,
//            layout.getDivide()
//        )
//
//        val gridCells = GridCalculator.calculateGridCells(spec, bounds)
//
//        val bestStart = GridCalculator.calculateBestCellPosition(
//            dragPosition,
//            wCells,
//            hCells,
//            gridCells,
//            spec,
//            bounds
//        ) ?: return DropResult.Ignore
//
//        val (startRow, startCol) = bestStart
//        val indices = GridCalculator.calculateCellIndices(
//            startRow, startCol, wCells, hCells, spec
//        ).toSet()
//
//        val occupied = when (payload) {
//            is DragPayload.ExistingWidget -> {
//                val others = occupiedCellsProvider(payload.positioned)
//                val originalIndices = if (payload.positioned.cellIndices.isNotEmpty()) {
//                    payload.positioned.cellIndices.toSet()
//                } else {
//                    payload.positioned.cellIndex?.let { setOf(it) } ?: emptySet()
//                }
//                others - originalIndices
//            }
//
//            else -> occupiedCellsProvider(null)
//        }
//
//        if (indices.any { it in occupied }) {
//            return DropResult.Ignore
//        }
//
//        val (wPx, hPx) = widget.toPixels(density, layout)
//        val offset = GridCalculator.calculateWidgetOffset(
//            startRow,
//            startCol,
//            wCells,
//            hCells,
//            wPx,
//            hPx,
//            bounds,
//            spec,
//            canvasPosition
//        )
//
//        return when (payload) {
//            is DragPayload.NewWidget ->
//                DropResult.Add(
//                    widget!!, offset, startRow, startCol,
//                    indices, wCells, hCells
//                )
//
//            is DragPayload.ExistingWidget ->
//                DropResult.Move(
//                    payload.positioned, offset, startRow, startCol, indices
//                )
//        }
//    }

    private fun isWithinLayoutBounds(position: Offset, layoutBounds: LayoutBounds): Boolean {
        return position.x >= layoutBounds.position.x &&
            position.x <= layoutBounds.position.x + layoutBounds.size.width &&
            position.y >= layoutBounds.position.y &&
            position.y <= layoutBounds.position.y + layoutBounds.size.height
    }
}