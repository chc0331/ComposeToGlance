package com.example.dsl.builder

import com.example.dsl.proto.Action
import com.example.dsl.proto.Alignment
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.Padding
import com.example.dsl.proto.Semantics
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.ViewProperty

/**
 * ViewProperty DSL
 */
class ViewPropertyDsl(private val builder: ViewProperty.Builder) {
    var viewId: Int
        get() = builder.viewId
        set(value) {
            builder.setViewId(value)
        }

    fun width(block: DimensionDsl.() -> Unit) {
        val dimensionBuilder = Dimension.newBuilder()
        DimensionDsl(dimensionBuilder).block()
        builder.setWidth(dimensionBuilder.build())
    }

    fun width(dimension: Dimension) {
        builder.setWidth(dimension)
    }

    fun height(block: DimensionDsl.() -> Unit) {
        val dimensionBuilder = Dimension.newBuilder()
        DimensionDsl(dimensionBuilder).block()
        builder.setHeight(dimensionBuilder.build())
    }

    fun height(dimension: Dimension) {
        builder.setHeight(dimension)
    }

    fun padding(block: PaddingDsl.() -> Unit) {
        val paddingBuilder = Padding.newBuilder()
        PaddingDsl(paddingBuilder).block()
        builder.setPadding(paddingBuilder.build())
    }

    fun padding(padding: Padding?) {
        padding?.let { builder.setPadding(it) }
    }

    fun cornerRadius(block: CornerRadiusDsl.() -> Unit) {
        val cornerRadiusBuilder = CornerRadius.newBuilder()
        CornerRadiusDsl(cornerRadiusBuilder).block()
        builder.setCornerRadius(cornerRadiusBuilder.build())
    }

    fun semantics(block: SemanticsDsl.() -> Unit) {
        val semanticsBuilder = com.example.dsl.proto.Semantics.newBuilder()
        SemanticsDsl(semanticsBuilder).block()
        builder.setSemantics(semanticsBuilder.build())
    }

    fun clickAction(block: ActionDsl.() -> Unit) {
        val actionBuilder = Action.newBuilder()
        ActionDsl(actionBuilder).block()
        builder.setClickAction(actionBuilder.build())
    }

    fun backgroundColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setBackgroundColor(colorProviderBuilder.build())
    }

    fun backgroundColor(colorProvider: ColorProvider?) {
        colorProvider?.let { builder.setBackgroundColor(it) }
    }
}

/**
 * ViewProperty DSL 빌더 함수
 */
fun viewProperty(block: ViewPropertyDsl.() -> Unit): ViewProperty {
    val builder = ViewProperty.newBuilder()
    val dsl = ViewPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Alignment DSL
 */
class AlignmentDsl(private val builder: Alignment.Builder) {
    var alignment: AlignmentType
        get() = builder.alignment
        set(value) {
            builder.setAlignment(value)
        }

    var horizontal: HorizontalAlignment
        get() = builder.horizontal
        set(value) {
            builder.setHorizontal(value)
        }

    var vertical: VerticalAlignment
        get() = builder.vertical
        set(value) {
            builder.setVertical(value)
        }
}

/**
 * Alignment DSL 빌더 함수
 */
fun alignment(block: AlignmentDsl.() -> Unit): Alignment {
    val builder = Alignment.newBuilder()
    val dsl = AlignmentDsl(builder)
    dsl.block()
    return builder.build()
}

