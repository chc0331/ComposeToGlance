package com.widgetworld.widget.editor.widget.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius

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
        GridLayoutCanvas(
            modifier = Modifier.fillMaxSize(),
            layout = layout, showText = showText
        )
    }
}

@Composable
private fun GridLayoutCanvas(
    modifier: Modifier = Modifier, layout: LayoutType, showText: Boolean
) {
    val gridCell = layout.getGridCell()
    Column(modifier) {
        repeat(gridCell.row) { rowIndex ->
            if (rowIndex > 0) {
                DashedHorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row(Modifier.weight(1f)) {
                repeat(gridCell.column) { colIndex ->
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
