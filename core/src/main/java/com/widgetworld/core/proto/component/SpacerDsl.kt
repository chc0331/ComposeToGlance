package com.widgetworld.core.proto.component

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.component.BaseComponentDsl
import com.widgetworld.core.proto.SpacerProperty
import com.widgetworld.core.proto.ViewProperty
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.property.ViewPropertyDsl

class SpacerDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = SpacerProperty.newBuilder()
    private val propertyDsl = SpacerPropertyDsl(propertyBuilder)

    init {
        this.modifier(modifier)
    }

    /**
     * SpacerProperty 빌드
     */
    internal fun build(): SpacerProperty {
        propertyBuilder.viewProperty = buildViewProperty()
        return propertyBuilder.build()
    }
}

internal class SpacerPropertyDsl(private val builder: SpacerProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }
}