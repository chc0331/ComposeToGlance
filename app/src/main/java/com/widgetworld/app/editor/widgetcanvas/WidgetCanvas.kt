package com.widgetworld.app.editor.widgetcanvas

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.widgetworld.app.editor.draganddrop.Draggable
import com.widgetworld.app.editor.draganddrop.LocalDragTargetInfo
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.app.editor.WidgetEditorViewModel
import com.widgetworld.app.editor.widgettab.WidgetComponent
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import kotlin.math.roundToInt

private const val TAG = "WidgetCanvas"

@Composable
fun WidgetCanvas(
    viewModel: WidgetEditorViewModel,
    onWidgetAddProcessed: (Offset, LayoutBounds, LayoutType) -> Unit,
    modifier: Modifier = Modifier,
    selectedLayout: LayoutType? = null,
    positionedWidgets: List<PlacedWidgetComponent> = emptyList(),
) {
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasBounds by remember { mutableStateOf<Rect?>(null) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val widget = viewModel.addedWidget
    // 위젯 추가 요청 처리
    LaunchedEffect(widget, layoutBounds, selectedLayout) {
        // 위젯 추가 처리 완료
        if (layoutBounds != null && selectedLayout != null) {
            onWidgetAddProcessed(canvasPosition, layoutBounds!!, selectedLayout)
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                val newPosition = layoutCoordinates.positionInWindow()
                val newBounds = layoutCoordinates.boundsInWindow()
                if (newPosition != canvasPosition) {
                    canvasPosition = newPosition
                }
                if (newBounds != canvasBounds) {
                    canvasBounds = newBounds
                }
                Log.i(TAG, "WidgetCanvas : $canvasPosition $canvasBounds")

            }
    ) {
        WidgetDropHandler(
            viewModel = viewModel,
            layoutBounds = layoutBounds,
            layoutType = selectedLayout,
            canvasPosition = canvasPosition,
            dragInfo = dragInfo
        )

        if (selectedLayout == null && positionedWidgets.isEmpty()) {
            Text(
                "위젯 캔버스",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            DeleteZoneIndicator(
                layoutBounds = layoutBounds,
                canvasPosition = canvasPosition,
                canvasBounds = canvasBounds,
                dragInfo = dragInfo,
                density = density
            )
            if (selectedLayout != null) {
                LayoutDisplay(
                    layout = selectedLayout,
                    onLayoutBoundsChanged = { bounds ->
                        layoutBounds = bounds
                    }
                )
            }


            val gridCells = rememberGridCells(selectedLayout, layoutBounds)
            val occupiedCells = remember(viewModel.positionedWidgets) {
                derivedStateOf {
                    viewModel.getOccupiedCells()
                }
            }.value

//            DragStateOverlay(
//                viewModel = viewModel,
//                gridCells = gridCells,
//                occupiedCells = occupiedCells,
//                selectedLayout = selectedLayout,
//                layoutBounds = layoutBounds,
//                canvasPosition = canvasPosition,
//                density = density,
//                dragInfo = dragInfo
//            )

            // Display dropped widgets
            val draggedItemId = remember(dragInfo.isDragging, dragInfo.dataToDrop) {
                if (dragInfo.isDragging && dragInfo.dataToDrop is PlacedWidgetComponent) {
                    (dragInfo.dataToDrop as PlacedWidgetComponent).widgetTag
                } else {
                    null
                }
            }

            positionedWidgets.forEach { item ->
                val isDragging = item.widgetTag == draggedItemId
                // 고유 ID를 key로 사용하여 위젯 이동 시 재생성되지 않도록 함
                // 이렇게 하면 위젯이 재배치될 때 기존 컴포저블이 유지되고 offset만 업데이트됨
                key(item.widgetTag) {
                    Draggable(
                        dataToDrop = item,
                        modifier = Modifier
                            .offset {
                                IntOffset(item.offsetX.roundToInt(), item.offsetY.roundToInt())
                            }
                            .alpha(if (isDragging) 0f else 1f)
                    ) {
                        val component = WidgetComponentRegistry.getComponent(item.widgetTag!!)
                        component?.let {
                            WidgetComponent(
                                data = it,
                                layout = selectedLayout
                            )
                        }
                    }
                }
            }
        }
    }
}