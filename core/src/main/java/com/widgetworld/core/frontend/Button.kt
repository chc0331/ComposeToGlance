package com.widgetworld.core.frontend

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.component.ButtonDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

fun WidgetScope.Button(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: ButtonDsl.() -> Unit
) {
    val dsl = ButtonDsl(this, modifier)
    dsl.contentProperty()
    val buttonNode = WidgetNode.newBuilder()
        .setButton(dsl.build())
        .build()
    addChild(buttonNode)
}