package com.example.dsl.builder

import com.example.dsl.proto.ButtonProperty
import com.example.dsl.proto.Color
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.ImageProperty
import com.example.dsl.proto.ImageProvider
import com.example.dsl.proto.ProgressProperty
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.SpacerProperty
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextContent
import com.example.dsl.proto.TextProperty
import com.example.dsl.proto.ViewProperty

/**
 * TextProperty DSL
 */
class TextPropertyDsl(private val builder: TextProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    fun text(block: TextContentDsl.() -> Unit) {
        val textContentBuilder = TextContent.newBuilder()
        TextContentDsl(textContentBuilder).block()
        builder.setText(textContentBuilder.build())
    }

    var maxLine: Int
        get() = builder.maxLine
        set(value) {
            builder.setMaxLine(value)
        }

    fun fontColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setFontColor(colorProviderBuilder.build())
    }

    var fontSize: Float
        get() = builder.fontSize
        set(value) {
            builder.setFontSize(value)
        }

    var fontWeight: FontWeight
        get() = builder.fontWeight
        set(value) {
            builder.setFontWeight(value)
        }

    var textAlign: TextAlign
        get() = builder.textAlign
        set(value) {
            builder.setTextAlign(value)
        }
}

/**
 * TextProperty DSL 빌더 함수 (대문자로 시작)
 */
fun TextProperty(block: TextPropertyDsl.() -> Unit): TextProperty {
    val builder = TextProperty.newBuilder()
    val dsl = TextPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * ImageProperty DSL
 */
class ImagePropertyDsl(private val builder: ImageProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    fun provider(block: ImageProviderDsl.() -> Unit) {
        val imageProviderBuilder = ImageProvider.newBuilder()
        ImageProviderDsl(imageProviderBuilder).block()
        builder.setProvider(imageProviderBuilder.build())
    }

    fun tintColor(block: ColorDsl.() -> Unit) {
        val colorBuilder = Color.newBuilder()
        ColorDsl(colorBuilder).block()
        builder.setTintColor(colorBuilder.build())
    }

    var alpha: Float
        get() = builder.alpha
        set(value) {
            builder.setAlpha(value)
        }

    var contentScale: ContentScale
        get() = builder.contentScale
        set(value) {
            builder.setContentScale(value)
        }
}

/**
 * ImageProperty DSL 빌더 함수 (대문자로 시작)
 */
fun ImageProperty(block: ImagePropertyDsl.() -> Unit): ImageProperty {
    val builder = ImageProperty.newBuilder()
    val dsl = ImagePropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * ButtonProperty DSL
 */
class ButtonPropertyDsl(private val builder: ButtonProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    fun text(block: TextContentDsl.() -> Unit) {
        val textContentBuilder = TextContent.newBuilder()
        TextContentDsl(textContentBuilder).block()
        builder.setText(textContentBuilder.build())
    }

    var maxLine: Int
        get() = builder.maxLine
        set(value) {
            builder.setMaxLine(value)
        }

    fun fontColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setFontColor(colorProviderBuilder.build())
    }

    var fontSize: Float
        get() = builder.fontSize
        set(value) {
            builder.setFontSize(value)
        }

    var fontWeight: FontWeight
        get() = builder.fontWeight
        set(value) {
            builder.setFontWeight(value)
        }

    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setBackgroundColor(colorProviderBuilder.build())
    }
}

/**
 * ButtonProperty DSL 빌더 함수 (대문자로 시작)
 */
fun ButtonProperty(block: ButtonPropertyDsl.() -> Unit): ButtonProperty {
    val builder = ButtonProperty.newBuilder()
    val dsl = ButtonPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * ProgressProperty DSL
 */
class ProgressPropertyDsl(private val builder: ProgressProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    var progressType: ProgressType
        get() = builder.progressType
        set(value) {
            builder.setProgressType(value)
        }

    var maxValue: Float
        get() = builder.maxValue
        set(value) {
            builder.setMaxValue(value)
        }

    var progressValue: Float
        get() = builder.progressValue
        set(value) {
            builder.setProgressValue(value)
        }

    fun progressColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setProgressColor(colorProviderBuilder.build())
    }

    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setBackgroundColor(colorProviderBuilder.build())
    }
}

/**
 * ProgressProperty DSL 빌더 함수 (대문자로 시작)
 */
fun ProgressProperty(block: ProgressPropertyDsl.() -> Unit): ProgressProperty {
    val builder = ProgressProperty.newBuilder()
    val dsl = ProgressPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * SpacerProperty DSL
 */
class SpacerPropertyDsl(private val builder: SpacerProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }
}

/**
 * SpacerProperty DSL 빌더 함수 (대문자로 시작)
 */
fun SpacerProperty(block: SpacerPropertyDsl.() -> Unit): SpacerProperty {
    val builder = SpacerProperty.newBuilder()
    val dsl = SpacerPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

