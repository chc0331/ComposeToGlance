package com.widgetkit.dsl.frontend

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.SpacerDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Spacer(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: SpacerDsl.() -> Unit = {}
) {
    val dsl = SpacerDsl(this, modifier)
    dsl.contentProperty()
    val spacerNode = WidgetNode.newBuilder()
        .setSpacer(dsl.build())
        .build()
    addChild(spacerNode)
}