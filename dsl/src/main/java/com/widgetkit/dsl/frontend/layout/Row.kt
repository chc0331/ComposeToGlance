package com.widgetkit.dsl.frontend.layout

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.layout.RowLayoutDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

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