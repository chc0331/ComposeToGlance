package com.example.composetoglance.builder

import com.example.composetoglance.proto.Action
import com.example.composetoglance.proto.Alignment
import com.example.composetoglance.proto.AlignmentType
import com.example.composetoglance.proto.CornerRadius
import com.example.composetoglance.proto.Dimension
import com.example.composetoglance.proto.HorizontalAlignment
import com.example.composetoglance.proto.Padding
import com.example.composetoglance.proto.Semantics
import com.example.composetoglance.proto.VerticalAlignment
import com.example.composetoglance.proto.ViewProperty

/**
 * ViewProperty, Alignment
 * */

fun viewProperty(
    viewId: Int,
    width: Dimension = wrapContentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    cornerRadius: CornerRadius? = null,
    semantics: Semantics? = null,
    clickAction: Action? = null
): ViewProperty = ViewProperty.newBuilder().setViewId(viewId).setWidth(width).setHeight(height)
    .apply { padding?.let { setPadding(it) } }
    .apply { cornerRadius?.let { setCornerRadius(it) } }
    .apply { semantics?.let { setSemantics(it) } }
    .apply { clickAction?.let { setClickAction(it) } }.build()

fun alignment(
    alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_UNSPECIFIED,
    horizontal: HorizontalAlignment = HorizontalAlignment.H_ALIGN_UNSPECIFIED,
    vertical: VerticalAlignment = VerticalAlignment.V_ALIGN_UNSPECIFIED
): Alignment = Alignment.newBuilder().setAlignment(alignment).setHorizontal(horizontal)
    .setVertical(vertical).build()



