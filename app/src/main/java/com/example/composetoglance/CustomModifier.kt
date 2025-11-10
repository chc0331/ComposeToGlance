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
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density

// Modifier factory
fun Modifier.track(id: String) = this then TrackElement(id)

// ModifierNodeElement
private data class TrackElement(val id: String) : ModifierNodeElement<TrackNode>() {
    override fun create() = TrackNode(id).apply {
        Log.i("heec.choi", "TrackElement - $id")
    }

    override fun update(node: TrackNode) {
        node.id = id
    }
}

// Modifier.Node
private class TrackNode(var id: String) : Modifier.Node(), ObserverModifierNode {
    override fun onAttach() {
        super.onAttach()
    }

    override fun onObservedReadsChanged() {
        TODO("Not yet implemented")
    }
}

fun Modifier.customModifier() = composed {
    LocalContext.current

    Modifier
}



