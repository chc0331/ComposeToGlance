package com.widgetkit.dsl.proto.component

import android.R.attr.text
import android.graphics.Color.argb
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.component.BaseComponentDsl
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.TextContent
import com.widgetkit.dsl.proto.TextProperty
import com.widgetkit.dsl.proto.ViewProperty
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.property.ColorProviderDsl
import com.widgetkit.dsl.proto.property.TextContentDsl
import com.widgetkit.dsl.proto.property.ViewPropertyDsl

class TextDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = TextProperty.newBuilder()
    private val propertyDsl = TextPropertyDsl(propertyBuilder)
    private var textSet = false
    private var fontColorSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 텍스트 내용 설정 블록
     */
    fun TextContent(block: TextContentDsl.() -> Unit) {
        textSet = true
        propertyDsl.Text(block)
    }

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
     * 텍스트 정렬
     */
    var textAlign: TextAlign
        get() = propertyDsl.textAlign
        set(value) {
            propertyDsl.textAlign = value
        }

    /**
     * TextProperty 빌드
     */
    internal fun build(): TextProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty

        if (!textSet) {
            propertyDsl.Text {
                text = ""
            }
        }
        if (!fontColorSet) {
            propertyDsl.FontColor {
                Color {
                    argb = 0xFF000000.toInt()
                }
            }
        }
        return propertyBuilder.build()
    }
}

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