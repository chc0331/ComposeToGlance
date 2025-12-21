package com.widgetkit.dsl.ui

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.CheckBoxDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Checkbox(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: CheckBoxDsl.() -> Unit
) {
    val dsl = CheckBoxDsl(this, modifier)
    dsl.contentProperty()
    val checkboxNode = WidgetNode.newBuilder()
        .setCheckbox(dsl.build())
        .build()
    addChild(checkboxNode)
}