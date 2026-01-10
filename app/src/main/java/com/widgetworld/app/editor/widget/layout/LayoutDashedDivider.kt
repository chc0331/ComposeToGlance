package com.widgetworld.app.editor.widget.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

/**
 * 점선 세로 구분선 컴포넌트
 */
@Composable
internal fun DashedVerticalDivider(
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
internal fun DashedHorizontalDivider(
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