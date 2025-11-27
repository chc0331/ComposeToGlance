package com.example.dsl.builder

import com.example.dsl.proto.Action
import com.example.dsl.proto.Alignment
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.Padding
import com.example.dsl.proto.Semantics
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.ViewProperty

/**
 * ViewProperty, Alignment
 * */

fun ViewProperty(
    viewId: Int,
    width: Dimension = wrapContentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    cornerRadius: CornerRadius? = null,
    semantics: Semantics? = null,
    clickAction: Action? = null,
    backgroundColor: ColorProvider? = null
): ViewProperty = ViewProperty.newBuilder().setViewId(viewId).setWidth(width).setHeight(height)
    .apply { padding?.let { setPadding(it) } }
    .apply { cornerRadius?.let { setCornerRadius(it) } }
    .apply { semantics?.let { setSemantics(it) } }
    .apply { clickAction?.let { setClickAction(it) } }
    .apply { backgroundColor?.let { setBackgroundColor(it) } }.build()

fun Alignment(
    alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_UNSPECIFIED,
    horizontal: HorizontalAlignment = HorizontalAlignment.H_ALIGN_UNSPECIFIED,
    vertical: VerticalAlignment = VerticalAlignment.V_ALIGN_UNSPECIFIED
): Alignment = Alignment.newBuilder().setAlignment(alignment).setHorizontal(horizontal)
    .setVertical(vertical).build()
