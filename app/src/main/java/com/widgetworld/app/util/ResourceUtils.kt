package com.widgetworld.app.util

import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
@ColorInt
fun Int.toColor(): Int {
    return LocalContext.current.getColor(this)
}