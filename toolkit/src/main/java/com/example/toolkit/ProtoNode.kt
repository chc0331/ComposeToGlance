package com.example.toolkit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.Updater

@Composable
inline fun <T : Emittable> ProtoNode(
    noinline factory: () -> T,
    update: @DisallowComposableCalls Updater<T>.() -> Unit,
) {
    ComposeNode<T, Applier>(factory, update)
}

@Composable
inline fun <T : Emittable> ProtoNode(
    noinline factory: () -> T,
    update: @DisallowComposableCalls Updater<T>.() -> Unit,
    content: @Composable @ProtoComposable () -> Unit,
) {
    ComposeNode<T, Applier>(factory, update, content)
}