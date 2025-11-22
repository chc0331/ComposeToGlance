package com.example.toolkit.builder

import com.example.toolkit.proto.Action
import com.example.toolkit.proto.Alignment
import com.example.toolkit.proto.AlignmentType
import com.example.toolkit.proto.ColorProvider
import com.example.toolkit.proto.CornerRadius
import com.example.toolkit.proto.Dimension
import com.example.toolkit.proto.HorizontalAlignment
import com.example.toolkit.proto.Padding
import com.example.toolkit.proto.Semantics
import com.example.toolkit.proto.VerticalAlignment
import com.example.toolkit.proto.ViewProperty

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
    clickAction: Action? = null,
    backgroundColor: ColorProvider? = null
): ViewProperty = ViewProperty.newBuilder().setViewId(viewId).setWidth(width).setHeight(height)
    .apply { padding?.let { setPadding(it) } }
    .apply { cornerRadius?.let { setCornerRadius(it) } }
    .apply { semantics?.let { setSemantics(it) } }
    .apply { clickAction?.let { setClickAction(it) } }
    .apply { backgroundColor?.let { setBackgroundColor(it) } }.build()

fun alignment(
    alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_UNSPECIFIED,
    horizontal: HorizontalAlignment = HorizontalAlignment.H_ALIGN_UNSPECIFIED,
    vertical: VerticalAlignment = VerticalAlignment.V_ALIGN_UNSPECIFIED
): Alignment = Alignment.newBuilder().setAlignment(alignment).setHorizontal(horizontal)
    .setVertical(vertical).build()
