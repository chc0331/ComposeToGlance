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
 * Color DSL
 */
class ColorDsl(private val builder: Color.Builder) {
    var argb: Int
        get() = builder.argb
        set(value) {
            builder.setArgb(value)
        }
}

/**
 * Color DSL 빌더 함수
 */
fun Color(block: ColorDsl.() -> Unit): Color {
    val builder = Color.newBuilder()
    val dsl = ColorDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * ColorProvider DSL
 */
class ColorProviderDsl(private val builder: ColorProvider.Builder) {
    var resId: Int
        get() = builder.resId
        set(value) {
            if (value != 0) {
                builder.setResId(value)
            }
        }

    fun color(block: ColorDsl.() -> Unit) {
        val colorBuilder = Color.newBuilder()
        ColorDsl(colorBuilder).block()
        builder.setColor(colorBuilder.build())
    }

    fun darkColor(block: ColorDsl.() -> Unit) {
        val colorBuilder = Color.newBuilder()
        ColorDsl(colorBuilder).block()
        builder.setDarkColor(colorBuilder.build())
    }
}

/**
 * ColorProvider DSL 빌더 함수
 */
fun ColorProvider(block: ColorProviderDsl.() -> Unit): ColorProvider {
    val builder = ColorProvider.newBuilder()
    val dsl = ColorProviderDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * TextContent DSL
 */
class TextContentDsl(private val builder: TextContent.Builder) {
    var text: String
        get() = builder.text
        set(value) {
            builder.setText(value)
        }

    var resId: Int
        get() = builder.resId
        set(value) {
            if (value != 0) {
                builder.setResId(value)
            }
        }
}

/**
 * TextContent DSL 빌더 함수
 */
fun TextContent(block: TextContentDsl.() -> Unit): TextContent {
    val builder = TextContent.newBuilder()
    val dsl = TextContentDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * ImageProvider DSL
 */
class ImageProviderDsl(private val builder: ImageProvider.Builder) {
    var drawableResId: Int
        get() = if (builder.hasDrawableResId()) builder.drawableResId else 0
        set(value) {
            if (value != 0) {
                builder.setDrawableResId(value)
            }
        }

    fun bitmap(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        builder.setBitmap(ByteString.copyFrom(byteArray))
    }

    var uri: String
        get() = if (builder.hasUri()) builder.uri else ""
        set(value) {
            if (value.isNotEmpty()) {
                builder.setUri(value)
            }
        }

    var iconResource: String
        get() = if (builder.hasIconResource()) builder.iconResource else ""
        set(value) {
            if (value.isNotEmpty()) {
                builder.setIconResource(value)
            }
        }
}

/**
 * ImageProvider DSL 빌더 함수
 */
fun ImageProvider(block: ImageProviderDsl.() -> Unit): ImageProvider {
    val builder = ImageProvider.newBuilder()
    val dsl = ImageProviderDsl(builder)
    dsl.block()
    return builder.build()
}

