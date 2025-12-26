package com.widgetkit.dsl

import com.widgetkit.dsl.proto.WidgetLayoutDocument
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocal


fun WidgetLayout(mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL, block: WidgetScope.() -> Unit): WidgetLayoutDocument {
    val scope = WidgetScope()
    scope.block()
    return WidgetLayoutDocument.newBuilder()
        .setRoot(scope.build())
        .setWidgetMode(mode)
        .build()
}

open class WidgetScope {
    private var viewIdCounter = 0
    internal val children = mutableListOf<WidgetNode>()
    private val locals = mutableMapOf<WidgetLocal<out Any?>, Any?>()

    fun nextViewId(): Int = viewIdCounter++

    fun addChild(node: WidgetNode) {
        children.add(node)
    }

    fun <T> setLocal(key: WidgetLocal<T>, value: T) {
        locals[key] = value
    }

    fun <T> getLocal(key: WidgetLocal<T>): T? {
        return locals[key] as? T ?: key.getDefaultValue()
    }

    internal fun copyLocalsFrom(parent: WidgetScope) {
        parent.locals.forEach { (key, value) ->
            locals[key as WidgetLocal<out Any?>] = value
        }
    }

    fun build(): WidgetNode {
        require(children.size == 1) { "WidgetScope must have exactly one root node" }
        return children[0]
    }

    fun buildContainer(
        setter: (WidgetNode.Builder, List<WidgetNode>) -> Unit
    ): WidgetNode {
        val builder = WidgetNode.newBuilder()
        setter(builder, children)
        return builder.build()
    }
}