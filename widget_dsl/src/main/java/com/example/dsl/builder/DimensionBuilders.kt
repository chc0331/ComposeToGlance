package com.example.dsl.builder

import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Dp
import com.example.dsl.proto.Padding

/**
 * Dp / Dimension / Padding / CornerRadius
 * */
fun Dp(value: Float): Dp = Dp.newBuilder().setValue(value).build()

fun DimensionDp(value: Float): Dimension = Dimension.newBuilder().setDp(Dp(value)).build()

fun DimensionWeight(weight: Float): Dimension = Dimension.newBuilder().setWeight(weight).build()

val wrapContentDimension: Dimension = Dimension.newBuilder().setWrapContent(true).build()
val matchParentDimension: Dimension = Dimension.newBuilder().setMatchParent(true).build()

fun Padding(
    start: Float = 0f, top: Float = 0f, end: Float = 0f, bottom: Float = 0f
): Padding = Padding.newBuilder().setStart(Dp(start)).setTop(Dp(top)).setEnd(Dp(end))
    .setBottom(Dp(bottom)).build()

fun CornerRadius(all: Float): CornerRadius = CornerRadius.newBuilder().setRadius(all).build()

