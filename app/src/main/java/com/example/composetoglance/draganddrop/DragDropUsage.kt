package com.example.composetoglance.draganddrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.R
import com.example.composetoglance.util.toColor

@Composable
fun MainContent() {
    // Wrap the entire horizontal pager with LongPressDraggable
    LongPressDrawable(modifier = Modifier.fillMaxSize()) {
        val widgetList = listOf(
            Widget("1", "2"),
            Widget("2", "3")
        )

        DropTarget<Widget>(modifier = Modifier.fillMaxSize()) { isInBound, droppedWidget ->
            if (!LocalDragTargetInfo.current.itemDropped) {
                if (isInBound) {
                    droppedWidget?.let { widget ->
                        LocalDragTargetInfo.current.itemDropped = true
                        LocalDragTargetInfo.current.dataToDrop = null
                    }
                }
            }
        }

        WidgetsList(widgetList)
    }
}

@Composable
fun WidgetsList(widgetList: List<Widget>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
    // Add your custom implementation for the WidgetItem here.
    // This composable will render the content of the draggable widget.
    // You can use the 'data' parameter to extract necessary information and display it.
    // The 'shouldAnimate' parameter can be used to control animations if needed.

    // Example: Displaying a simple card with the widget's name
    Card(
        modifier = Modifier
            .size(50.dp)
            .padding(16.dp)
            .graphicsLayer {
                // Scale the card when shouldAnimate is true
                scaleX = if (shouldAnimate) 1.2f else 1.0f
                scaleY = if (shouldAnimate) 1.2f else 1.0f
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = data.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = data.description)
        }
    }
}

data class Widget(val name: String, val description: String)