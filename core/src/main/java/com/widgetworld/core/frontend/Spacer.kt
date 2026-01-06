package com.widgetworld.core.frontend

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.component.SpacerDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

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