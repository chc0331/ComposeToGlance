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
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetkit.dsl.theme.GlanceThemeConverter
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetComponentRegistry
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.getSizeInCells
import com.widgetkit.core.getSizeInCellsForLayout
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
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { onAddClick(data) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "추가",
                        color = Color.White
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
                .background(Color(0xFFF5F5F5)), // Default surface variant color
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

                    // Preview 모드에서는 Context 기반 기본 테마 사용
                    val glanceTheme = remember(key) { 
                        GlanceThemeConverter.createDefaultTheme(context) 
                    }

                    // layout을 미리 생성하여 캐싱 (깜박임 방지)
                    val layout = remember(key, glanceTheme) {
                        WidgetLayout(mode = WidgetMode.WIDGET_MODE_PREVIEW) {
                            WidgetLocalProvider(
                                WidgetLocalPreview provides true,
                                WidgetLocalSize provides size,
                                WidgetLocalContext provides context,
                                WidgetLocalTheme provides glanceTheme
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
    // 기본 색상 사용
    val titleColor = Color(0xFF616161) // onSurfaceVariant
    val bodyColor = Color(0xFF616161).copy(alpha = 0.7f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data.getName(),
            color = titleColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = data.getSizeType().toString(),
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
    /**
     * Proto로 변환 시 실제 배치된 셀 정보를 기반으로 row_span과 col_span 계산
     * @param gridColumns 현재 그리드의 열 수 (cellIndices를 row/col로 변환하기 위해 필요)
     */
    fun toProto(gridColumns: Int): PlacedWidgetComponent {
        // cellIndices가 있으면 실제 배치된 셀 정보를 기반으로 span 계산
        val (colSpan, rowSpan) = if (cellIndices.isNotEmpty() && gridColumns > 0) {
            calculateSpansFromIndices(cellIndices, gridColumns)
        } else {
            // fallback: 기본 1x 사이즈 사용
            widget.getSizeInCells()
        }
        
        return PlacedWidgetComponent.newBuilder()
            .setGridIndex((cellIndex?.plus(1)) ?: 1)
            .setRowSpan(rowSpan)
            .setColSpan(colSpan)
            .setWidgetTag(widget.getWidgetTag())
            .setWidgetCategory(widget.getWidgetCategory().toProto())
            .build()
    }
    
    /**
     * cellIndices로부터 실제 row_span과 col_span 계산
     */
    private fun calculateSpansFromIndices(indices: List<Int>, gridColumns: Int): Pair<Int, Int> {
        if (indices.isEmpty()) return 1 to 1
        
        // 각 셀의 row와 col 계산
        val rows = indices.map { it / gridColumns }
        val cols = indices.map { it % gridColumns }
        
        // span = max - min + 1
        val rowSpan = (rows.maxOrNull() ?: 0) - (rows.minOrNull() ?: 0) + 1
        val colSpan = (cols.maxOrNull() ?: 0) - (cols.minOrNull() ?: 0) + 1
        
        return colSpan to rowSpan
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
        // 1x 그리드 기준 기본 사이즈 (1셀 = 약 70dp 기준)
        // Tiny(1x1), Small(2x1), Medium(2x2)
        return when (getSizeType()) {
            SizeType.TINY -> DpSize(70.dp, 70.dp)      // 1x1
            SizeType.SMALL -> DpSize(144.dp, 70.dp)    // 2x1
            SizeType.MEDIUM -> DpSize(144.dp, 144.dp)  // 2x2
            SizeType.MEDIUM_PLUS -> DpSize(218.dp, 144.dp)  // 3x2
            else -> DpSize(292.dp, 144.dp)             // 4x2 (LARGE)
        }
    }

    val rootPadding = 8.dp
    val contentPadding = 4.dp
    val gridSpec = layout.gridSpec()
    val rowCell = gridSpec?.rows ?: 1
    val colCell = gridSpec?.columns ?: 1
    val containerSize = layout.getDpSize()
    val cellWidth = (containerSize.width - rootPadding * 2) / colCell
    val cellHeight = (containerSize.height - rootPadding * 2) / rowCell
    
    // 레이아웃 타입과 그리드 배수를 고려한 동적 사이즈 계산
    val sizeInCells = this.getSizeInCellsForLayout(layout.sizeType, layout.gridMultiplier)
    val widthCells: Int = sizeInCells.first
    val heightCells: Int = sizeInCells.second
    
    // 여러 셀을 차지할 때 셀 사이의 패딩을 고려
    val widthPadding = if (widthCells > 1) contentPadding * (widthCells - 1) else 0.dp
    val heightPadding = if (heightCells > 1) contentPadding * (heightCells - 1) else 0.dp
    
    return DpSize(
        (cellWidth * widthCells) - widthPadding,
        (cellHeight * heightCells) - heightPadding
    )
}
