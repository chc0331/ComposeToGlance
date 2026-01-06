package com.widgetworld.core.frontend.layout

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.layout.BoxLayoutDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

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