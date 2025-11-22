package com.example.toolkit.builder

import com.example.toolkit.proto.CornerRadius
import com.example.toolkit.proto.Dimension
import com.example.toolkit.proto.Dp
import com.example.toolkit.proto.Padding

/**
 * Dp / Dimension / Padding / CornerRadius
 * */
fun dp(value: Float): Dp = Dp.newBuilder().setValue(value).build()

fun dimensionDp(value: Float): Dimension = Dimension.newBuilder().setDp(dp(value)).build()

fun dimensionWeight(weight: Float): Dimension = Dimension.newBuilder().setWeight(weight).build()

val wrapContentDimension: Dimension = Dimension.newBuilder().setWrapContent(true).build()
val matchParentDimension: Dimension = Dimension.newBuilder().setMatchParent(true).build()

fun padding(
    start: Float = 0f, top: Float = 0f, end: Float = 0f, bottom: Float = 0f
): Padding = Padding.newBuilder().setStart(dp(start)).setTop(dp(top)).setEnd(dp(end))
    .setBottom(dp(bottom)).build()

fun cornerRadius(all: Float): CornerRadius = CornerRadius.newBuilder().setRadius(all).build()

