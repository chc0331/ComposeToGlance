package com.widgetworld.core.proto.layout

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.AlignmentType
import com.widgetworld.core.proto.component.BaseComponentDsl
import com.widgetworld.core.proto.BoxLayoutProperty
import com.widgetworld.core.proto.modifier.WidgetModifier

class BoxLayoutDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = BoxLayoutProperty.newBuilder()
    private val propertyDsl = BoxLayoutPropertyDsl(propertyBuilder)
    private var contentAlignmentSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 콘텐츠 정렬
     */
    var contentAlignment: AlignmentType
        get() = propertyDsl.contentAlignment
        set(value) {
            contentAlignmentSet = true
            propertyDsl.contentAlignment = value
        }

    /**
     * BoxLayoutProperty 빌드
     */
    internal fun build(): BoxLayoutProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty
        // alignment 기본값 설정
        if (!contentAlignmentSet) {
            propertyDsl.contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
        }
        return propertyBuilder.build()
    }
}

internal class BoxLayoutPropertyDsl(private val builder: BoxLayoutProperty.Builder) {
    var contentAlignment: AlignmentType
        get() = builder.contentAlignment
        set(value) {
            builder.setContentAlignment(value)
        }
}