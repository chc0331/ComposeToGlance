package com.example.toolkit

import androidx.compose.runtime.AbstractApplier

public class Applier(root: EmittableWithChildren) : AbstractApplier<Emittable>(root) {
    private val newRootMaxDepth = root.maxDepth

    override fun onClear() {
        (root as EmittableWithChildren).children.clear()
    }

    override fun insertBottomUp(index: Int, instance: Emittable) {
        // Ignored, the tree is built top-down.
    }

    override fun insertTopDown(index: Int, instance: Emittable) {
        val parent = current as EmittableWithChildren
        require(parent.maxDepth > 0) {
            "Too many embedded views for the current surface. The maximum depth is: " +
                    "${(root as EmittableWithChildren).maxDepth}"
        }
        if (instance is EmittableWithChildren) {
            instance.maxDepth =
                if (instance.resetsDepthForChildren) {
                    newRootMaxDepth
                } else {
                    parent.maxDepth - 1
                }
        }
        currentChildren.add(index, instance)
    }

    override fun move(from: Int, to: Int, count: Int) {
        currentChildren.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        currentChildren.remove(index, count)
    }

    private val currentChildren: MutableList<Emittable>
        get() {
            current.let {
                if (it is EmittableWithChildren) {
                    return it.children
                }
            }
            throw IllegalStateException("Current node cannot accept children")
        }
}