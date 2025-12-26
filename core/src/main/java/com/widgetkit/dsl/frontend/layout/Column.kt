package com.widgetkit.dsl.frontend.layout

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.layout.ColumnLayoutDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Column(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: ColumnLayoutDsl.() -> Unit = {},
    content: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = ColumnLayoutDsl(this, modifier)
    dsl.contentProperty()

    val columnNode = WidgetNode.newBuilder()
        .setColumn(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(columnNode)
}