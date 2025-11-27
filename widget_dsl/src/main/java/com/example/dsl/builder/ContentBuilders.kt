package com.example.dsl.builder

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.example.dsl.proto.Color
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.ImageProvider
import com.example.dsl.proto.TextContent
import com.google.protobuf.ByteString
import java.io.ByteArrayOutputStream

/**
 * Color, ColorProvider, TextContent, ImageProvider
 * */
fun Color(argb: Int): Color = Color.newBuilder().setArgb(argb).build()

fun ColorProvider(
    resId: Int = 0, color: Color? = null, darkColor: Color? = null
): ColorProvider = ColorProvider.newBuilder().apply { if (resId != 0) setResId(resId) }
    .apply { color?.let { setColor(it) } }.apply { darkColor?.let { setDarkColor(it) } }.build()

fun TextContent(text: String, resId: Int = 0): TextContent =
    TextContent.newBuilder().setText(text).apply { if (resId != 0) setResId(resId) }.build()

fun ImageProviderFromDrawable(@DrawableRes resId: Int): ImageProvider =
    ImageProvider.newBuilder().setDrawableResId(resId).build()

fun ImageProviderFromUri(uri: String): ImageProvider =
    ImageProvider.newBuilder().setUri(uri).build()

fun ImageProviderFromBitmap(bitmap: Bitmap): ImageProvider {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    return ImageProvider.newBuilder().setBitmap(ByteString.copyFrom(byteArray)).build()
}
