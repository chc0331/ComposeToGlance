package com.widgetworld.widget.editor.widget.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius

@Composable
fun LayoutComponentContainer(
    modifier: Modifier = Modifier,
    layout: LayoutType,
    isClicked: Boolean,
    onLayoutClick: () -> Unit,
    onAddClick: (LayoutType) -> Unit,
) {
    val context = LocalContext.current
    val scaleFactor = 0.45f
    val cornerRadius = context.getSystemBackgroundRadius() * scaleFactor
    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(cornerRadius))
            .clickable { onLayoutClick() },
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