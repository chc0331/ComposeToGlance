package com.example.dsl.builder

import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Dp
import com.example.dsl.proto.Padding

/**
 * Dimension 관련 간단한 빌더 함수
 * 
 * 이 파일은 파라미터를 직접 받는 간단한 빌더 함수를 포함합니다.
 * - Dp(value: Float)
 * - DimensionDp(value: Float)
 * - DimensionWeight(weight: Float)
 * - Padding(start, top, end, bottom)
 * - CornerRadius(all: Float)
 * - wrapContentDimension, matchParentDimension (상수)
 * 
 * DSL 클래스 및 block을 받는 DSL 빌더 함수는 DimensionDsl.kt를 참조하세요.
 */
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

