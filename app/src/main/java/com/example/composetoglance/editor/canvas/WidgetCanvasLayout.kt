package com.example.composetoglance.editor.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.example.composetoglance.editor.layout.Layout
import com.example.composetoglance.editor.layout.LayoutComponent
import com.example.composetoglance.editor.util.LayoutBounds

@Composable
fun BoxScope.LayoutDisplay(
    selectedLayout: Layout?,
    onLayoutBoundsChanged: (LayoutBounds) -> Unit
) {
    selectedLayout?.let { layout ->
        Box(
            modifier = Modifier
                .align(Alignment.Center)
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
                type = layout.type,
                layoutType = layout.sizeType,
                showText = false
            )
        }
    }
}

