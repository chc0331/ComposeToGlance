package com.example.dsl.glance.converter

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dsl.proto.Padding

/**
 * Proto Padding을 Glance Padding으로 변환
 */
object PaddingConverter {
    /**
     * Proto Padding의 각 방향 값을 반환
     * @param protoPadding Proto Padding
     * @return Pair<start, top, end, bottom> in dp
     */
    fun toGlancePaddingValues(protoPadding: Padding): PaddingValues {
        val start = if (protoPadding.hasStart()) protoPadding.start.value.dp else 0.dp
        val top = if (protoPadding.hasTop()) protoPadding.top.value.dp else 0.dp
        val end = if (protoPadding.hasEnd()) protoPadding.end.value.dp else 0.dp
        val bottom = if (protoPadding.hasBottom()) protoPadding.bottom.value.dp else 0.dp

        return PaddingValues(
            start = start,
            top = top,
            end = end,
            bottom = bottom
        )
    }

    /**
     * 모든 방향이 0인지 확인
     */
    fun isEmpty(protoPadding: Padding): Boolean {
        val start = if (protoPadding.hasStart()) protoPadding.start.value else 0f
        val top = if (protoPadding.hasTop()) protoPadding.top.value else 0f
        val end = if (protoPadding.hasEnd()) protoPadding.end.value else 0f
        val bottom = if (protoPadding.hasBottom()) protoPadding.bottom.value else 0f

        return start == 0f && top == 0f && end == 0f && bottom == 0f
    }
}

/**
 * Padding 값들을 담는 데이터 클래스
 */
data class PaddingValues(
    val start: Dp,
    val top: Dp,
    val end: Dp,
    val bottom: Dp
)

