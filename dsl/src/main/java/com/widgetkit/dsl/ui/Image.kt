package com.widgetkit.dsl.ui

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.ImageDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Image(
    modifier: WidgetModifier = WidgetModifier,
    contentProperty: ImageDsl.() -> Unit
) {
    val dsl = ImageDsl(this, modifier)
    dsl.contentProperty()
    val imageNode = WidgetNode.newBuilder()
        .setImage(dsl.build())
        .build()
    addChild(imageNode)
}