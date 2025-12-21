package com.widgetkit.dsl.proto.builder

import com.widgetkit.dsl.proto.Dimension
import com.widgetkit.dsl.proto.Dp

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
internal fun Dp(value: Float): Dp = Dp.newBuilder().setValue(value).build()

val wrapContentDimension: Dimension = Dimension.newBuilder().setWrapContent(true).build()
val matchParentDimension: Dimension = Dimension.newBuilder().setMatchParent(true).build()