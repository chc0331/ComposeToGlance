package com.example.toolkit.component

import ProtoModifier
import android.R.attr.maxLines
import com.example.composetoglance.proto.Color
import com.example.toolkit.Emittable

class EmittableButton : EmittableWithText() {
    override var modifier: ProtoModifier = ProtoModifier
    var backgroundColor: Color? = null

    override fun copy(): Emittable =
        EmittableButton().also {
            it.modifier = modifier
            it.text = text
            it.style = style
            it.backgroundColor = backgroundColor
        }

    override fun toString(): String =
        "EmittableButton('$text', style=$style, " +
                "backgroundColor=$backgroundColor modifier=$modifier, maxLines=$maxLines)"
}