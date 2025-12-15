package com.example.composetoglance.editor.widget

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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.widget.LayoutDpSize
import com.example.widget.util.getSystemBackgroundRadius

data class Layout(val sizeType: String) {
    fun getDpSize(): DpSize {
        val size = LayoutDpSize[sizeType] ?: Pair(155.dp, 185.dp)
        return DpSize(size.first, size.second)
    }
}

data class LayoutGridSpec(val rows: Int, val columns: Int)

private val layoutGridSpecs = mapOf(
    "Full" to mapOf(
        "Small" to LayoutGridSpec(rows = 1, columns = 2),
        "Medium" to LayoutGridSpec(rows = 2, columns = 2),
        "Large" to LayoutGridSpec(rows = 2, columns = 4)
    )
)

fun Layout.gridSpec(): LayoutGridSpec? = layoutGridSpecs["Full"]?.get(sizeType)

@Composable
fun ClickableLayoutComponent(
    modifier: Modifier = Modifier,
    data: Layout,
    isClicked: Boolean,
    onComponentClick: () -> Unit,
    onAddClick: (Layout) -> Unit,
) {
    val context = LocalContext.current
    val cornerRadius = context.getSystemBackgroundRadius() * 0.4f
    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(cornerRadius))
            .clickable { onComponentClick() },
        contentAlignment = Alignment.Center
    ) {
        LayoutComponent(data.sizeType, isPreview = true)
        if (isClicked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.15f))
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


@Composable
fun LayoutComponent(
    layoutType: String,
    showText: Boolean = false,
    isPreview: Boolean = false
) {
    val context = LocalContext.current
    var (width, height) = LayoutDpSize[layoutType] ?: Pair(180.dp, 80.dp)
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
        FullLayoutComponent(layoutType, showText)
    }
}

/**
 * Full 레이아웃 타입의 컴포넌트
 */
@Composable
private fun FullLayoutComponent(layoutType: String, showText: Boolean) {
    when (layoutType) {
        "Small" -> createGridRow(columns = 2, showText = showText)
        "Medium" -> createGridLayout(rows = 2, columns = 2, showText = showText)
        "Large" -> createGridLayout(rows = 2, columns = 4, showText = showText)
    }
}

/**
 * 가로 그리드 행을 생성하는 헬퍼 함수
 */
@Composable
private fun createGridRow(columns: Int, showText: Boolean) {
    Row(Modifier.fillMaxSize()) {
        repeat(columns) { index ->
            if (index > 0) {
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
