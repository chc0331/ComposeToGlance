package com.example.dsl.builder

import com.example.dsl.proto.Action
import com.example.dsl.proto.Component
import com.example.dsl.proto.Semantics


/**
 * Component / Action / Semantics
 * */

fun semantics(contentDescription: String): Semantics =
    Semantics.newBuilder().setContentDescription(contentDescription).build()

fun component(packageName: String, className: String): Component =
    Component.newBuilder().setPackageName(packageName).setClassName(className).build()

fun action(
    activity: Boolean = false,
    service: Boolean = false,
    broadcastReceiver: Boolean = false,
    component: Component
): Action = Action.newBuilder().setActivity(activity).setService(service)
    .setBroadcastReceiver(broadcastReceiver).setComponent(component).build()

