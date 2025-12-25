package com.widgetkit.dsl.frontend.layout

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.layout.ListDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

class ListScope : WidgetScope() {
    fun item(content: WidgetScope.() -> Unit) {
        content()
    }
}

fun WidgetScope.List(
    modifier: WidgetModifier = WidgetModifier,
    mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL,
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
        .setWidgetMode(mode)
        .apply {
            childScope.children.forEach { addChildren(it) }
        }.build()

    addChild(listNode)
}