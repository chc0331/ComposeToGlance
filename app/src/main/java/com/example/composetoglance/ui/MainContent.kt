package com.example.composetoglance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.composetoglance.ui.bottompanel.BottomPanelWithTabs
import com.example.composetoglance.ui.canvas.WidgetCanvas
import com.example.composetoglance.ui.viewmodel.WidgetEditorViewModel

/**
 * 캔버스 관련 상수
 */
private object CanvasConstants {
    val CANVAS_WEIGHT = 2.8f
    val BOTTOM_PANEL_WEIGHT = 1.2f
    val HORIZONTAL_PADDING = 16.dp
    val TOP_PADDING = 16.dp
    val SPACER_SIZE = 6.dp
    val CORNER_RADIUS = 16.dp
    val BOTTOM_PANEL_CORNER_RADIUS = 12.dp
    val BORDER_WIDTH = 1.dp
    val CANVAS_BACKGROUND_COLOR = Color(80f, 47f, 100f, 0.1f)
    val STROKE_WIDTH = 2.dp
    val DASH_PATTERN = floatArrayOf(20f, 20f)
}

/**
 * 캔버스 테두리를 그리는 Modifier 확장 함수
 */
private fun Modifier.canvasBorder(outline: Color): Modifier {
    return this.drawBehind {
        val cornerRadius = CanvasConstants.CORNER_RADIUS.toPx()
        val strokeWidth = CanvasConstants.STROKE_WIDTH.toPx()
        val dashEffect = PathEffect.dashPathEffect(
            CanvasConstants.DASH_PATTERN,
            0f
        )
        val inset = strokeWidth / 6
        val borderRoundRect = RoundRect(
            left = -inset,
            top = -inset,
            right = size.width + inset,
            bottom = size.height + inset,
            cornerRadius = CornerRadius(
                cornerRadius + inset,
                cornerRadius + inset
            )
        )
        drawRoundRect(
            color = CanvasConstants.CANVAS_BACKGROUND_COLOR,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )
        val path = Path().apply {
            addRoundRect(borderRoundRect)
        }
        val stroke = Stroke(
            width = strokeWidth,
            pathEffect = dashEffect
        )
        drawPath(
            path = path,
            color = outline,
            style = stroke
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    viewModel: WidgetEditorViewModel = viewModel()
) {
    val outline = MaterialTheme.colorScheme.outline

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("위젯 편집") },
                modifier = Modifier.height(64.dp),
                actions = {
                    TextButton(onClick = { viewModel.save() }) {
                        Text("저장")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LongPressDrawable(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = CanvasConstants.HORIZONTAL_PADDING)
            ) {
                WidgetCanvas(
                    modifier = Modifier
                        .weight(CanvasConstants.CANVAS_WEIGHT)
                        .fillMaxWidth()
                        .padding(top = CanvasConstants.TOP_PADDING)
                        .canvasBorder(outline),
                    selectedLayout = viewModel.selectedLayout,
                )

                Spacer(modifier = Modifier.size(CanvasConstants.SPACER_SIZE))

                BottomPanelWithTabs(
                    widgets = viewModel.widgets,
                    categories = viewModel.categories,
                    onLayoutSelected = { viewModel.selectLayout(it) },
                    modifier = Modifier
                        .weight(CanvasConstants.BOTTOM_PANEL_WEIGHT)
                        .clip(RoundedCornerShape(CanvasConstants.BOTTOM_PANEL_CORNER_RADIUS))
                        .border(
                            CanvasConstants.BORDER_WIDTH,
                            outline,
                            RoundedCornerShape(CanvasConstants.BOTTOM_PANEL_CORNER_RADIUS)
                        )
                )
            }
        }
    }
}
