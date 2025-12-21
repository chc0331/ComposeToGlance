package com.widgetkit.dsl.dsl

import com.widgetkit.dsl.proto.Action
import com.widgetkit.dsl.proto.Component
import com.widgetkit.dsl.proto.Semantics

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