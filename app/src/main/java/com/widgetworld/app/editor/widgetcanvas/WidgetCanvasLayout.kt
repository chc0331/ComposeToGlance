package com.widgetworld.app.editor.widgetcanvas

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
import com.widgetworld.app.editor.widgettab.layout.LayoutComponent
import com.widgetworld.app.editor.util.LayoutBounds
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius

@Composable
fun BoxScope.LayoutDisplay(
    layout: LayoutType,
    onLayoutBoundsChanged: (LayoutBounds) -> Unit
) {
    val context = LocalContext.current
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

