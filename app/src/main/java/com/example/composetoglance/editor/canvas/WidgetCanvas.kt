package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.example.composetoglance.editor.draganddrop.LocalDragTargetInfo
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.widget.WidgetItem
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(
    viewModel: WidgetEditorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLayout = viewModel.selectedLayout
    val positionedWidgets = viewModel.positionedWidgets
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    var layoutBounds by remember { mutableStateOf<LayoutBounds?>(null) }
    val density = LocalDensity.current
    val dragInfo = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .onGloballyPositioned {
                canvasPosition = it.positionInWindow()
            }
    ) {
        WidgetDropHandler(
            viewModel = viewModel,
            layoutBounds = layoutBounds,
            selectedLayout = selectedLayout,
            canvasPosition = canvasPosition,
            density = density,
            dragInfo = dragInfo
        )

        if (selectedLayout == null && positionedWidgets.isEmpty()) {
            Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
        } else {
            LayoutDisplay(
                selectedLayout = selectedLayout,
                onLayoutBoundsChanged = { bounds ->
                    layoutBounds = bounds
                }
            )

            val gridCells = rememberGridCells(selectedLayout, layoutBounds)
            val occupiedCells = viewModel.getOccupiedCells()

            DragStateOverlay(
                viewModel = viewModel,
                gridCells = gridCells,
                occupiedCells = occupiedCells,
                selectedLayout = selectedLayout,
                layoutBounds = layoutBounds,
                canvasPosition = canvasPosition,
                density = density,
                dragInfo = dragInfo
            )

            // Display dropped widgets
            positionedWidgets.forEach { item ->
                Box(
                    modifier = Modifier.offset {
                        IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                    }
                ) {
                    WidgetItem(data = item.widget)
                }
            }
        }
    }
}
