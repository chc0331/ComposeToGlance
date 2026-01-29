package com.widgetworld.app.editor.widgetcanvas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.widgetworld.app.editor.WidgetEditorViewModel
import com.widgetworld.app.editor.draganddrop.DragTargetInfo
import com.widgetworld.app.editor.draganddrop.DropTarget
import com.widgetworld.app.editor.util.GridCalculator
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.widgettab.getCellIndices
import com.widgetworld.app.editor.widgettab.toPixels
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.getSizeInCellsForLayout
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent


@Composable
fun WidgetDropHandler(
    viewModel: WidgetEditorViewModel,
    widgetDropViewModel: WidgetDropViewModel = viewModel(),
    layoutBounds: LayoutBounds?,
    selectedLayout: LayoutType?,
    canvasPosition: Offset,
    dragInfo: DragTargetInfo,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    DropTarget(
        onDrop = { dropState ->
            // todo : 정리 필요
            // 1. 드롭 상태 정보 추출.
            val isInBound = dropState.isInBound
            val droppedItem = dropState.droppedData
            val dropPositionInWindow = dropState.dropPositionInWindow

            // 2. 위젯 삭제 처리(레이아웃 밖으로 드래그 된 경우)
            if (droppedItem is PlacedWidgetComponent && layoutBounds != null) {
                val bounds = layoutBounds
                val isWithinLayoutBounds = dropPositionInWindow.x >= bounds.position.x &&
                        dropPositionInWindow.x <= bounds.position.x + bounds.size.width &&
                        dropPositionInWindow.y >= bounds.position.y &&
                        dropPositionInWindow.y <= bounds.position.y + bounds.size.height

                if (!isWithinLayoutBounds) {
                    // 즉시 드래그 상태 정리하여 잔상 방지
                    dragInfo.itemDropped = true
                    dragInfo.isDragging = false
                    dragInfo.dragOffset = Offset.Zero
                    dragInfo.dataToDrop = null
                    dragInfo.draggableComposable = null
                    widgetDropViewModel.removePositionedWidget(droppedItem)
                    return@DropTarget
                }
            }

            // 3. 위젯 삭제 처리(캔버스 밖으로 드래그 된 경우)
            if (!isInBound && droppedItem is PlacedWidgetComponent) {
                // 즉시 드래그 상태 정리하여 잔상 방지
                dragInfo.itemDropped = true
                dragInfo.isDragging = false
                dragInfo.dragOffset = Offset.Zero
                dragInfo.dataToDrop = null
                dragInfo.draggableComposable = null
                widgetDropViewModel.removePositionedWidget(droppedItem)
                return@DropTarget
            }

            /**
             * 4. 유효성 검사
             * - 캔서브 밖으로 드래그 된 위젯은 처리하지 않음.
             * - 드롭된 아이템 타입 체크
             * */
            if (!isInBound) {
                return@DropTarget
            }

            val widget = when (droppedItem) {
                is WidgetComponent -> droppedItem
                is PlacedWidgetComponent -> {
                    WidgetComponentRegistry.getComponent(droppedItem.widgetTag ?: "")
                }

                else -> return@DropTarget
            }
            val bounds = layoutBounds
            val spec = selectedLayout?.getGridCell()
            if (bounds == null || spec == null || widget == null) {
                return@DropTarget
            }
            val currentLayout = selectedLayout ?: return@DropTarget

            // 5. 그리드 계산 및 위치 결정
            val widgetSizeInCells = widget.getSizeInCellsForLayout(
                currentLayout.name,
                currentLayout.getDivide()
            )
            val widgetWidthCells = widgetSizeInCells.first
            val widgetHeightCells = widgetSizeInCells.second
            val gridCells = GridCalculator.calculateGridCells(spec, bounds)

            val bestStart = GridCalculator.calculateBestCellPosition(
                dropPositionInWindow,
                widgetWidthCells,
                widgetHeightCells,
                gridCells,
                spec,
                bounds
            ) ?: return@DropTarget

            val (startRow, startCol) = bestStart
            val indices = GridCalculator.calculateCellIndices(
                startRow,
                startCol,
                widgetWidthCells,
                widgetHeightCells,
                spec
            )

            //6. 충돌 검사: 드래그 중인 위젯의 원래 위치(모든 행 포함)는 제외하고 다른 위젯과의 충돌만 확인
            val occupiedIndices = if (droppedItem is PlacedWidgetComponent) {
                // 드래그 중인 위젯을 제외한 점유된 셀들 (모든 행의 셀 포함)
                val occupiedByOthers = viewModel.getOccupiedCells(excluding = droppedItem)
                // 원래 위치의 모든 셀 인덱스 (모든 행 포함)를 명시적으로 제외 (부분 겹침 허용을 위해)
                val originalIndices = droppedItem.getCellIndices()
                // 원래 위치의 모든 셀(모든 행 포함)을 제외하여 부분 겹침 이동 허용
                // 예: (1,1) (2,1)에서 (2,1) (3,1)로 이동 가능
                occupiedByOthers - originalIndices
            } else {
                viewModel.getOccupiedCells()
            }

            if (indices.any { it in occupiedIndices }) {
                return@DropTarget
            }

            //7. 드롭 완료 처리
            dragInfo.itemDropped = true

            val (widgetWidthPx, widgetHeightPx) = widget.toPixels(density, selectedLayout)
            val adjustedOffset = GridCalculator.calculateWidgetOffset(
                startRow,
                startCol,
                widgetWidthCells,
                widgetHeightCells,
                widgetWidthPx,
                widgetHeightPx,
                bounds,
                spec,
                canvasPosition
            )

            when (droppedItem) {
                is WidgetComponent -> viewModel.addPositionedWidget(
                    widget = widget,
                    offset = adjustedOffset,
                    startRow = startRow,
                    startCol = startCol,
                    cellIndices = indices,
                    widgetWidthCells, widgetHeightCells

                )

                is PlacedWidgetComponent -> {
                    viewModel.movePositionedWidget(
                        positionedWidget = droppedItem,
                        offset = adjustedOffset,
                        startRow = startRow,
                        startCol = startCol,
                        cellIndices = indices
                    )
                }
            }
        }
    )
}