package com.widgetkit.dsl.frontend.layout

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.layout.BoxLayoutDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Box(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: BoxLayoutDsl.() -> Unit = {},
    content: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = BoxLayoutDsl(this, modifier)
    dsl.contentProperty()

    val boxNode = WidgetNode.newBuilder()
        .setBox(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(boxNode)
}