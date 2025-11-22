package com.example.composetoglance.builder

import com.example.composetoglance.proto.AlignmentType
import com.example.composetoglance.proto.BoxLayoutProperty
import com.example.composetoglance.proto.ColumnLayoutProperty
import com.example.composetoglance.proto.HorizontalAlignment
import com.example.composetoglance.proto.RowLayoutProperty
import com.example.composetoglance.proto.VerticalAlignment
import com.example.composetoglance.proto.ViewProperty

fun boxLayoutProperty(
    viewProperty: ViewProperty, alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_START
): BoxLayoutProperty = BoxLayoutProperty.newBuilder().setViewProperty(viewProperty)
    .setContentAlignment(alignment).build()

fun rowLayoutProperty(
    viewProperty: ViewProperty,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_START,
    verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP
): RowLayoutProperty = RowLayoutProperty.newBuilder().setViewProperty(viewProperty)
    .setHorizontalAlignment(horizontalAlignment).setVerticalAlignment(verticalAlignment).build()

fun columnLayoutProperty(
    viewProperty: ViewProperty,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_START,
    verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP
): ColumnLayoutProperty = ColumnLayoutProperty.newBuilder().setViewProperty(viewProperty)
    .setHorizontalAlignment(horizontalAlignment).setVerticalAlignment(verticalAlignment).build()


