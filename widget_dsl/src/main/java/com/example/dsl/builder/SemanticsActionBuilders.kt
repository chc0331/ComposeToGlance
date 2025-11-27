package com.example.dsl.builder

import com.example.dsl.proto.Action
import com.example.dsl.proto.Component
import com.example.dsl.proto.Semantics

/**
 * Semantics / Component / Action 간단한 빌더 함수
 * 
 * 이 파일은 파라미터를 직접 받는 간단한 빌더 함수를 포함합니다.
 * DSL 클래스 및 block을 받는 DSL 빌더 함수는 SemanticsActionDsl.kt를 참조하세요.
 */

/**
 * Semantics 간단한 빌더 함수 (파라미터를 직접 받는)
 */
fun Semantics(contentDescription: String): Semantics =
    Semantics.newBuilder().setContentDescription(contentDescription).build()

/**
 * Component 간단한 빌더 함수 (파라미터를 직접 받는)
 */
fun Component(packageName: String, className: String): Component =
    Component.newBuilder().setPackageName(packageName).setClassName(className).build()

/**
 * Action 간단한 빌더 함수 (파라미터를 직접 받는)
 */
fun Action(
    activity: Boolean = false,
    service: Boolean = false,
    broadcastReceiver: Boolean = false,
    component: Component
): Action = Action.newBuilder().setActivity(activity).setService(service)
    .setBroadcastReceiver(broadcastReceiver).setComponent(component).build()

