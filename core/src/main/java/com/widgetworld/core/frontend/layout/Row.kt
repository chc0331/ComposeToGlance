package com.widgetworld.core.frontend.layout

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.layout.RowLayoutDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

fun WidgetScope.Row(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: RowLayoutDsl.() -> Unit = {},
    content: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = RowLayoutDsl(this, modifier)
    dsl.contentProperty()

    val rowNode = WidgetNode.newBuilder()
        .setRow(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(rowNode)
}