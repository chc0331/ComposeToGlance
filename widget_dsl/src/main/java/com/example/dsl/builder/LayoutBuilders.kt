package com.example.dsl.builder

import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.BoxLayoutProperty
import com.example.dsl.proto.ColumnLayoutProperty
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.RowLayoutProperty
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.ViewProperty

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

