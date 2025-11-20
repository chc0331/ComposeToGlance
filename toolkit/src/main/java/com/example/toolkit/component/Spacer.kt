package com.example.toolkit.component

import ProtoModifier
import com.example.toolkit.Emittable

public class EmittableSpacer : Emittable {
    override var modifier: ProtoModifier = ProtoModifier

    override fun copy(): Emittable = EmittableSpacer().also { it.modifier = modifier }

    override fun toString(): String = "EmittableSpacer(modifier=$modifier)"
}