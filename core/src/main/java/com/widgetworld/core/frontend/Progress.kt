package com.widgetworld.core.frontend

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.component.ProgressDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

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