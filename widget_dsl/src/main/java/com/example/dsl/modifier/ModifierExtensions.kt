package com.example.dsl.modifier

import com.example.dsl.proto.Color
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Dp
import com.example.dsl.proto.Padding
import com.example.dsl.syntax.builder.Dp as DpBuilder

/**
 * Modifier 편의 확장 함수들
 * 
 * Dimension, Padding, CornerRadius 등을 간편하게 생성할 수 있는 함수들을 제공합니다.
 */

// ==================== Dimension 편의 함수 ====================

/**
 * Dp 값 타입 (타입 안전성을 위한 래퍼)
 */
@JvmInline
value class DpValue(val value: Float)

/**
 * Float를 Dp로 변환
 */
val Float.dp: DpValue get() = DpValue(this)

/**
 * Int를 Dp로 변환
 */
val Int.dp: DpValue get() = DpValue(this.toFloat())

/**
 * wrapContent Dimension
 */
val wrapContent: Dimension = Dimension.newBuilder().setWrapContent(true).build()

/**
 * matchParent Dimension
 */
val matchParent: Dimension = Dimension.newBuilder().setMatchParent(true).build()

/**
 * Dp 값으로 Dimension 생성
 */
fun DimensionDp(value: Float): Dimension {
    return Dimension.newBuilder()
        .setDp(Dp.newBuilder().setValue(value).build())
        .build()
}

/**
 * Weight로 Dimension 생성
 */
fun DimensionWeight(weight: Float): Dimension {
    return Dimension.newBuilder()
        .setWeight(weight)
        .build()
}

/**
 * Modifier에 width를 Dp 값으로 설정
 */
fun Modifier.width(value: DpValue): Modifier {
    val dimension = DimensionDp(value.value)
    return this then Modifier.WidthModifier(dimension)
}

/**
 * Modifier에 width를 Float 값으로 설정
 */
fun Modifier.width(value: Float): Modifier {
    val dimension = DimensionDp(value)
    return this then Modifier.WidthModifier(dimension)
}

/**
 * Modifier에 width를 fillMaxWidth (matchParent)로 설정
 */
fun Modifier.fillMaxWidth(): Modifier {
    return this then Modifier.WidthModifier(matchParent)
}

/**
 * Modifier에 width를 wrapContentWidth로 설정
 */
fun Modifier.wrapContentWidth(): Modifier {
    return this then Modifier.WidthModifier(wrapContent)
}

/**
 * Modifier에 height를 fillMaxHeight (matchParent)로 설정
 */
fun Modifier.fillMaxHeight(): Modifier {
    return this then Modifier.HeightModifier(matchParent)
}

/**
 * Modifier에 height를 wrapContentHeight로 설정
 */
fun Modifier.wrapContentHeight(): Modifier {
    return this then Modifier.HeightModifier(wrapContent)
}

/**
 * Modifier에 height를 Dimension으로 설정
 * (Modifier.kt의 height(dimension: Dimension)과 동일하지만 확장 함수로 제공)
 */
fun Modifier.height(dimension: Dimension): Modifier {
    return this then Modifier.HeightModifier(dimension)
}

/**
 * Modifier에 height를 Dp 값으로 설정
 */
fun Modifier.height(value: DpValue): Modifier {
    val dimension = DimensionDp(value.value)
    return this then Modifier.HeightModifier(dimension)
}

/**
 * Modifier에 height를 Float 값으로 설정
 */
fun Modifier.height(value: Float): Modifier {
    val dimension = DimensionDp(value)
    return this then Modifier.HeightModifier(dimension)
}

// ==================== Padding 편의 함수 ====================

/**
 * Padding을 생성하는 편의 함수
 */
fun Padding(
    all: Float? = null,
    horizontal: Float? = null,
    vertical: Float? = null,
    start: Float = 0f,
    top: Float = 0f,
    end: Float = 0f,
    bottom: Float = 0f
): Padding {
    val finalStart = all ?: horizontal ?: start
    val finalTop = all ?: vertical ?: top
    val finalEnd = all ?: horizontal ?: end
    val finalBottom = all ?: vertical ?: bottom

    return Padding.newBuilder()
        .setStart(DpBuilder(finalStart))
        .setTop(DpBuilder(finalTop))
        .setEnd(DpBuilder(finalEnd))
        .setBottom(DpBuilder(finalBottom))
        .build()
}

/**
 * Modifier에 padding을 설정 (모든 방향 동일)
 */
fun Modifier.padding(all: Float): Modifier {
    return padding(Padding(all = all))
}

/**
 * Modifier에 padding을 설정 (Dp 값)
 */
fun Modifier.padding(all: DpValue): Modifier {
    return padding(Padding(all = all.value))
}

/**
 * Modifier에 padding을 설정 (수평/수직)
 */
fun Modifier.padding(
    horizontal: Float? = null,
    vertical: Float? = null
): Modifier {
    return padding(Padding(horizontal = horizontal, vertical = vertical))
}

/**
 * Modifier에 padding을 설정 (Dp 값, 수평/수직)
 */
fun Modifier.padding(
    horizontal: DpValue? = null,
    vertical: DpValue? = null
): Modifier {
    return padding(Padding(
        horizontal = horizontal?.value,
        vertical = vertical?.value
    ))
}

/**
 * Modifier에 padding을 설정 (개별 방향)
 */
fun Modifier.padding(
    start: Float = 0f,
    top: Float = 0f,
    end: Float = 0f,
    bottom: Float = 0f
): Modifier {
    return padding(Padding(start = start, top = top, end = end, bottom = bottom))
}

/**
 * Modifier에 padding을 설정 (Dp 값, 개별 방향)
 */
fun Modifier.padding(
    start: DpValue = 0.dp,
    top: DpValue = 0.dp,
    end: DpValue = 0.dp,
    bottom: DpValue = 0.dp
): Modifier {
    return padding(Padding(
        start = start.value,
        top = top.value,
        end = end.value,
        bottom = bottom.value
    ))
}

// ==================== CornerRadius 편의 함수 ====================

/**
 * CornerRadius 생성
 */
fun CornerRadius(radius: Float): CornerRadius {
    return CornerRadius.newBuilder().setRadius(radius).build()
}

/**
 * Modifier에 cornerRadius를 설정
 */
fun Modifier.cornerRadius(radius: Float): Modifier {
    return cornerRadius(CornerRadius(radius))
}

/**
 * Modifier에 cornerRadius를 설정 (Dp 값)
 */
fun Modifier.cornerRadius(radius: DpValue): Modifier {
    return cornerRadius(CornerRadius(radius.value))
}

// ==================== ColorProvider 편의 함수 ====================

/**
 * ColorProvider 생성 (Color만)
 */
fun ColorProvider(colorArgb: Int): ColorProvider {
    return ColorProvider.newBuilder()
        .setColor(Color.newBuilder().setArgb(colorArgb).build())
        .build()
}

/**
 * Modifier에 backgroundColor를 설정 (ARGB Int 값)
 */
fun Modifier.backgroundColor(colorArgb: Int): Modifier {
    return backgroundColor(ColorProvider(colorArgb))
}

