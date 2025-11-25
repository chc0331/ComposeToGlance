package com.example.dsl.glance.converter

import androidx.glance.layout.Alignment
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.VerticalAlignment

/**
 * Proto Alignment를 Glance Alignment로 변환
 */
object AlignmentConverter {
    /**
     * Proto AlignmentType을 Glance Alignment로 변환
     */
    fun toGlanceAlignment(alignmentType: AlignmentType): Alignment {
        return when (alignmentType) {
            AlignmentType.ALIGNMENT_TYPE_TOP_START -> Alignment.TopStart
            AlignmentType.ALIGNMENT_TYPE_TOP_CENTER -> Alignment.TopCenter
            AlignmentType.ALIGNMENT_TYPE_TOP_END -> Alignment.TopEnd
            AlignmentType.ALIGNMENT_TYPE_CENTER_START -> Alignment.CenterStart
            AlignmentType.ALIGNMENT_TYPE_CENTER -> Alignment.Center
            AlignmentType.ALIGNMENT_TYPE_CENTER_END -> Alignment.CenterEnd
            AlignmentType.ALIGNMENT_TYPE_BOTTOM_START -> Alignment.BottomStart
            AlignmentType.ALIGNMENT_TYPE_BOTTOM_CENTER -> Alignment.BottomCenter
            AlignmentType.ALIGNMENT_TYPE_BOTTOM_END -> Alignment.BottomEnd
            AlignmentType.ALIGNMENT_TYPE_TOP -> Alignment.Top
            AlignmentType.ALIGNMENT_TYPE_CENTER_VERTICAL -> Alignment.CenterVertically
            AlignmentType.ALIGNMENT_TYPE_BOTTOM -> Alignment.Bottom
            AlignmentType.ALIGNMENT_TYPE_START -> Alignment.Start
            AlignmentType.ALIGNMENT_TYPE_CENTER_HORIZONTAL -> Alignment.CenterHorizontally
            AlignmentType.ALIGNMENT_TYPE_END -> Alignment.End
            else -> Alignment.Start
        } as Alignment
    }

    /**
     * HorizontalAlignment와 VerticalAlignment를 조합하여 Glance Alignment 생성
     */
    fun toGlanceAlignment(
        horizontal: HorizontalAlignment,
        vertical: VerticalAlignment
    ): Alignment {
        val h = when (horizontal) {
            HorizontalAlignment.H_ALIGN_START -> Alignment.Horizontal.Start
            HorizontalAlignment.H_ALIGN_CENTER -> Alignment.Horizontal.CenterHorizontally
            HorizontalAlignment.H_ALIGN_END -> Alignment.Horizontal.End
            else -> Alignment.Horizontal.Start
        }

        val v = when (vertical) {
            VerticalAlignment.V_ALIGN_TOP -> Alignment.Vertical.Top
            VerticalAlignment.V_ALIGN_CENTER -> Alignment.Vertical.CenterVertically
            VerticalAlignment.V_ALIGN_BOTTOM -> Alignment.Vertical.Bottom
            else -> Alignment.Vertical.Top
        }

        return Alignment(h, v)
    }

    /**
     * HorizontalAlignment만으로 Glance Horizontal Alignment 생성
     */
    fun toGlanceHorizontalAlignment(horizontal: HorizontalAlignment): Alignment.Horizontal {
        return when (horizontal) {
            HorizontalAlignment.H_ALIGN_START -> Alignment.Horizontal.Start
            HorizontalAlignment.H_ALIGN_CENTER -> Alignment.Horizontal.CenterHorizontally
            HorizontalAlignment.H_ALIGN_END -> Alignment.Horizontal.End
            else -> Alignment.Horizontal.Start
        }
    }

    /**
     * VerticalAlignment만으로 Glance Vertical Alignment 생성
     */
    fun toGlanceVerticalAlignment(vertical: VerticalAlignment): Alignment.Vertical {
        return when (vertical) {
            VerticalAlignment.V_ALIGN_TOP -> Alignment.Vertical.Top
            VerticalAlignment.V_ALIGN_CENTER -> Alignment.Vertical.CenterVertically
            VerticalAlignment.V_ALIGN_BOTTOM -> Alignment.Vertical.Bottom
            else -> Alignment.Vertical.Top
        }
    }
}

