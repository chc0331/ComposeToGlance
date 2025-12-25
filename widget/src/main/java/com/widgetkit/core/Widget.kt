package com.widgetkit.core

import com.widgetkit.core.component.WidgetComponent

enum class SizeType {
    TINY, SMALL, MEDIUM, LARGE;

    override fun toString(): String {
        return when (this) {
            TINY -> "Tiny"
            SMALL -> "Small"
            MEDIUM -> "Medium"
            else -> "Large"
        }
    }

    fun toProto(): com.widgetkit.core.proto.SizeType {
        return when (this) {
            TINY -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_TINY
            SMALL -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_SMALL
            MEDIUM -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_MEDIUM
            else -> com.widgetkit.core.proto.SizeType.SIZE_TYPE_LARGE
        }
    }

    companion object {
        fun getSizeType(value: String): SizeType {
            return when (value) {
                "Tiny" -> TINY
                "Small" -> SMALL
                "Medium" -> MEDIUM
                else -> LARGE
            }
        }
    }
}

/**
 * 위젯 사이즈 타입을 파싱하여 그리드에서 차지하는 셀 수를 반환
 * @return Pair<width in cells, height in cells>
 */
fun WidgetComponent.getSizeInCells(): Pair<Int, Int> {
    return when (getSizeType()) {
        SizeType.TINY -> 2 to 2
        SizeType.SMALL -> 4 to 2
        SizeType.MEDIUM -> 4 to 4
        else -> 8 to 4
    }
}
