package com.example.composetoglance.editor.widget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.editor.draganddrop.DragTarget
import com.example.dsl.WidgetLayout
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.provider.DslLocalContext
import com.example.dsl.provider.DslLocalProvider
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.Widget
import com.example.widget.WidgetComponentRegistry
import com.example.widget.view.AppWidgetView

@Composable
fun DragTargetWidgetItem(
    data: Widget,
    isClicked: Boolean = false,
    modifier: Modifier = Modifier,
    onComponentClick: () -> Unit = {},
    onAddClick: (Widget) -> Unit = {},
    onDragStart: () -> Unit = {}
) {
    DragTarget(
        context = LocalContext.current,
        modifier = modifier.wrapContentSize(),
        dataToDrop = data,
        onComponentClick = onComponentClick,
        onDragStart = onDragStart,
        dragContent = { WidgetItem(data) }
    ) {
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            WidgetItem(data)
            if (isClicked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                ) {
                    Button(
                        onClick = { onAddClick(data) },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("추가")
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetItem(
    data: Widget,
    modifier: Modifier = Modifier
) {
    // 위젯 데이터를 기반으로 캐시 키 생성하여 재렌더링 방지
    val cacheKey = remember(data.componentId, data.sizeType) {
        "${data.componentId}_${data.sizeType}"
    }

    // 위젯 아이템 전체를 캐싱하여 깜박임 방지
    WidgetItemContent(
        data = data,
        modifier = modifier,
        key = cacheKey
    )
}

@Composable
private fun WidgetItemContent(
    data: Widget,
    modifier: Modifier,
    key: String
) {
    val (width, height) = data.getSizeInDp()
    val size = remember(key) { DpSize(width, height) }
    val context = LocalContext.current

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
            // componentId가 있으면 DSL 컴포넌트를 렌더링, 없으면 기본 텍스트 표시
            if (data.componentId != null) {
                val component = remember(key) {
                    Log.i("heec.choi", "Component : ${data.componentId}")
                    WidgetComponentRegistry.getComponent(data.componentId)
                }
                if (component != null) {
                    // DSL 컴포넌트를 AppWidgetView로 렌더링
                    // renderer를 remember로 캐싱하여 재렌더링 방지
                    val renderer = remember(key) { GlanceRenderer(context) }

                    // layout을 미리 생성하여 캐싱 (깜박임 방지)
                    val layout = remember(key) {
                        WidgetLayout {
                            DslLocalProvider(
                                DslLocalSize provides size,
                                DslLocalContext provides context
                            ) {
                                component()
                            }
                        }
                    }

                    AppWidgetView(
                        size = size,
                        layout = layout,
                        renderer = renderer
                    )
                } else {
                    // 컴포넌트를 찾을 수 없을 때 기본 텍스트 표시
                    DefaultWidgetContent(data)
                }
            } else {
                // componentId가 없을 때 기본 텍스트 표시
                DefaultWidgetContent(data)
            }
        }
    }
}

@Composable
private fun DefaultWidgetContent(data: Widget) {
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
            text = data.sizeType.toString(),
            style = TextStyle(fontSize = 12.sp),
            color = Color.LightGray
        )
    }
}

data class PositionedWidget(
    val widget: Widget,
    val offset: Offset,
    val cellIndex: Int? = null,
    val cellIndices: List<Int> = emptyList() // 여러 셀을 차지하는 경우
)

/**
 * 위젯 사이즈 타입에 따른 실제 크기를 Dp 단위로 반환
 * @return Pair<width in dp, height in dp>
 */
fun Widget.getSizeInDp(): Pair<Dp, Dp> {
    return when (sizeType) {
        SizeType.TINY -> 50.dp to 50.dp
        SizeType.SMALL -> 100.dp to 50.dp
        SizeType.MEDIUM -> 100.dp to 100.dp
        else -> 50.dp to 50.dp
    }
}

fun Widget.toPixels(density: Density): Pair<Float, Float> {
    return with(density) {
        val (widthDp, heightDp) = getSizeInDp()
        widthDp.toPx() to heightDp.toPx()
    }
}
