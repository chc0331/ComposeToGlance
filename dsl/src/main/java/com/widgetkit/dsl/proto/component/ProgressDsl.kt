package com.widgetkit.dsl.proto.component

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.component.BaseComponentDsl
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.ProgressProperty
import com.widgetkit.dsl.proto.ProgressType
import com.widgetkit.dsl.proto.ProgressType.PROGRESS_TYPE_LINEAR
import com.widgetkit.dsl.proto.ViewProperty
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.property.ColorProviderDsl
import com.widgetkit.dsl.proto.property.ViewPropertyDsl

class ProgressDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ProgressProperty.newBuilder()
    private val propertyDsl = ProgressPropertyDsl(propertyBuilder)
    private var progressColorSet = false
    private var backgroundColorSet = false

    init {
        this.modifier(modifier)
        // 기본값 설정
        propertyDsl.progressType = PROGRESS_TYPE_LINEAR
        propertyDsl.maxValue = 100f
        propertyDsl.progressValue = 0f
    }

    /**
     * 진행률 타입
     */
    var progressType: ProgressType
        get() = propertyDsl.progressType
        set(value) {
            propertyDsl.progressType = value
        }

    /**
     * 최대값
     */
    var maxValue: Float
        get() = propertyDsl.maxValue
        set(value) {
            propertyDsl.maxValue = value
        }

    /**
     * 진행률 값
     */
    var progressValue: Float
        get() = propertyDsl.progressValue
        set(value) {
            propertyDsl.progressValue = value
        }

    /**
     * 진행률 색상 설정 블록
     */
    fun ProgressColor(block: ColorProviderDsl.() -> Unit) {
        progressColorSet = true
        propertyDsl.ProgressColor(block)
    }

    /**
     * 배경 색상 설정 블록
     */
    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        backgroundColorSet = true
        propertyDsl.BackgroundColor(block)
    }

    /**
     * ProgressProperty 빌드
     */
    internal fun build(): ProgressProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty

        if (!progressColorSet) {
            propertyDsl.ProgressColor {
                Color {
                    argb = 0xFFFFFFFF.toInt()
                }
            }
        }
        if (!backgroundColorSet) {
            propertyDsl.BackgroundColor {
                Color {
                    argb = 0xFFE0E0E0.toInt()
                }
            }
        }
        return propertyBuilder.build()
    }
}

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