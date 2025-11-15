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
import com.example.composetoglance.draganddrop.layout.Layout
import com.example.composetoglance.draganddrop.layout.LayoutComponent
import com.example.composetoglance.draganddrop.layout.PositionedLayout
import com.example.composetoglance.draganddrop.widget.PositionedWidget
import com.example.composetoglance.draganddrop.widget.Widget
import com.example.composetoglance.draganddrop.widget.WidgetItem
import kotlin.math.roundToInt

@Composable
fun WidgetCanvas(modifier: Modifier = Modifier) {
    val canvasItems = remember { mutableStateListOf<Any>() }
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
        DropTarget(modifier = Modifier.fillMaxSize()) { isInBound, droppedItem ->
            if (isInBound && droppedItem != null && !dragInfo.itemDropped) {
                val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
                val relativeOffset = dropPositionInWindow - canvasPosition

                when (droppedItem) {
                    is Widget -> {
                        val widgetSizePx = with(density) { 50.dp.toPx() }
                        val adjustedOffset = Offset(relativeOffset.x - widgetSizePx / 2, relativeOffset.y - widgetSizePx / 2)
                        canvasItems.add(PositionedWidget(droppedItem, adjustedOffset))
                    }
                    is Layout -> {
                        val (width, height) = when (droppedItem.sizeType) {
                            "Small" -> Pair(105.dp, 45.dp)
                            "Medium" -> Pair(90.dp, 90.dp)
                            "Large" -> Pair(180.dp, 90.dp)
                            else -> Pair(105.dp, 45.dp)
                        }
                        val adjustedOffset = Offset(
                            relativeOffset.x - with(density) { width.toPx() } / 2,
                            relativeOffset.y - with(density) { height.toPx() } / 2
                        )
                        canvasItems.add(PositionedLayout(droppedItem, adjustedOffset))
                    }
                }
                dragInfo.itemDropped = true
            }
        }

        if (canvasItems.isEmpty()) {
            Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
        } else {
            canvasItems.forEach { item ->
                when (item) {
                    is PositionedWidget -> {
                        Box(
                            modifier = Modifier.offset {
                                IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                            }
                        ) {
                            WidgetItem(data = item.widget, shouldAnimate = false)
                        }
                    }
                    is PositionedLayout -> {
                        Box(
                            modifier = Modifier.offset {
                                IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt())
                            }
                        ) {
                            LayoutComponent(type = item.layout.type, layoutType = item.layout.sizeType, shouldAnimate = false)
                        }
                    }
                }
            }
        }
    }
}
