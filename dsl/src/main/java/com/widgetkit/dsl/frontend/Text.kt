package com.widgetkit.dsl.frontend

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.TextDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Text(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: TextDsl.() -> Unit
) {
    val dsl = TextDsl(this, modifier)
    dsl.contentProperty()
    val textNode = WidgetNode.newBuilder()
        .setText(dsl.build())
        .build()
    addChild(textNode)
}