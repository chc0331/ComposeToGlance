package com.example.composetoglance.editor.canvas

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * 캔버스 관련 상수
 */
object CanvasConstants {
    val CANVAS_WEIGHT = 2.8f
    val BOTTOM_PANEL_WEIGHT = 1.2f
    val HORIZONTAL_PADDING = 16.dp
    val TOP_PADDING = 16.dp
    val SPACER_SIZE = 6.dp
    val CORNER_RADIUS = 16.dp
    val BOTTOM_PANEL_CORNER_RADIUS = 12.dp
    val BORDER_WIDTH = 1.dp
    val STROKE_WIDTH = 2.dp
    val DASH_PATTERN = floatArrayOf(20f, 20f)
    const val CANVAS_BACKGROUND_ALPHA = 0.05f
}

/**
 * 캔버스 테두리를 그리는 Modifier 확장 함수
 * @param outline 테두리 색상 (MaterialTheme.colorScheme.outline 권장)
 * @param backgroundColor 배경 색상 (MaterialTheme.colorScheme.surfaceVariant 권장)
 */
fun Modifier.canvasBorder(
    outline: Color,
    backgroundColor: Color = outline.copy(alpha = CanvasConstants.CANVAS_BACKGROUND_ALPHA)
): Modifier {
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
            color = backgroundColor,
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

