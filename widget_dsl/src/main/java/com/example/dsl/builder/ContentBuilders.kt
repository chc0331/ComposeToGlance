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
 * Content 관련 간단한 빌더 함수
 *
 * 이 파일은 파라미터를 직접 받는 간단한 빌더 함수를 포함합니다.
 * - Color(argb: Int)
 * - ColorProvider(resId, color, darkColor)
 * - TextContent(text, resId)
 * - ImageProviderFromDrawable(resId)
 * - ImageProviderFromUri(uri)
 * - ImageProviderFromBitmap(bitmap)
 *
 * DSL 클래스 및 block을 받는 DSL 빌더 함수는 ContentDsl.kt를 참조하세요.
 */
internal fun Color(argb: Int): Color = Color.newBuilder().setArgb(argb).build()

internal fun ColorProvider(
    resId: Int = 0, color: Color? = null, darkColor: Color? = null
): ColorProvider = ColorProvider.newBuilder().apply { if (resId != 0) setResId(resId) }
    .apply { color?.let { setColor(it) } }.apply { darkColor?.let { setDarkColor(it) } }.build()

internal fun TextContent(text: String, resId: Int = 0): TextContent =
    TextContent.newBuilder().setText(text).apply { if (resId != 0) setResId(resId) }.build()

internal fun ImageProviderFromDrawable(@DrawableRes resId: Int): ImageProvider =
    ImageProvider.newBuilder().setDrawableResId(resId).build()

internal fun ImageProviderFromUri(uri: String): ImageProvider =
    ImageProvider.newBuilder().setUri(uri).build()

internal fun ImageProviderFromBitmap(bitmap: Bitmap): ImageProvider {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val byteArray = stream.toByteArray()
    return ImageProvider.newBuilder().setBitmap(ByteString.copyFrom(byteArray)).build()
}
