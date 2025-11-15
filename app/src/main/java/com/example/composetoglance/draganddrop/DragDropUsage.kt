package com.example.composetoglance.draganddrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.R
import com.example.composetoglance.util.toColor
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainContent() {
    val widgets = remember {
        mutableStateListOf(
            Widget("1", "2"),
            Widget("2", "3")
        )
    }
    val canvasWidgets = remember { mutableStateListOf<PositionedWidget>() }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    LongPressDrawable(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // DropTarget at the top
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
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

            BottomPanelWithTabs(
                widgets = widgets,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BottomPanelWithTabs(widgets: List<Widget>, modifier: Modifier = Modifier) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) })
            }
        }
        when (tabIndex) {
            0 -> LayoutsTabContent()
            1 -> WidgetsList(widgetList = widgets, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LayoutsTabContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(R.color.bottom_panel_background_color.toColor())),
        contentAlignment = Alignment.Center
    ) {
        Text("레이아웃 탭")
    }
}

@Composable
fun WidgetsList(widgetList: List<Widget>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(R.color.bottom_panel_background_color.toColor())),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(widgetList) { widget ->
            // this composable was defined earlier as
            // each widget item is itself a drag target
            DragTargetWidgetItem(
                data = widget,
            )
        }
    }
}

@Composable
fun DragTargetWidgetItem(
    modifier: Modifier = Modifier,
    data: Widget
) {
    DragTarget(
        context = LocalContext.current,
        modifier = modifier
            .wrapContentSize(),
        dataToDrop = data,
    ) { shouldAnimate ->
        WidgetItem(data, shouldAnimate)
    }
}

@Composable
fun WidgetItem(
    data: Widget,
    shouldAnimate: Boolean
) {
    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleX = if (shouldAnimate) 1.2f else 1.0f
                scaleY = if (shouldAnimate) 1.2f else 1.0f
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.DarkGray), // Dark Gray background for widget
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = Color.White // White text for visibility
            )
        }
    }
}

data class Widget(val name: String, val description: String)

data class PositionedWidget(val widget: Widget, val offset: Offset)
