package com.widgetkit.core

import com.widgetkit.core.component.WidgetComponent

enum class SizeType {
    TINY, SMALL, MEDIUM, MEDIUM_PLUS, LARGE;

    override fun toString(): String {
        return when (this) {
            TINY -> "Tiny"
            SMALL -> "Small"
            MEDIUM -> "Medium"
            MEDIUM_PLUS -> "Medium Plus"
            else -> "Large"
        }
    }

    fun toProto(): com.widgetkit.core.proto.SizeType {
        return when (this) {
            TINY -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_TINY
            SMALL -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_SMALL
            MEDIUM -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_MEDIUM
            MEDIUM_PLUS -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_MEDIUM_PLUS
            else -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_LARGE
        }
    }

    companion object {
        fun getSizeType(value: String): SizeType {
            return when (value) {
                "Tiny" -> TINY
                "Small" -> SMALL
                "Medium" -> MEDIUM
                "Medium Plus" -> MEDIUM_PLUS
                "ExtraLarge" -> LARGE
                else -> LARGE
            }
        }
    }
}

/**
 * 위젯 사이즈 타입을 파싱하여 그리드에서 차지하는 셀 수를 반환 (1x 그리드 기준)
 * 1x 그리드 기준: Tiny = 1x1, Small = 2x1, Medium = 2x2
 * @return Pair<width in cells, height in cells>
 */
fun WidgetComponent.getSizeInCells(): Pair<Int, Int> {
    return when (getSizeType()) {
        SizeType.TINY -> 1 to 1
        SizeType.SMALL -> 2 to 1
        SizeType.MEDIUM -> 2 to 2
        SizeType.MEDIUM_PLUS -> 3 to 2
        else -> 4 to 2
    }
}

/**
 * 동적 그리드에서 위젯이 차지하는 셀 수를 계산 (기본 사이즈 × 그리드 배수)
 * @param gridMultiplier 그리드 배수 (1, 2, 4, 6)
 * @return Pair<width in cells, height in cells>
 */
fun WidgetComponent.getSizeInCells(gridMultiplier: Int): Pair<Int, Int> {
    val baseSizeInCells = getSizeInCells()
    val validMultiplier = if (gridMultiplier in listOf(1, 2, 4, 6)) gridMultiplier else 1
    
    return Pair(
        baseSizeInCells.first * validMultiplier,
        baseSizeInCells.second * validMultiplier
    )
}

/**
 * 레이아웃 타입에 따른 위젯 사이즈 계산
 * @param layoutType 레이아웃 타입
 * @param gridMultiplier 그리드 배수
 * @return Pair<width in cells, height in cells>
 */
fun WidgetComponent.getSizeInCellsForLayout(layoutType: String, gridMultiplier: Int = 1): Pair<Int, Int> {
    return getSizeInCells(gridMultiplier)
}
