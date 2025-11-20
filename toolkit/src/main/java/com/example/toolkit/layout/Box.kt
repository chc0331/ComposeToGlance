package com.example.toolkit.layout

import ProtoModifier
import com.example.composetoglance.proto.AlignmentType
import com.example.toolkit.Emittable
import com.example.toolkit.EmittableWithChildren

public class EmittableBox : EmittableWithChildren() {
    override var modifier: ProtoModifier = ProtoModifier
    var contentAlignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_TOP_START

    override fun copy(): Emittable =
        EmittableBox().also {
            it.modifier = modifier
            it.contentAlignment = contentAlignment
            it.children.addAll(children.map { it.copy() })
        }

    override fun toString(): String =
        "EmittableBox(" +
                "modifier=$modifier, " +
                "contentAlignment=$contentAlignment" +
                "children=[\n${childrenToString()}\n]" +
                ")"
}