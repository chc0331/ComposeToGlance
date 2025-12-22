package com.widgetkit.dsl.proto.component

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.CheckboxProperty
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.TextProperty
import com.widgetkit.dsl.proto.ViewProperty
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.property.ColorProviderDsl
import com.widgetkit.dsl.proto.property.TextPropertyDsl
import com.widgetkit.dsl.proto.property.ViewPropertyDsl

class CheckBoxDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = CheckboxProperty.newBuilder()
    private val propertyDsl = CheckBoxPropertyDsl(propertyBuilder)
    private var textSet = false
    private var checkedColorSet = false
    private var uncheckedColorSet = false

    init {
        this.modifier(modifier)
        // 기본값 설정
        propertyDsl.checked = false
    }

    /**
     * 체크 상태
     */
    var checked: Boolean
        get() = propertyDsl.checked
        set(value) {
            propertyDsl.checked = value
        }

    /**
     * 체크박스 텍스트 설정 블록
     */
    fun TextProperty(block: TextPropertyDsl.() -> Unit) {
        textSet = true
        propertyDsl.Text(block)
    }

    /**
     * 체크 상태 색상 설정 블록
     */
    fun CheckedColor(block: ColorProviderDsl.() -> Unit) {
        checkedColorSet = true
        propertyDsl.CheckedColor(block)
    }

    /**
     * 미체크 상태 색상 설정 블록
     */
    fun UncheckedColor(block: ColorProviderDsl.() -> Unit) {
        uncheckedColorSet = true
        propertyDsl.UncheckedColor(block)
    }

    /**
     * CheckboxProperty 빌드
     */
    internal fun build(): CheckboxProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty
        if (!textSet) {
            propertyDsl.Text {
                TextContent {
                    text = ""
                }
            }
        }
        if (!checkedColorSet) {
            propertyDsl.CheckedColor {
                Color {
                    argb = 0xFF4CAF50.toInt() // 기본 체크 색상 (녹색)
                }
            }
        }
        if (!uncheckedColorSet) {
            propertyDsl.UncheckedColor {
                Color {
                    argb = 0xFF9E9E9E.toInt() // 기본 미체크 색상 (회색)
                }
            }
        }
        return propertyBuilder.build()
    }
}

internal class CheckBoxPropertyDsl(private val builder: CheckboxProperty.Builder) {
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

    fun Text(block: TextPropertyDsl.() -> Unit) {
        val textPropertyBuilder = TextProperty.newBuilder()
        TextPropertyDsl(textPropertyBuilder).block()
        val textProperty = textPropertyBuilder.build()
        builder.setTextProperty(textProperty)
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
