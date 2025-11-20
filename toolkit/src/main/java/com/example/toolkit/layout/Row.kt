package com.example.toolkit.layout

import ProtoModifier
import com.example.composetoglance.proto.HorizontalAlignment
import com.example.composetoglance.proto.VerticalAlignment
import com.example.toolkit.Emittable
import com.example.toolkit.EmittableWithChildren

public class EmittableRow : EmittableWithChildren() {
    override var modifier: ProtoModifier = ProtoModifier
    public var horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_START
    public var verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP

    override fun copy(): Emittable =
        EmittableRow().also {
            it.modifier = modifier
            it.horizontalAlignment = horizontalAlignment
            it.verticalAlignment = verticalAlignment
            it.children.addAll(children.map { it.copy() })
        }

    override fun toString(): String =
        "EmittableRow(" +
                "modifier=$modifier, " +
                "horizontalAlignment=$horizontalAlignment, " +
                "verticalAlignment=$verticalAlignment, " +
                "children=[\n${childrenToString()}\n]" +
                ")"
}