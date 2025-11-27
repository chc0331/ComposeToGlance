package com.example.dsl.builder

import com.example.dsl.proto.Action
import com.example.dsl.proto.Component
import com.example.dsl.proto.Semantics


/**
 * Component / Action / Semantics
 * */

fun Semantics(contentDescription: String): Semantics =
    Semantics.newBuilder().setContentDescription(contentDescription).build()

fun Component(packageName: String, className: String): Component =
    Component.newBuilder().setPackageName(packageName).setClassName(className).build()

fun Action(
    activity: Boolean = false,
    service: Boolean = false,
    broadcastReceiver: Boolean = false,
    component: Component
): Action = Action.newBuilder().setActivity(activity).setService(service)
    .setBroadcastReceiver(broadcastReceiver).setComponent(component).build()

/**
 * Semantics DSL
 */
class SemanticsDsl(private val builder: Semantics.Builder) {
    var contentDescription: String
        get() = builder.contentDescription
        set(value) {
            builder.setContentDescription(value)
        }
}

/**
 * Semantics DSL 빌더 함수
 */
fun Semantics(block: SemanticsDsl.() -> Unit): Semantics {
    val builder = Semantics.newBuilder()
    val dsl = SemanticsDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Component DSL
 */
class ComponentDsl(private val builder: Component.Builder) {
    var packageName: String
        get() = builder.packageName
        set(value) {
            builder.setPackageName(value)
        }

    var className: String
        get() = builder.className
        set(value) {
            builder.setClassName(value)
        }
}

/**
 * Component DSL 빌더 함수
 */
fun Component(block: ComponentDsl.() -> Unit): Component {
    val builder = Component.newBuilder()
    val dsl = ComponentDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Action DSL
 */
class ActionDsl(private val builder: Action.Builder) {
    var activity: Boolean
        get() = builder.activity
        set(value) {
            builder.setActivity(value)
        }

    var service: Boolean
        get() = builder.service
        set(value) {
            builder.setService(value)
        }

    var broadcastReceiver: Boolean
        get() = builder.broadcastReceiver
        set(value) {
            builder.setBroadcastReceiver(value)
        }

    fun component(block: ComponentDsl.() -> Unit) {
        val componentBuilder = Component.newBuilder()
        ComponentDsl(componentBuilder).block()
        builder.setComponent(componentBuilder.build())
    }
}

/**
 * Action DSL 빌더 함수
 */
fun Action(block: ActionDsl.() -> Unit): Action {
    val builder = Action.newBuilder()
    val dsl = ActionDsl(builder)
    dsl.block()
    return builder.build()
}

