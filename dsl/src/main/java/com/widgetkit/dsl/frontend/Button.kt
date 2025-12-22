package com.widgetkit.dsl.frontend

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.ButtonDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

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