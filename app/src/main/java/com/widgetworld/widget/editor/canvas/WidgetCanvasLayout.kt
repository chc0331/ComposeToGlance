package com.widgetworld.widget.editor.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import com.widgetworld.widget.editor.widget.Layout
import com.widgetworld.widget.editor.widget.LayoutComponent
import com.widgetworld.widget.editor.util.LayoutBounds
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius

@Composable
fun BoxScope.LayoutDisplay(
    selectedLayout: Layout?,
    onLayoutBoundsChanged: (LayoutBounds) -> Unit
) {
    val context = LocalContext.current
    selectedLayout?.let { layout ->
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(context.getSystemBackgroundRadius()))
                .onGloballyPositioned { layoutCoordinates ->
                    onLayoutBoundsChanged(
                        LayoutBounds(
                            position = layoutCoordinates.positionInWindow(),
                            size = layoutCoordinates.size
                        )
                    )
                }
        ) {
            LayoutComponent(
                layout = layout,
                showText = false
            )
        }
    }
}

