package com.example.toolkit

import ProtoModifier

interface Emittable {
    var modifier: ProtoModifier

    fun copy(): Emittable
}

abstract class EmittableWithChildren(
    internal var maxDepth: Int = Int.MAX_VALUE,
    internal val resetsDepthForChildren: Boolean = false
) : Emittable {
    val children: MutableList<Emittable> = mutableListOf()

    protected fun childrenToString(): String = children.joinToString(",\n").prependIndent("  ")
}

fun EmittableWithChildren.addChild(e: Emittable) {
    this.children += e
}

fun EmittableWithChildren.addChildIfNotNull(e: Emittable?) {
    e?.let {
        this.children += it
    }
}