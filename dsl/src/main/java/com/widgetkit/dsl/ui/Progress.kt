package com.widgetkit.dsl.ui

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.ProgressDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Progress(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: ProgressDsl.() -> Unit
) {
    val dsl = ProgressDsl(this, modifier)
    dsl.contentProperty()
    val progressNode = WidgetNode.newBuilder()
        .setProgress(dsl.build())
        .build()
    addChild(progressNode)
}