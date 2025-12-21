package com.widgetkit.widget.editor.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.widgetkit.widget.editor.draganddrop.DragTarget
import com.widgetkit.dsl.WidgetLayout
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetComponentRegistry
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.proto.PlacedWidgetComponent
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.core.view.AppWidgetView

@Composable
fun DragTargetWidgetItem(
    data: WidgetComponent,
    isClicked: Boolean = false,
    modifier: Modifier = Modifier,
    onComponentClick: () -> Unit = {},
    onAddClick: (WidgetComponent) -> Unit = {},
    onDragStart: () -> Unit = {}
) {
    val context = LocalContext.current
    val cornerRadius = context.getSystemBackgroundRadius()

    DragTarget(
        context = context,
        modifier = modifier.wrapContentSize(),
        dataToDrop = data,
        onComponentClick = onComponentClick,
        onDragStart = onDragStart,
        dragContent = { WidgetItem(data, showLabel = false) }
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
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                        .clickable { onAddClick(data) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "추가",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetItem(
    data: WidgetComponent,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    layout: Layout? = null,
) {
    // 위젯 데이터를 기반으로 캐시 키 생성하여 재렌더링 방지
    val cacheKey = remember(data.getWidgetTag(), data.getSizeType()) {
        "${data.getWidgetTag()}_${data.getSizeType()}"
    }

    // 위젯 아이템 전체를 캐싱하여 깜박임 방지
    WidgetItemContent(
        data = data,
        modifier = modifier,
        key = cacheKey,
        showLabel = showLabel,
        layout = layout,
    )
}

@Composable
private fun WidgetItemContent(
    data: WidgetComponent,
    modifier: Modifier,
    key: String,
    showLabel: Boolean = true,
    layout: Layout? = null,
) {
    val size = remember(key) { data.getSizeInDp(layout) }
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(size.width)
                .height(size.height)
                .clip(RoundedCornerShape(context.getSystemBackgroundRadius()))
                .background(MaterialTheme.colorScheme.surfaceVariant), // Use theme surface variant
            contentAlignment = Alignment.Center
        ) {
            // componentId가 있으면 DSL 컴포넌트를 렌더링, 없으면 기본 텍스트 표시
            if (data.getWidgetTag() != null) {
                val component = remember(key) {
                    WidgetComponentRegistry.getComponent(data.getWidgetTag())
                }
                if (component != null) {
                    // DSL 컴포넌트를 AppWidgetView로 렌더링
                    // renderer를 remember로 캐싱하여 재렌더링 방지
                    val renderer = remember(key) { WidgetRenderer(context) }

                    // layout을 미리 생성하여 캐싱 (깜박임 방지)
                    val layout = remember(key) {
                        WidgetLayout {
                            WidgetLocalProvider(
                                WidgetLocalPreview provides true,
                                WidgetLocalSize provides size,
                                WidgetLocalContext provides context
                            ) {
                                // 현재 scope에서 Content를 호출하여 locals에 접근 가능하도록 함
                                // this는 WidgetLocalProvider가 생성한 childScope를 가리킴
                                // Content()가 생성한 children은 WidgetLocalProvider가 자동으로 수집함
                                component.renderContent(this)
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
private fun DefaultWidgetContent(data: WidgetComponent) {
    // MaterialTheme을 먼저 읽고, remember 블록 안에서는 복사만 수행
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    // MaterialTheme 스타일 객체를 remember로 캐싱하여 불필요한 재생성 방지
    val titleStyle = remember(typography) {
        typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    }
    val bodyStyle = remember(typography) {
        typography.bodySmall
    }
    val titleColor = colorScheme.onSurfaceVariant
    val bodyColor = remember(colorScheme) {
        colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data.getName(),
            style = titleStyle,
            color = titleColor
        )
        Text(
            text = data.getSizeType().toString(),
            style = bodyStyle,
            color = bodyColor
        )
    }
}

data class PositionedWidget(
    val widget: WidgetComponent,
    val offset: Offset,
    val cellIndex: Int? = null,
    val cellIndices: List<Int> = emptyList(), // 여러 셀을 차지하는 경우
    val id: String = java.util.UUID.randomUUID().toString() // 고유 ID for stable key
) {
    fun toProto(): PlacedWidgetComponent {
        val (row, col) = when (cellIndices.size) {
            1 -> Pair(1, 1)
            2 -> Pair(1, 2)
            else -> Pair(2, 2)
        }
        return PlacedWidgetComponent.newBuilder()
            .setGridIndex((cellIndex?.plus(1)) ?: 1)
            .setRowSpan(row)
            .setColSpan(col)
            .setWidgetTag(widget.getWidgetTag())
            .setWidgetCategory(widget.getWidgetCategory().toProto())
            .build()
    }
}

/**
 * 위젯 사이즈 타입에 따른 실제 크기를 Dp 단위로 반환
 * @return Pair<width in dp, height in dp>
 */
fun WidgetComponent.getSizeInDp(layout: Layout?): DpSize {
    return getDpSizeByLayoutType(layout)
}

fun WidgetComponent.toPixels(density: Density, layout: Layout): Pair<Float, Float> {
    return with(density) {
        val (widthDp, heightDp) = getDpSizeByLayoutType(layout)
        widthDp.toPx() to heightDp.toPx()
    }
}

private fun WidgetComponent.getDpSizeByLayoutType(layout: Layout?): DpSize {
    if (layout == null) {
        return when (getSizeType()) {
            SizeType.TINY -> DpSize(68.dp, 80.dp)
            SizeType.SMALL -> DpSize(148.dp, 80.dp)
            SizeType.MEDIUM -> DpSize(148.dp, 160.dp)
            else -> DpSize(50.dp, 50.dp)
        }
    }

    val rootPadding = 8.dp
    val contentPadding = 4.dp
    val rowCell = layout.gridSpec()?.rows ?: 1
    val colCell = layout.gridSpec()?.columns ?: 1
    val containerSize = layout.getDpSize()
    val cellWidth = (containerSize.width - rootPadding * 2) / colCell
    val cellHeight = (containerSize.height - rootPadding * 2) / rowCell
    return when (getSizeType()) {
        SizeType.TINY -> DpSize(cellWidth, cellHeight)
        SizeType.SMALL -> DpSize((cellWidth * 2) - contentPadding * 2, cellHeight)
        else -> DpSize((cellWidth * 2) - contentPadding, (cellHeight * 2) - contentPadding)
    }
}
