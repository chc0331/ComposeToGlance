package com.widgetkit.dsl.proto.component

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.component.BaseComponentDsl
import com.widgetkit.dsl.proto.SpacerProperty
import com.widgetkit.dsl.proto.ViewProperty
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.property.ViewPropertyDsl

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