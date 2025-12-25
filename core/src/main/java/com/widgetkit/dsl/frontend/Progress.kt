package com.widgetkit.dsl.frontend

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.ProgressDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Progress(
    modifier: WidgetModifier = WidgetModifier,
    mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL,
    contentProperty: ProgressDsl.() -> Unit
) {
    val dsl = ProgressDsl(this, modifier)
    dsl.contentProperty()
    val progressNode = WidgetNode.newBuilder()
        .setProgress(dsl.build())
        .setWidgetMode(mode)
        .build()
    addChild(progressNode)
}