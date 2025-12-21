package com.widgetkit.dsl.proto.property

import android.graphics.Bitmap
import com.google.protobuf.ByteString
import com.widgetkit.dsl.proto.Color
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.ImageProvider
import com.widgetkit.dsl.proto.TextContent
import java.io.ByteArrayOutputStream

/**
 * Content 관련 DSL 클래스 및 DSL 빌더 함수
 *
 * 이 파일은 block을 받는 DSL 빌더 함수와 DSL 클래스를 포함합니다.
 * - DSL 클래스: ColorDsl, ColorProviderDsl, TextContentDsl, ImageProviderDsl
 * - DSL 빌더 함수: Color(block), ColorProvider(block), TextContent(block), ImageProvider(block)
 *
 * 간단한 빌더 함수(파라미터를 직접 받는)는 ContentBuilders.kt를 참조하세요.
 */

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

    fun Color(block: ColorDsl.() -> Unit) {
        val colorBuilder = Color.newBuilder()
        ColorDsl(colorBuilder).block()
        builder.setColor(colorBuilder.build())
    }

    fun DarkColor(block: ColorDsl.() -> Unit) {
        val colorBuilder = Color.newBuilder()
        ColorDsl(colorBuilder).block()
        builder.setDarkColor(colorBuilder.build())
    }
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

