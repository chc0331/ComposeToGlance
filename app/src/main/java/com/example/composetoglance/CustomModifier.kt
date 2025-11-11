package com.example.composetoglance

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.currentCompositeKeyHashCode
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.node.ModifierNodeElement

import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.InteroperableComposeUiNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.viewinterop.InteropView
import java.util.concurrent.atomic.AtomicInteger


val ROOT_ID = 0
private val id = AtomicInteger(ROOT_ID)
fun generateUniqueId() = id.incrementAndGet()

// Modifier factory
fun Modifier.track(id: String) = this then TrackElement(id)

// ModifierNodeElement
private data class TrackElement(val id: String) : ModifierNodeElement<TrackNode>() {
    override fun create() = TrackNode(id).apply {
        Log.i("heec.choi", "TrackElement - $id")
    }

    override fun update(node: TrackNode) {
        Log.i("heec.choi", "Update / $node $id")
        node.id = id
    }
}

// Modifier.Node
private class TrackNode(var id: String) : Modifier.Node(), ObserverModifierNode, SemanticsModifierNode {
    override fun onAttach() {
        super.onAttach()
        Log.i("heec.choi", "onAttach / $id")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("heec.choi", "onDetach / $id")
    }

    override fun onReset() {
        super.onReset()
        Log.i("heec.choi", "onReset / $id")
    }

    override fun onObservedReadsChanged() {
        Log.i("heec.choi", "onObservedReadsChanged")
    }

    override fun SemanticsPropertyReceiver.applySemantics() {

    }
}



