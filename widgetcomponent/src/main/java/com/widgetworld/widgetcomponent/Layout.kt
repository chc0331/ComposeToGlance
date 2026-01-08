package com.widgetworld.widgetcomponent

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

data class GridSpec(val row: Int, val column: Int)

sealed class Layout(
    val name: String, private var divide: Int = 1,
    private val baseRow: Int, private val baseColumn: Int,
    private val baseWidth: Dp, private val baseHeight: Dp
) {

    object Small :
        Layout("Small", baseRow = 1, baseColumn = 2, baseWidth = 135.dp, baseHeight = 80.dp)

    object Medium :
        Layout("Medium", baseRow = 2, baseColumn = 2, baseWidth = 135.dp, baseHeight = 165.dp)

    object Large :
        Layout("Large", baseRow = 2, baseColumn = 4, baseWidth = 280.dp, baseHeight = 165.dp)

    object ExtraLarge :
        Layout("Extra Large", baseRow = 4, baseColumn = 4, baseWidth = 280.dp, baseHeight = 330.dp)

    fun getDpSize(): DpSize {
        return DpSize(baseWidth, baseHeight)
    }

    fun getGridCell(): GridSpec {
        return GridSpec(baseRow * divide, baseColumn * divide)
    }

    fun setDivide(divide: Int) {
        this.divide = divide
    }

    fun getDivide() = this.divide
}