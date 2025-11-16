package com.example.composetoglance.draganddrop.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import com.example.composetoglance.draganddrop.DropTarget
import com.example.composetoglance.draganddrop.LocalDragTargetInfo
import com.example.composetoglance.draganddrop.layout.Layout
import com.example.composetoglance.draganddrop.layout.LayoutComponent
import com.example.composetoglance.draganddrop.widget.PositionedWidget
import com.example.composetoglance.draganddrop.widget.Widget
import com.example.composetoglance.draganddrop.widget.WidgetItem
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(selectedLayout: Layout?, modifier: Modifier = Modifier) {
    val positionedWidgets = remember { mutableStateListOf<PositionedWidget>() }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    // When a new layout is selected, clear the existing widgets.
    LaunchedEffect(selectedLayout) {
        if (selectedLayout != null) {
            positionedWidgets.clear()
        }
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onGloballyPositioned {
                canvasPosition = it.positionInWindow()
            }
    ) {
        val dragInfo = LocalDragTargetInfo.current
        DropTarget(modifier = Modifier.fillMaxSize()) { isInBound, droppedItem ->
            if (isInBound && droppedItem != null && !dragInfo.itemDropped) {
                val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
                val relativeOffset = dropPositionInWindow - canvasPosition

                if (droppedItem is Widget) {
                    val widgetSizePx = with(density) { 50.dp.toPx() }
                    val adjustedOffset = Offset(relativeOffset.x - widgetSizePx / 2, relativeOffset.y - widgetSizePx / 2)
                    positionedWidgets.add(PositionedWidget(droppedItem, adjustedOffset))
                    dragInfo.itemDropped = true
                }
            }
        }

        if (selectedLayout == null && positionedWidgets.isEmpty()) {
            Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
        } else {
            // Display selected layout in the center
            selectedLayout?.let {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    LayoutComponent(
                        type = it.type,
                        layoutType = it.sizeType,
                        shouldAnimate = false,
                        showText = false
                    )
                }
            }
            // Display dropped widgets
            positionedWidgets.forEach { item ->
                Box(
                    modifier = Modifier.offset {
                        IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                    }
                ) {
                    WidgetItem(data = item.widget, shouldAnimate = false)
                }
            }
        }
    }
}
