package com.widgetkit.dsl.proto.property

import com.widgetkit.dsl.proto.Action
import com.widgetkit.dsl.proto.ActionDsl
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.CornerRadius
import com.widgetkit.dsl.proto.Dimension
import com.widgetkit.dsl.proto.Padding
import com.widgetkit.dsl.proto.Semantics
import com.widgetkit.dsl.proto.SemanticsDsl
import com.widgetkit.dsl.proto.ViewProperty

/**
 * ViewProperty 및 Alignment 관련 DSL 클래스 및 DSL 빌더 함수
 *
 * 이 파일은 block을 받는 DSL 빌더 함수와 DSL 클래스를 포함합니다.
 * - DSL 클래스: ViewPropertyDsl, AlignmentDsl
 * - DSL 빌더 함수: ViewProperty(block), Alignment(block)
 *
 * 간단한 빌더 함수(파라미터를 직접 받는)는 ViewPropertyBuilders.kt를 참조하세요.
 */

/**
 * ViewProperty DSL
 */
class ViewPropertyDsl(private val builder: ViewProperty.Builder) {
    var viewId: Int
        get() = builder.viewId
        set(value) {
            builder.setViewId(value)
        }

    fun Width(block: DimensionDsl.() -> Unit) {
        val dimensionBuilder = Dimension.newBuilder()
        DimensionDsl(dimensionBuilder).block()
        builder.setWidth(dimensionBuilder.build())
    }

    fun Height(block: DimensionDsl.() -> Unit) {
        val dimensionBuilder = Dimension.newBuilder()
        DimensionDsl(dimensionBuilder).block()
        builder.setHeight(dimensionBuilder.build())
    }

    fun Padding(block: PaddingDsl.() -> Unit) {
        val paddingBuilder = Padding.newBuilder()
        PaddingDsl(paddingBuilder).block()
        builder.setPadding(paddingBuilder.build())
    }

    fun CornerRadius(block: CornerRadiusDsl.() -> Unit) {
        val cornerRadiusBuilder = CornerRadius.newBuilder()
        CornerRadiusDsl(cornerRadiusBuilder).block()
        builder.setCornerRadius(cornerRadiusBuilder.build())
    }

    fun Semantics(block: SemanticsDsl.() -> Unit) {
        val semanticsBuilder = Semantics.newBuilder()
        SemanticsDsl(semanticsBuilder).block()
        builder.setSemantics(semanticsBuilder.build())
    }

    fun ClickAction(block: ActionDsl.() -> Unit) {
        val actionBuilder = Action.newBuilder()
        ActionDsl(actionBuilder).block()
        builder.setClickAction(actionBuilder.build())
    }

    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setBackgroundColor(colorProviderBuilder.build())
    }

    var partiallyUpdate: Boolean
        get() = builder.partiallyUpdate
        set(value) {
            builder.setPartiallyUpdate(value)
        }

    var hide: Boolean
        get() = builder.hide
        set(value) {
            builder.setHide(value)
        }
}