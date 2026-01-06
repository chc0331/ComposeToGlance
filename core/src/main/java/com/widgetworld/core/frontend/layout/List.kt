package com.widgetworld.core.frontend.layout

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.layout.ListDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

class ListScope : WidgetScope() {
    fun item(content: WidgetScope.() -> Unit) {
        content()
    }
}

fun WidgetScope.List(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: ListDsl.() -> Unit = {},
    content: ListScope.() -> Unit
) {
    val childScope = ListScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = ListDsl(this, modifier)
    dsl.contentProperty()

    val listNode = WidgetNode.newBuilder()
        .setList(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }.build()

    addChild(listNode)
}