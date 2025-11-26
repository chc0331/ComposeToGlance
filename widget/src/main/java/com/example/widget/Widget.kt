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
