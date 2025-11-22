package com.example.toolkit.glance.converter

import androidx.compose.ui.unit.dp
import androidx.glance.unit.Dimension
import com.example.composetoglance.proto.Dimension as ProtoDimension

/**
 * Proto Dimension을 Glance Dimension으로 변환
 */
object DimensionConverter {
    /**
     * Proto Dimension을 Glance Dimension으로 변환
     * @param protoDimension Proto Dimension
     * @return Glance Dimension 또는 null (wrap_content/match_parent는 null 반환)
     */
    fun toGlanceDimension(protoDimension: ProtoDimension): Dimension? {
        return when {
            protoDimension.hasDp() -> {
                Dimension.Dp(protoDimension.dp.value.dp)
            }
            protoDimension.wrapContent -> {
                Dimension.Wrap
            }
            protoDimension.matchParent -> {
                Dimension.Expand
            }
            protoDimension.hasWeight() -> {
                // Glance는 weight를 직접 지원하지 않으므로 Expand로 변환
                Dimension.Expand
            }
            else -> null
        }
    }

    /**
     * Proto Dimension이 wrap_content인지 확인
     */
    fun isWrapContent(protoDimension: ProtoDimension): Boolean {
        return protoDimension.wrapContent
    }

    /**
     * Proto Dimension이 match_parent인지 확인
     */
    fun isMatchParent(protoDimension: ProtoDimension): Boolean {
        return protoDimension.matchParent
    }

    /**
     * Proto Dimension이 DP 값인지 확인
     */
    fun hasDpValue(protoDimension: ProtoDimension): Boolean {
        return protoDimension.hasDp()
    }

    /**
     * Proto Dimension에서 DP 값을 추출
     */
    fun getDpValue(protoDimension: ProtoDimension): Float? {
        return if (protoDimension.hasDp()) protoDimension.dp.value else null
    }
}

