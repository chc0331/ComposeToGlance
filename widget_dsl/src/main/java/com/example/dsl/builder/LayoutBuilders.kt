package com.example.dsl.builder

import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.BoxLayoutProperty
import com.example.dsl.proto.ColumnLayoutProperty
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.RowLayoutProperty
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.ViewProperty

/**
 * LayoutProperty 관련 간단한 빌더 함수
 * 
 * 이 파일은 파라미터를 직접 받는 간단한 빌더 함수를 포함합니다.
 * - BoxLayoutProperty(viewProperty, alignment)
 * - RowLayoutProperty(viewProperty, horizontalAlignment, verticalAlignment)
 * - ColumnLayoutProperty(viewProperty, horizontalAlignment, verticalAlignment)
 * 
 * DSL 클래스 및 block을 받는 DSL 빌더 함수는 LayoutPropertyDsl.kt를 참조하세요.
 */

fun BoxLayoutProperty(
    viewProperty: ViewProperty, alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_START
): BoxLayoutProperty = BoxLayoutProperty.newBuilder().setViewProperty(viewProperty)
    .setContentAlignment(alignment).build()

fun RowLayoutProperty(
    viewProperty: ViewProperty,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_START,
    verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP
): RowLayoutProperty = RowLayoutProperty.newBuilder().setViewProperty(viewProperty)
    .setHorizontalAlignment(horizontalAlignment).setVerticalAlignment(verticalAlignment).build()

fun ColumnLayoutProperty(
    viewProperty: ViewProperty,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_START,
    verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP
): ColumnLayoutProperty = ColumnLayoutProperty.newBuilder().setViewProperty(viewProperty)
    .setHorizontalAlignment(horizontalAlignment).setVerticalAlignment(verticalAlignment).build()

