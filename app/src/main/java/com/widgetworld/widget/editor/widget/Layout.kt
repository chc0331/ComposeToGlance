package com.widgetworld.widget.editor.widget

import android.R.attr.data
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius

@Composable
fun ClickableLayoutComponent(
    modifier: Modifier = Modifier,
    layout: LayoutType,
    isClicked: Boolean,
    onComponentClick: () -> Unit,
    onAddClick: (LayoutType) -> Unit,
) {
    val context = LocalContext.current
    val scaleFactor =
        if (layout.name == "Large" || layout.name == "Extra Large") 0.45f else 0.45f
    val cornerRadius = context.getSystemBackgroundRadius() * scaleFactor
    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(cornerRadius))
            .clickable { onComponentClick() },
        contentAlignment = Alignment.Center
    ) {
        LayoutComponent(layout, isPreview = true, scaleFactor = scaleFactor)
        if (isClicked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.15f))
                    .clickable { onAddClick(layout) },
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


@Composable
fun LayoutComponent(
    layout: LayoutType,
    showText: Boolean = false,
    isPreview: Boolean = false,
    scaleFactor: Float = 1f
) {
    val context = LocalContext.current
    var (width, height) = layout.getDpSize()
    var cornerRadius = context.getSystemBackgroundRadius()
    if (isPreview) {
        width = width * scaleFactor
        height = height * scaleFactor
        cornerRadius = cornerRadius * scaleFactor
    }
    Box(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        FullLayoutComponent(layout.name, showText)
    }
}

@Composable
fun LayoutComponent(
    layout: LayoutType,
    showText: Boolean = false,
    isPreview: Boolean = false
) {
    val context = LocalContext.current
    var (width, height) = layout.getDpSize()
    var cornerRadius = context.getSystemBackgroundRadius()
    if (isPreview) {
        width = width * 0.4f
        height = height * 0.4f
        cornerRadius = cornerRadius * 0.4f
    }
    Box(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        DynamicLayoutComponent(layout, showText)
    }
}

/**
 * Full 레이아웃 타입의 컴포넌트 (정적 그리드)
 */
@Composable
private fun FullLayoutComponent(layoutType: String, showText: Boolean) {
    when (layoutType) {
        "Small" -> createGridLayout(rows = 1, columns = 2, showText = showText)
        "Medium" -> createGridLayout(rows = 2, columns = 2, showText = showText)
        "Medium Plus" -> createGridLayout(rows = 4, columns = 6, showText = showText)
        "Large" -> createGridLayout(rows = 2, columns = 4, showText = showText)
        "Extra Large" -> createGridLayout(rows = 4, columns = 4, showText = showText)
        else -> createGridLayout(rows = 2, columns = 2, showText = showText)
    }
}

/**
 * 동적 그리드 배수를 고려한 레이아웃 컴포넌트
 */
@Composable
private fun DynamicLayoutComponent(layout: LayoutType, showText: Boolean) {
    val gridSpec = layout.getGridCell()
    if (gridSpec != null) {
        createGridLayout(rows = gridSpec.row, columns = gridSpec.column, showText = showText)
    } else {
        // fallback to static layout
        FullLayoutComponent(layout.name, showText)
    }
}

/**
 * 그리드 레이아웃을 생성하는 헬퍼 함수 (행과 열 모두)
 */
@Composable
private fun createGridLayout(rows: Int, columns: Int, showText: Boolean) {
    Column(Modifier.fillMaxSize()) {
        repeat(rows) { rowIndex ->
            if (rowIndex > 0) {
                DashedHorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row(Modifier.weight(1f)) {
                repeat(columns) { colIndex ->
                    if (colIndex > 0) {
                        DashedVerticalDivider(
                            Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (showText) {
                            Text("1", Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 점선 세로 구분선 컴포넌트
 */
@Composable
private fun DashedVerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline
) {
    Box(
        modifier = modifier.drawBehind {
            val strokeWidth = 0.3.dp.toPx()
            val dashEffect = PathEffect.dashPathEffect(
                floatArrayOf(8f, 12f),
                0f
            )
            drawLine(
                color = color,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = strokeWidth,
                pathEffect = dashEffect
            )
        }
    )
}

/**
 * 점선 가로 구분선 컴포넌트
 */
@Composable
private fun DashedHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline
) {
    Box(
        modifier = modifier.drawBehind {
            val strokeWidth = 0.3.dp.toPx()
            val dashEffect = PathEffect.dashPathEffect(
                floatArrayOf(8f, 12f),
                0f
            )
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = strokeWidth,
                pathEffect = dashEffect
            )
        }
    )
}
