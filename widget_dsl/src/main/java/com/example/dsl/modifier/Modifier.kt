package com.example.dsl.modifier

import android.content.ComponentName
import android.content.Context
import com.example.dsl.action.RunWidgetCallbackAction
import com.example.dsl.action.toBytes
import com.example.dsl.proto.Action
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.Component
import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Padding
import com.example.dsl.proto.Semantics
import com.google.protobuf.ByteString

/**
 * Widget DSL Modifier
 *
 * Jetpack Compose 스타일의 Modifier를 제공하여 ViewProperty 설정을 간소화합니다.
 *
 * 사용 예시:
 * ```
 * Text(
 *     text = "Hello",
 *     modifier = WidgetModifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(start = 16.dp, top = 8.dp)
 * )
 * ```
 */
interface WidgetModifier {
    /**
     * Modifier 체이닝을 위한 내부 클래스
     */
    data class Combined(
        val outer: WidgetModifier,
        val inner: WidgetModifier
    ) : WidgetModifier {
        override fun toString(): String = "WidgetModifier.Combined($outer, $inner)"
    }

    /**
     * Width 설정
     */
    data class WidthModifier(val dimension: Dimension) : WidgetModifier

    /**
     * Height 설정
     */
    data class HeightModifier(val dimension: Dimension) : WidgetModifier

    /**
     * Padding 설정
     */
    data class PaddingModifier(val padding: Padding) : WidgetModifier

    /**
     * CornerRadius 설정
     */
    data class CornerRadiusModifier(val cornerRadius: CornerRadius) : WidgetModifier

    /**
     * Semantics 설정
     */
    data class SemanticsModifier(val semantics: Semantics) : WidgetModifier

    /**
     * ClickAction 설정
     */
    data class ClickActionModifier(val action: Action) : WidgetModifier

    data class LambdaActionModifier(val action: () -> Unit) : WidgetModifier

    /**
     * BackgroundColor 설정
     */
    data class BackgroundColorModifier(val colorProvider: ColorProvider) : WidgetModifier

    /**
     * ViewId 설정
     */
    data class ViewIdModifier(val viewId: Int) : WidgetModifier

    /**
     * PartiallyUpdate 설정
     */
    data class PartiallyUpdateModifier(val partiallyUpdate: Boolean) : WidgetModifier

    /**
     * Hide 설정
     */
    data class HideModifier(val hide: Boolean) : WidgetModifier

    /**
     * Modifier 체이닝
     */
    infix fun then(other: WidgetModifier): WidgetModifier {
        return when {
            this === WidgetModifier -> other
            other === WidgetModifier -> this
            else -> Combined(this, other)
        }
    }

    /**
     * The companion object `WidgetModifier` is the empty, default, or starter [WidgetModifier] that
     * contains no elements. Use it to create a new [WidgetModifier] using modifier
     * extension factory functions.
     */
    public companion object : WidgetModifier {
        override infix fun then(other: WidgetModifier): WidgetModifier = other
        override fun toString(): String = "WidgetModifier"
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
fun WidgetModifier.padding(padding: Padding): WidgetModifier {
    return this then WidgetModifier.PaddingModifier(padding)
}

/**
 * CornerRadius 설정
 */
fun WidgetModifier.cornerRadius(cornerRadius: CornerRadius): WidgetModifier {
    return this then WidgetModifier.CornerRadiusModifier(cornerRadius)
}

/**
 * Semantics 설정
 */
fun WidgetModifier.semantics(semantics: Semantics): WidgetModifier {
    return this then WidgetModifier.SemanticsModifier(semantics)
}

/**
 * ClickAction 설정 (Activity Component 정보를 직접 전달)
 *
 * @param componentName Activity의 ComponentName
 *
 * 사용 예시:
 * ```
 * Text(
 *     text = "Open Settings",
 *     modifier = WidgetModifier
 *         .clickAction(
 *             ComponentName("com.example.app", "com.example.app.SettingsActivity")
 *         )
 * )
 * ```
 */
fun WidgetModifier.clickAction(componentName: ComponentName): WidgetModifier {
    val component = Component.newBuilder()
        .setPackageName(componentName.packageName)
        .setClassName(componentName.className)
        .build()

    val action = Action.newBuilder()
        .setActivity(true)
        .setComponent(component)
        .build()

    return this then WidgetModifier.ClickActionModifier(action)
}

fun WidgetModifier.clickAction(
    context: Context,
    action: RunWidgetCallbackAction
): WidgetModifier {
    val component = Component.newBuilder()
        .setPackageName(context.packageName)
        .setClassName(action.receiverClass.name)
        .build()

    val action = Action.newBuilder()
        .setBroadcastReceiver(true)
        .setComponent(component)
        .setActionParameters(ByteString.copyFrom(action.parameters.toBytes()))
        .build()

    return this then WidgetModifier.ClickActionModifier(action)
}

/**
 * BackgroundColor 설정
 */
fun WidgetModifier.backgroundColor(colorProvider: ColorProvider): WidgetModifier {
    return this then WidgetModifier.BackgroundColorModifier(colorProvider)
}

/**
 * ViewId 설정
 */
fun WidgetModifier.viewId(viewId: Int): WidgetModifier {
    return this then WidgetModifier.ViewIdModifier(viewId)
}

/**
 * PartiallyUpdate 설정
 */
fun WidgetModifier.partiallyUpdate(partiallyUpdate: Boolean): WidgetModifier {
    return this then WidgetModifier.PartiallyUpdateModifier(partiallyUpdate)
}

/**
 * Hide 설정
 */
fun WidgetModifier.hide(hide: Boolean): WidgetModifier {
    return this then WidgetModifier.HideModifier(hide)
}



