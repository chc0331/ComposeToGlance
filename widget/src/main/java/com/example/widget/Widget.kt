package com.example.widget

import com.example.widget.component.WidgetComponent

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

    fun toProto(): com.example.widget.proto.SizeType {
        return when (this) {
            TINY -> com.example.widget.proto.SizeType.SIZE_TYPE_TINY
            SMALL -> com.example.widget.proto.SizeType.SIZE_TYPE_SMALL
            MEDIUM -> com.example.widget.proto.SizeType.SIZE_TYPE_MEDIUM
            else -> com.example.widget.proto.SizeType.SIZE_TYPE_LARGE
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
        SizeType.TINY -> 1 to 1
        SizeType.SMALL -> 2 to 1
        SizeType.MEDIUM -> 2 to 2
        else -> 4 to 2
    }
}
