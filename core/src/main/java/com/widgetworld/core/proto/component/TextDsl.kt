package com.widgetworld.core.proto.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.proto.TextAlign
import com.widgetworld.core.proto.TextDecoration
import com.widgetworld.core.proto.TextProperty
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.property.ColorProviderDsl
import com.widgetworld.core.proto.property.TextContentDsl
import com.widgetworld.core.proto.property.TextPropertyDsl

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
     * 텍스트 장식
     */
    var textDecoration: TextDecoration
        get() = propertyDsl.textDecoration
        set(value) {
            propertyDsl.textDecoration = value
        }

    /**
     * 텍스트 내용을 직접 설정 (파라미터 기반 API용)
     */
    internal fun setText(text: String?) {
        if (text != null) {
            textSet = true
            propertyDsl.TextContent {
                this.text = text
            }
        }
    }

    /**
     * 리소스 ID로 텍스트 설정 (파라미터 기반 API용)
     */
    internal fun setTextResId(resId: Int) {
        if (resId != 0) {
            textSet = true
            propertyDsl.TextContent {
                this.resId = resId
            }
        }
    }

    /**
     * Compose Color로 폰트 색상 설정 (파라미터 기반 API용)
     */
    internal fun setFontColor(color: Color) {
        fontColorSet = true
        propertyDsl.FontColor {
            Color {
                argb = color.toArgb()
            }
        }
    }

    /**
     * ARGB Int로 폰트 색상 설정 (파라미터 기반 API용)
     */
    internal fun setFontColorArgb(argb: Int) {
        if (argb != 0) {
            fontColorSet = true
            propertyDsl.FontColor {
                Color {
                    this.argb = argb
                }
            }
        }
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