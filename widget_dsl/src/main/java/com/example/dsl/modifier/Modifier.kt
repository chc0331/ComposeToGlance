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
 *     modifier = DslModifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(start = 16.dp, top = 8.dp)
 * )
 * ```
 */
interface DslModifier {
    /**
     * Modifier 체이닝을 위한 내부 클래스
     */
    data class Combined(
        val outer: DslModifier,
        val inner: DslModifier
    ) : DslModifier {
        override fun toString(): String = "DslModifier.Combined($outer, $inner)"
    }

    /**
     * Width 설정
     */
    data class WidthModifier(val dimension: Dimension) : DslModifier

    /**
     * Height 설정
     */
    data class HeightModifier(val dimension: Dimension) : DslModifier

    /**
     * Padding 설정
     */
    data class PaddingModifier(val padding: Padding) : DslModifier

    /**
     * CornerRadius 설정
     */
    data class CornerRadiusModifier(val cornerRadius: CornerRadius) : DslModifier

    /**
     * Semantics 설정
     */
    data class SemanticsModifier(val semantics: Semantics) : DslModifier

    /**
     * ClickAction 설정
     */
    data class ClickActionModifier(val action: Action) : DslModifier

    /**
     * BackgroundColor 설정
     */
    data class BackgroundColorModifier(val colorProvider: ColorProvider) : DslModifier

    /**
     * ViewId 설정
     */
    data class ViewIdModifier(val viewId: Int) : DslModifier

    /**
     * PartiallyUpdate 설정
     */
    data class PartiallyUpdateModifier(val partiallyUpdate: Boolean) : DslModifier

    /**
     * Hide 설정
     */
    data class HideModifier(val hide: Boolean) : DslModifier

    /**
     * Modifier 체이닝
     */
    infix fun then(other: DslModifier): DslModifier {
        return when {
            this === DslModifier -> other
            other === DslModifier -> this
            else -> Combined(this, other)
        }
    }

    /**
     * The companion object `DslModifier` is the empty, default, or starter [DslModifier] that
     * contains no elements. Use it to create a new [DslModifier] using modifier
     * extension factory functions.
     */
    public companion object : DslModifier {
        override infix fun then(other: DslModifier): DslModifier = other
        override fun toString(): String = "DslModifier"
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
fun DslModifier.padding(padding: Padding): DslModifier {
    return this then DslModifier.PaddingModifier(padding)
}

/**
 * CornerRadius 설정
 */
fun DslModifier.cornerRadius(cornerRadius: CornerRadius): DslModifier {
    return this then DslModifier.CornerRadiusModifier(cornerRadius)
}

/**
 * Semantics 설정
 */
fun DslModifier.semantics(semantics: Semantics): DslModifier {
    return this then DslModifier.SemanticsModifier(semantics)
}

/**
 * ClickAction 설정
 */
fun DslModifier.clickAction(action: Action): DslModifier {
    return this then DslModifier.ClickActionModifier(action)
}

/**
 * BackgroundColor 설정
 */
fun DslModifier.backgroundColor(colorProvider: ColorProvider): DslModifier {
    return this then DslModifier.BackgroundColorModifier(colorProvider)
}

/**
 * ViewId 설정
 */
fun DslModifier.viewId(viewId: Int): DslModifier {
    return this then DslModifier.ViewIdModifier(viewId)
}

/**
 * PartiallyUpdate 설정
 */
fun DslModifier.partiallyUpdate(partiallyUpdate: Boolean): DslModifier {
    return this then DslModifier.PartiallyUpdateModifier(partiallyUpdate)
}

/**
 * Hide 설정
 */
fun DslModifier.hide(hide: Boolean): DslModifier {
    return this then DslModifier.HideModifier(hide)
}

