package com.example.composetoglance.editor.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Layout(val type: String, val sizeType: String)

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
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable { onComponentClick() },
        contentAlignment = Alignment.Center
    ) {
        LayoutComponent(data.type, data.sizeType, isPreview = true)
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


@Composable
fun LayoutComponent(
    type: String,
    layoutType: String,
    showText: Boolean = false,
    isPreview: Boolean = false
) {
    var (width, height) = LayoutDpSize[layoutType] ?: Pair(180.dp, 80.dp)
    if (isPreview) {
        width = width * 0.5f
        height = height * 0.5f
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline),
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
                VerticalDivider(
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
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row(Modifier.weight(1f)) {
                repeat(columns) { colIndex ->
                    if (colIndex > 0) {
                        VerticalDivider(
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
