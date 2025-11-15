package com.example.composetoglance.draganddrop.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composetoglance.draganddrop.DropTarget
import com.example.composetoglance.draganddrop.LocalDragTargetInfo
import com.example.composetoglance.draganddrop.widget.PositionedWidget
import com.example.composetoglance.draganddrop.widget.Widget
import com.example.composetoglance.draganddrop.widget.WidgetItem
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(modifier: Modifier = Modifier) {
    val canvasWidgets = remember { mutableStateListOf<PositionedWidget>() }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .background(Color(0xFFADD8E6)) // Light Blue Background
            .onGloballyPositioned {
                canvasPosition = it.positionInWindow()
            }
    ) {
        val dragInfo = LocalDragTargetInfo.current
        DropTarget<Widget>(modifier = Modifier.fillMaxSize()) { isInBound, droppedWidget ->
            if (isInBound && droppedWidget != null && !dragInfo.itemDropped) {
                val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
                val relativeOffset = dropPositionInWindow - canvasPosition

                val widgetSizePx = with(density) { 50.dp.toPx() }

                val adjustedOffset = Offset(
                    x = relativeOffset.x - widgetSizePx / 2,
                    y = relativeOffset.y - widgetSizePx / 2
                )

                canvasWidgets.add(PositionedWidget(droppedWidget, adjustedOffset))
                dragInfo.itemDropped = true
            }
        }

        if (canvasWidgets.isEmpty()) {
            Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
        } else {
            canvasWidgets.forEach { positionedWidget ->
                Box(
                    modifier = Modifier.offset {
                        IntOffset(
                            positionedWidget.offset.x.roundToInt(),
                            positionedWidget.offset.y.roundToInt()
                        )
                    }
                ) {
                    WidgetItem(data = positionedWidget.widget, shouldAnimate = false)
                }
            }
        }
    }
}
