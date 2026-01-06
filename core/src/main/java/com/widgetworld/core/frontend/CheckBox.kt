package com.widgetworld.core.frontend

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.component.CheckBoxDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

fun WidgetScope.CheckBox(
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