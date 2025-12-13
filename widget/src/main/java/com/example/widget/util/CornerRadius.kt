package com.example.widget.util

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Context.getSystemBackgroundRadius(): Dp {
//    val px = resources.getDimensionPixelSize(
//        android.R.dimen.system_app_widget_background_radius
//    )
//    val density = resources.displayMetrics.density
//    return (px / density).dp
    return 16.dp
}

fun Context.getSystemContentRadius(): Dp {
//    val px = resources.getDimensionPixelSize(
//        android.R.dimen.system_app_widget_background_radius
//    )
//    val density = resources.displayMetrics.density
//    return (px / density).dp
    return 16.dp
}
