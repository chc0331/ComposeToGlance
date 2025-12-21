package com.widgetkit.dsl.proto.component

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.component.BaseComponentDsl
import com.widgetkit.dsl.proto.ButtonProperty
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextContent
import com.widgetkit.dsl.proto.ViewProperty
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.property.ColorProviderDsl
import com.widgetkit.dsl.proto.property.TextContentDsl
import com.widgetkit.dsl.proto.property.ViewPropertyDsl

class ButtonDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ButtonProperty.newBuilder()
    private val propertyDsl = ButtonPropertyDsl(propertyBuilder)
    private var textSet = false
    private var fontColorSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 텍스트 내용 설정 블록
     */
    fun Text(block: TextContentDsl.() -> Unit) {
        textSet = true
        propertyDsl.Text(block)
    }

    /**
     * 텍스트 내용 직접 설정
     */
//    var text: String
//        get() = if (propertyBuilder.hasText()) propertyBuilder.text.text else ""
//        set(value) {
//            textSet = true
//            propertyDsl.text {
//                this.text = value
//            }
//        }

    /**
     * 최대 줄 수
     */
    var maxLine: Int
        get() = propertyDsl.maxLine
        set(value) {
            propertyDsl.maxLine = value
        }

    /**
     * 폰트 색상 설정 블록
     */
    fun FontColor(block: ColorProviderDsl.() -> Unit) {
        fontColorSet = true
        propertyDsl.FontColor(block)
    }

    /**
     * 폰트 크기
     */
    var fontSize: Float
        get() = propertyDsl.fontSize
        set(value) {
            propertyDsl.fontSize = value
        }

    /**
     * 폰트 두께
     */
    var fontWeight: FontWeight
        get() = propertyDsl.fontWeight
        set(value) {
            propertyDsl.fontWeight = value
        }

    /**
     * 배경 색상 설정 블록
     */
    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        propertyDsl.BackgroundColor(block)
    }

    /**
     * ButtonProperty 빌드
     */
    internal fun build(): ButtonProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty

        if (!textSet) {
            throw IllegalArgumentException("Button text must be set")
        }
        if (!fontColorSet) {
            propertyDsl.FontColor {
                Color {
                    argb = 0xFFFFFFFF.toInt()
                }
            }
        }
        return propertyBuilder.build()
    }
}

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