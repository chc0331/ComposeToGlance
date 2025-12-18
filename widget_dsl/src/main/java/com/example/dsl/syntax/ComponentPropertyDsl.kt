package com.example.dsl.syntax

import com.example.dsl.proto.ButtonProperty
import com.example.dsl.proto.CheckboxProperty
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
 * ComponentProperty 관련 DSL 클래스 및 DSL 빌더 함수
 *
 * 이 파일은 block을 받는 DSL 빌더 함수와 DSL 클래스를 포함합니다.
 * - DSL 클래스: TextPropertyDsl, ImagePropertyDsl, ButtonPropertyDsl, ProgressPropertyDsl, SpacerPropertyDsl
 * - DSL 빌더 함수: TextProperty(block), ImageProperty(block), ButtonProperty(block), ProgressProperty(block), SpacerProperty(block)
 *
 * 간단한 빌더 함수(파라미터를 직접 받는)는 ComponentPropertyBuilders.kt를 참조하세요.
 */

/**
 * TextProperty DSL
 */
internal class TextPropertyDsl(private val builder: TextProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    fun Text(block: TextContentDsl.() -> Unit) {
        val textContentBuilder = TextContent.newBuilder()
        TextContentDsl(textContentBuilder).block()
        builder.setText(textContentBuilder.build())
    }

    var maxLine: Int
        get() = builder.maxLine
        set(value) {
            builder.setMaxLine(value)
        }

    fun FontColor(block: ColorProviderDsl.() -> Unit) {
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
 * ImageProperty DSL
 */
internal class ImagePropertyDsl(private val builder: ImageProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    fun Provider(block: ImageProviderDsl.() -> Unit) {
        val imageProviderBuilder = ImageProvider.newBuilder()
        ImageProviderDsl(imageProviderBuilder).block()
        builder.setProvider(imageProviderBuilder.build())
    }

    fun TintColor(block: ColorDsl.() -> Unit) {
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

    var animation: Boolean
        get() = builder.animation
        set(value) {
            builder.setAnimation(value)
        }

    var infiniteLoop: Boolean
        get() = builder.infiniteLoop
        set(value) {
            builder.setInfiniteLoop(value)
        }


}

/**
 * ButtonProperty DSL
 */
internal class ButtonPropertyDsl(private val builder: ButtonProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    fun Text(block: TextContentDsl.() -> Unit) {
        val textContentBuilder = TextContent.newBuilder()
        TextContentDsl(textContentBuilder).block()
        builder.setText(textContentBuilder.build())
    }

    var maxLine: Int
        get() = builder.maxLine
        set(value) {
            builder.setMaxLine(value)
        }

    fun FontColor(block: ColorProviderDsl.() -> Unit) {
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
 * ProgressProperty DSL
 */
internal class ProgressPropertyDsl(private val builder: ProgressProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
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

    fun ProgressColor(block: ColorProviderDsl.() -> Unit) {
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
 * SpacerProperty DSL
 */
internal class SpacerPropertyDsl(private val builder: SpacerProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }
}

/**
 * CheckboxProperty DSL
 */
internal class CheckboxPropertyDsl(private val builder: CheckboxProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    var checked: Boolean
        get() = builder.checked
        set(value) {
            builder.setChecked(value)
        }

    fun Text(block: TextContentDsl.() -> Unit) {
        val textContentBuilder = TextContent.newBuilder()
        TextContentDsl(textContentBuilder).block()
        builder.setText(textContentBuilder.build())
    }

    fun CheckedColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setCheckedColor(colorProviderBuilder.build())
    }

    fun UncheckedColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setUncheckedColor(colorProviderBuilder.build())
    }
}
