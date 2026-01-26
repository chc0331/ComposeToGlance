package com.widgetworld.app.editor.widgetcanvas

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
 * 캔버스 테두리를 그리는 Modifier 확장 함수
 * @param outline 테두리 색상 (MaterialTheme.colorScheme.outline 권장)
 * @param backgroundColor 배경 색상 (MaterialTheme.colorScheme.surfaceVariant 권장)
 */
fun Modifier.canvasBorder(
    outline: Color,
    backgroundColor: Color = outline.copy(alpha = 0.05f)
): Modifier {
    return this.drawBehind {
        val cornerRadius = 16.dp.toPx()
        val strokeWidth = 2.dp.toPx()
        val dashEffect = PathEffect.dashPathEffect(
            floatArrayOf(20f, 20f),
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

