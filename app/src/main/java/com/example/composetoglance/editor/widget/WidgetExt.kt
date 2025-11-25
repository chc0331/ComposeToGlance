package com.example.composetoglance.editor.widget

import androidx.compose.ui.unit.Density
import com.example.composetoglance.editor.widget.Widget

fun Widget.toPixels(density: Density): Pair<Float, Float> {
    return with(density) {
        val (widthDp, heightDp) = getSizeInDp()
        widthDp.toPx() to heightDp.toPx()
    }
}