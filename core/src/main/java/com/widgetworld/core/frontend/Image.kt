package com.widgetworld.core.frontend

import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.component.ImageDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

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