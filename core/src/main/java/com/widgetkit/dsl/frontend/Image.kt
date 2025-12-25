package com.widgetkit.dsl.frontend

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.ImageDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Image(
    modifier: WidgetModifier = WidgetModifier,
    mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL,
    contentProperty: ImageDsl.() -> Unit
) {
    val dsl = ImageDsl(this, modifier)
    dsl.contentProperty()
    val imageNode = WidgetNode.newBuilder()
        .setImage(dsl.build())
        .setWidgetMode(mode)
        .build()
    addChild(imageNode)
}