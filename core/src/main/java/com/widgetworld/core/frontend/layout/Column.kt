package com.widgetworld.core.frontend.layout

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.layout.ColumnLayoutDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

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