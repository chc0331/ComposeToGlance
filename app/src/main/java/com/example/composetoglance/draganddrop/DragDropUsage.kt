package com.example.composetoglance.draganddrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.R
import com.example.composetoglance.util.toColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainContent() {
    val widgets = remember {
        mutableStateListOf(
            Widget("1", "2"),
            Widget("2", "3")
        )
    }
    val canvasWidgets = remember { mutableStateListOf<Widget>() }

    LongPressDrawable(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // DropTarget at the top
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.5f))
            ) {
                val dragInfo = LocalDragTargetInfo.current
                DropTarget<Widget>(modifier = Modifier.fillMaxSize()) { isInBound, droppedWidget ->
                    if (isInBound && droppedWidget != null && !dragInfo.itemDropped) {
                        canvasWidgets.add(droppedWidget)
                        dragInfo.itemDropped = true
                    }
                }

                if (canvasWidgets.isEmpty()) {
                    Text("위젯 캔버스", modifier = Modifier.align(Alignment.Center))
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        canvasWidgets.forEach { widget ->
                            WidgetItem(data = widget, shouldAnimate = false)
                        }
                    }
                }
            }

            WidgetsList(
                widgetList = widgets,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WidgetsList(widgetList: List<Widget>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
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
            .padding(8.dp)
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
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
        }
    }
}

data class Widget(val name: String, val description: String)
