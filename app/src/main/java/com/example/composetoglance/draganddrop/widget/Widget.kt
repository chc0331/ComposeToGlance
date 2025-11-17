package com.example.composetoglance.draganddrop.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.draganddrop.DragTarget

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
                scaleX = 1.0f
                scaleY = 1.0f
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
