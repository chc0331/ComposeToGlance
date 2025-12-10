package com.example.dsl.modifier

import com.example.dsl.proto.Action
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Padding
import com.example.dsl.proto.Semantics

/**
 * Widget DSL Modifier
 *
 * Jetpack Compose 스타일의 Modifier를 제공하여 ViewProperty 설정을 간소화합니다.
 *
 * 사용 예시:
 * ```
 * Text(
 *     text = "Hello",
 *     modifier = Modifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(start = 16.dp, top = 8.dp)
 * )
 * ```
 */
interface Modifier {
    /**
     * Modifier 체이닝을 위한 내부 클래스
     */
    data class Combined(
        val outer: Modifier,
        val inner: Modifier
    ) : Modifier {
        override fun toString(): String = "Modifier.Combined($outer, $inner)"
    }

    /**
     * Width 설정
     */
    data class WidthModifier(val dimension: Dimension) : Modifier

    /**
     * Height 설정
     */
    data class HeightModifier(val dimension: Dimension) : Modifier

    /**
     * Padding 설정
     */
    data class PaddingModifier(val padding: Padding) : Modifier

    /**
     * CornerRadius 설정
     */
    data class CornerRadiusModifier(val cornerRadius: CornerRadius) : Modifier

    /**
     * Semantics 설정
     */
    data class SemanticsModifier(val semantics: Semantics) : Modifier

    /**
     * ClickAction 설정
     */
    data class ClickActionModifier(val action: Action) : Modifier

    /**
     * BackgroundColor 설정
     */
    data class BackgroundColorModifier(val colorProvider: ColorProvider) : Modifier

    /**
     * ViewId 설정
     */
    data class ViewIdModifier(val viewId: Int) : Modifier

    /**
     * PartiallyUpdate 설정
     */
    data class PartiallyUpdateModifier(val partiallyUpdate: Boolean) : Modifier

    /**
     * Hide 설정
     */
    data class HideModifier(val hide: Boolean) : Modifier

    /**
     * Modifier 체이닝
     */
    infix fun then(other: Modifier): Modifier {
        return when {
            this === Modifier -> other
            other === Modifier -> this
            else -> Combined(this, other)
        }
    }

    /**
     * The companion object `Modifier` is the empty, default, or starter [Modifier] that
     * contains no elements. Use it to create a new [Modifier] using modifier
     * extension factory functions.
     */
    public companion object : Modifier {
        override infix fun then(other: Modifier): Modifier = other
        override fun toString(): String = "Modifier"
    }
}

/**
 * Modifier 확장 함수들
 *
 * Note: width()와 height()의 Dimension 오버로드는 ModifierExtensions.kt에 정의되어 있습니다.
 */

/**
 * Padding 설정
 */
fun Modifier.padding(padding: Padding): Modifier {
    return this then Modifier.PaddingModifier(padding)
}

/**
 * CornerRadius 설정
 */
fun Modifier.cornerRadius(cornerRadius: CornerRadius): Modifier {
    return this then Modifier.CornerRadiusModifier(cornerRadius)
}

/**
 * Semantics 설정
 */
fun Modifier.semantics(semantics: Semantics): Modifier {
    return this then Modifier.SemanticsModifier(semantics)
}

/**
 * ClickAction 설정
 */
fun Modifier.clickAction(action: Action): Modifier {
    return this then Modifier.ClickActionModifier(action)
}

/**
 * BackgroundColor 설정
 */
fun Modifier.backgroundColor(colorProvider: ColorProvider): Modifier {
    return this then Modifier.BackgroundColorModifier(colorProvider)
}

/**
 * ViewId 설정
 */
fun Modifier.viewId(viewId: Int): Modifier {
    return this then Modifier.ViewIdModifier(viewId)
}

/**
 * PartiallyUpdate 설정
 */
fun Modifier.partiallyUpdate(partiallyUpdate: Boolean): Modifier {
    return this then Modifier.PartiallyUpdateModifier(partiallyUpdate)
}

/**
 * Hide 설정
 */
fun Modifier.hide(hide: Boolean): Modifier {
    return this then Modifier.HideModifier(hide)
}

