package com.example.dsl.builder

import com.example.dsl.proto.Action
import com.example.dsl.proto.Component
import com.example.dsl.proto.Semantics

/**
 * Semantics / Component / Action DSL 클래스 및 DSL 빌더 함수
 * 
 * 이 파일은 block을 받는 DSL 빌더 함수와 DSL 클래스를 포함합니다.
 * 간단한 빌더 함수(파라미터를 직접 받는)는 SemanticsActionBuilders.kt를 참조하세요.
 */

/**
 * Semantics DSL 클래스
 */
class SemanticsDsl(private val builder: Semantics.Builder) {
    var contentDescription: String
        get() = builder.contentDescription
        set(value) {
            builder.setContentDescription(value)
        }
}

/**
 * Semantics DSL 빌더 함수 (block을 받는)
 */
fun Semantics(block: SemanticsDsl.() -> Unit): Semantics {
    val builder = Semantics.newBuilder()
    val dsl = SemanticsDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Component DSL 클래스
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
 * Component DSL 빌더 함수 (block을 받는)
 */
fun Component(block: ComponentDsl.() -> Unit): Component {
    val builder = Component.newBuilder()
    val dsl = ComponentDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Action DSL 클래스
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
 * Action DSL 빌더 함수 (block을 받는)
 */
fun Action(block: ActionDsl.() -> Unit): Action {
    val builder = Action.newBuilder()
    val dsl = ActionDsl(builder)
    dsl.block()
    return builder.build()
}

