package com.example.composetoglance.builder

import androidx.annotation.DrawableRes
import com.example.composetoglance.proto.Color
import com.example.composetoglance.proto.ColorProvider
import com.example.composetoglance.proto.ImageProvider
import com.example.composetoglance.proto.TextContent

/**
 * Color, ColorProvider, TextContent, ImageProvider
 * */
object ContentBuilder {
    fun color(argb: Int): Color = Color.newBuilder().setArgb(argb).build()

    fun colorProvider(
        resId: Int = 0, color: Color? = null, darkColor: Color? = null
    ): ColorProvider = ColorProvider.newBuilder().apply { if (resId != 0) setResId(resId) }
        .apply { color?.let { setColor(it) } }.apply { darkColor?.let { setDarkColor(it) } }.build()

    fun textContent(text: String, resId: Int = 0): TextContent =
        TextContent.newBuilder().setText(text).apply { if (resId != 0) setResId(resId) }.build()

    fun imageProviderFromDrawable(@DrawableRes resId: Int): ImageProvider =
        ImageProvider.newBuilder().setDrawableResId(resId).build()

    fun imageProviderFromUri(uri: String): ImageProvider =
        ImageProvider.newBuilder().setUri(uri).build()
}


