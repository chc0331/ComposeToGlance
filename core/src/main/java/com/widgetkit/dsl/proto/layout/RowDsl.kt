package com.widgetkit.dsl.proto.layout

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.component.BaseComponentDsl
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.HorizontalAlignment.H_ALIGN_START
import com.widgetkit.dsl.proto.RowLayoutProperty
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.proto.VerticalAlignment.V_ALIGN_TOP
import com.widgetkit.dsl.proto.modifier.WidgetModifier

class RowLayoutDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = RowLayoutProperty.newBuilder()
    private val propertyDsl = RowLayoutPropertyDsl(propertyBuilder)
    private var horizontalAlignmentSet = false
    private var verticalAlignmentSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 수평 정렬
     */
    var horizontalAlignment: HorizontalAlignment
        get() = propertyDsl.horizontalAlignment
        set(value) {
            horizontalAlignmentSet = true
            propertyDsl.horizontalAlignment = value
        }

    /**
     * 수직 정렬
     */
    var verticalAlignment: VerticalAlignment
        get() = propertyDsl.verticalAlignment
        set(value) {
            verticalAlignmentSet = true
            propertyDsl.verticalAlignment = value
        }

    /**
     * RowLayoutProperty 빌드
     */
    internal fun build(): RowLayoutProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty
        // alignment 기본값 설정
        if (!horizontalAlignmentSet) {
            propertyDsl.horizontalAlignment = H_ALIGN_START
        }
        if (!verticalAlignmentSet) {
            propertyDsl.verticalAlignment = V_ALIGN_TOP
        }
        return propertyBuilder.build()
    }
}

internal class RowLayoutPropertyDsl(private val builder: RowLayoutProperty.Builder) {
    var horizontalAlignment: HorizontalAlignment
        get() = builder.horizontalAlignment
        set(value) {
            builder.setHorizontalAlignment(value)
        }

    var verticalAlignment: VerticalAlignment
        get() = builder.verticalAlignment
        set(value) {
            builder.setVerticalAlignment(value)
        }
}