package com.example.toolkit.layout

import ProtoModifier
import com.example.composetoglance.proto.HorizontalAlignment
import com.example.composetoglance.proto.VerticalAlignment
import com.example.toolkit.Emittable
import com.example.toolkit.EmittableWithChildren

class EmittableColumn : EmittableWithChildren() {
    override var modifier: ProtoModifier = ProtoModifier
    var verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP
    var horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_START

    override fun copy(): Emittable =
        EmittableColumn().also {
            it.modifier = modifier
            it.verticalAlignment = verticalAlignment
            it.horizontalAlignment = horizontalAlignment
            it.children.addAll(children.map { it.copy() })
        }

    override fun toString(): String =
        "EmittableColumn(" +
                "modifier=$modifier, " +
                "verticalAlignment=$verticalAlignment, " +
                "horizontalAlignment=$horizontalAlignment, " +
                "children=[\n${childrenToString()}\n]" +
                ")"
}