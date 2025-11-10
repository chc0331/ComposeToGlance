package com.example.composetoglance

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.ModifierLocalModifierNode
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize

class Logger {
    fun log(string: String) {
        Log.i("heec.choi","Log : $string")
        println(string)
    }
}

val loggerLocal = modifierLocalOf { Logger() }

class ProvideLoggerNode(logger: Logger) : ModifierLocalModifierNode, Modifier.Node() {
    override val providedValues = modifierLocalMapOf(loggerLocal to logger)
}

data class ProvideLoggerElement(val logger: Logger) : ModifierNodeElement<ProvideLoggerNode>() {
    override fun create() = ProvideLoggerNode(logger)

    override fun update(node: ProvideLoggerNode) {
        node.provide(loggerLocal, logger)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "provideLogger"
        properties["logger"] = logger
    }
}

class SizeLoggerNode(var id: String) :
    ModifierLocalModifierNode, LayoutAwareModifierNode, Modifier.Node() {
    override fun onRemeasured(size: IntSize) {
        loggerLocal.current.log("The size of $id was $size")
    }
}

data class SizeLoggerElement(val id: String) : ModifierNodeElement<SizeLoggerNode>() {
    override fun create() = SizeLoggerNode(id)

    override fun update(node: SizeLoggerNode) {
        node.id = id
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "logSize"
        properties["id"] = id
    }
}

fun Modifier.logSize(id: String) = this then SizeLoggerElement(id)
fun Modifier.provideLogger(logger: Logger) = this then ProvideLoggerElement(logger)

