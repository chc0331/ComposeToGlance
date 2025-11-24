package com.example.composetoglance.editor.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.editor.draganddrop.DragTarget

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
    ) {
        WidgetItem(data)
    }
}

@Composable
fun WidgetItem(
    data: Widget,
    modifier: Modifier = Modifier
) {
    val (width, height) = data.getSizeInDp()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray), // Dark Gray background for widget
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.name,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = Color.White // White text for visibility
                )
                Text(
                    text = data.sizeType,
                    style = TextStyle(fontSize = 12.sp),
                    color = Color.LightGray
                )
            }
        }
    }
}

data class Widget(
    val name: String,
    val description: String,
    val sizeType: String = "1x1", // "1x1", "2x1", "2x2"
    val categoryId: String? = null // 카테고리 ID
)

data class Category(
    val id: String,
    val name: String,
    val icon: String? = null // 아이콘은 나중에 추가 가능
)

data class PositionedWidget(
    val widget: Widget,
    val offset: Offset,
    val cellIndex: Int? = null,
    val cellIndices: List<Int> = emptyList() // 여러 셀을 차지하는 경우
)

/**
 * 위젯 사이즈 타입을 파싱하여 그리드에서 차지하는 셀 수를 반환
 * @return Pair<width in cells, height in cells>
 */
fun Widget.getSizeInCells(): Pair<Int, Int> {
    return when (sizeType) {
        "1x1" -> 1 to 1
        "2x1" -> 2 to 1
        "2x2" -> 2 to 2
        else -> 1 to 1
    }
}

/**
 * 위젯 사이즈 타입에 따른 실제 크기를 Dp 단위로 반환
 * @return Pair<width in dp, height in dp>
 */
fun Widget.getSizeInDp(): Pair<Dp, Dp> {
    return when (sizeType) {
        "1x1" -> 50.dp to 50.dp
        "2x1" -> 100.dp to 50.dp
        "2x2" -> 100.dp to 100.dp
        else -> 50.dp to 50.dp
    }
}
