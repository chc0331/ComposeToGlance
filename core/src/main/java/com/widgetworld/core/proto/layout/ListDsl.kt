package com.widgetworld.core.proto.layout

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.HorizontalAlignment
import com.widgetworld.core.proto.ListLayoutProperty
import com.widgetworld.core.proto.component.BaseComponentDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

class ListDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {

    private val propertyBuilder = ListLayoutProperty.newBuilder()
    private val propertyDsl = ListLayoutPropertyDsl(propertyBuilder)
    private var horizontalAlignmentSet = false

    init {
        this.modifier(modifier)
    }

    var horizontalAlignment: HorizontalAlignment
        get() = propertyDsl.horizontalAlignment
        set(value) {
            horizontalAlignmentSet = true
            propertyDsl.horizontalAlignment = value
        }

    internal fun build(): ListLayoutProperty {
        val viewProperty = buildViewProperty()
        propertyBuilder.viewProperty = viewProperty

        if (!horizontalAlignmentSet) {
            propertyDsl.horizontalAlignment = HorizontalAlignment.H_ALIGN_START
        }
        return propertyBuilder.build()
    }


}

internal class ListLayoutPropertyDsl(private val builder: ListLayoutProperty.Builder) {
    var horizontalAlignment: HorizontalAlignment
        get() = builder.horizontalAlignment
        set(value) {
            builder.setHorizontalAlignment(value)
        }
}