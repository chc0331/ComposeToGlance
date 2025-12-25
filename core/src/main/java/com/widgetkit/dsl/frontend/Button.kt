package com.widgetkit.dsl.frontend

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.ButtonDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Button(
    modifier: WidgetModifier = WidgetModifier,
    mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL,
    contentProperty: ButtonDsl.() -> Unit
) {
    val dsl = ButtonDsl(this, modifier)
    dsl.contentProperty()
    val buttonNode = WidgetNode.newBuilder()
        .setButton(dsl.build())
        .setWidgetMode(mode)
        .build()
    addChild(buttonNode)
}