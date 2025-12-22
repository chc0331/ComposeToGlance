package com.widgetkit.dsl.proto.component

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.TextProperty
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.property.ColorProviderDsl
import com.widgetkit.dsl.proto.property.TextContentDsl
import com.widgetkit.dsl.proto.property.TextPropertyDsl

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
        propertyDsl.TextContent(block)
    }

    /**
     * TextPropertyDsl 전체를 사용하는 텍스트 설정 블록
     * - CheckBoxDsl의 Text 블록과 동일한 스타일로 사용 가능
     */
    fun Text(block: TextPropertyDsl.() -> Unit) {
        textSet = true
        propertyDsl.block()
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
            propertyDsl.TextContent {
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