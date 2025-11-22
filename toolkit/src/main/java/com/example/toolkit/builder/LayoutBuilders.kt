package com.example.toolkit.builder

import com.example.toolkit.proto.AlignmentType
import com.example.toolkit.proto.BoxLayoutProperty
import com.example.toolkit.proto.ColumnLayoutProperty
import com.example.toolkit.proto.HorizontalAlignment
import com.example.toolkit.proto.RowLayoutProperty
import com.example.toolkit.proto.VerticalAlignment
import com.example.toolkit.proto.ViewProperty

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

