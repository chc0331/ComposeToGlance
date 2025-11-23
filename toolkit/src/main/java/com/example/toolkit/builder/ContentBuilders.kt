package com.example.toolkit.builder

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.example.toolkit.proto.Color
import com.example.toolkit.proto.ColorProvider
import com.example.toolkit.proto.ImageProvider
import com.example.toolkit.proto.TextContent
import com.google.protobuf.ByteString
import java.io.ByteArrayOutputStream

/**
 * Color, ColorProvider, TextContent, ImageProvider
 * */
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

fun imageProviderFromBitmap(bitmap: Bitmap): ImageProvider {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    return ImageProvider.newBuilder().setBitmap(ByteString.copyFrom(byteArray)).build()
}
