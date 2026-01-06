package com.widgetworld.core.proto.component

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.Color
import com.widgetworld.core.proto.ContentScale
import com.widgetworld.core.proto.ImageProperty
import com.widgetworld.core.proto.ImageProvider
import com.widgetworld.core.proto.ViewProperty
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.property.ColorDsl
import com.widgetworld.core.proto.property.ImageProviderDsl
import com.widgetworld.core.proto.property.ViewPropertyDsl

class ImageDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ImageProperty.newBuilder()
    private val propertyDsl = ImagePropertyDsl(propertyBuilder)
    private var providerSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 이미지 제공자 설정 블록
     */
    fun Provider(block: ImageProviderDsl.() -> Unit) {
        providerSet = true
        propertyDsl.Provider(block)
    }

    /**
     * 틴트 색상 설정 블록
     */
    fun TintColor(block: ColorDsl.() -> Unit) {
        propertyDsl.TintColor(block)
    }

    /**
     * 투명도
     */
    var alpha: Float
        get() = propertyDsl.alpha
        set(value) {
            propertyDsl.alpha = value
        }

    /**
     * 콘텐츠 스케일
     */
    var contentScale: ContentScale
        get() = propertyDsl.contentScale
        set(value) {
            propertyDsl.contentScale = value
        }

    var animation: Boolean
        get() = propertyDsl.animation
        set(value) {
            propertyDsl.animation = value
        }

    var infiniteLoop: Boolean
        get() = propertyDsl.infiniteLoop
        set(value) {
            propertyDsl.infiniteLoop = value
        }

    /**
     * ImageProperty 빌드
     */
    internal fun build(): ImageProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty
        if (!providerSet) {
            throw IllegalArgumentException("Image provider must be set. Use provider { drawableResId = ... } or provider { uri = ... } or provider { bitmap(...) }")
        }
        return propertyBuilder.build()
    }
}

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