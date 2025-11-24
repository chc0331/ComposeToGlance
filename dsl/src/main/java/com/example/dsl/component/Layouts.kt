package com.example.dsl.component

import com.example.dsl.WidgetScope
import com.example.dsl.builder.boxLayoutProperty
import com.example.dsl.builder.columnLayoutProperty
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.builder.rowLayoutProperty
import com.example.dsl.builder.viewProperty
import com.example.dsl.builder.wrapContentDimension
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_START
import com.example.dsl.proto.Padding
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_TOP
import com.example.dsl.proto.WidgetNode

// ==================== 레이아웃 DSL ====================

/**
 * Column 레이아웃
 */
fun WidgetScope.Column(
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    horizontalAlignment: HorizontalAlignment = H_ALIGN_START,
    verticalAlignment: VerticalAlignment = V_ALIGN_TOP,
    backgroundColor: ColorProvider? = null,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.block()

    val columnNode = WidgetNode.newBuilder()
        .setColumn(
            columnLayoutProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    backgroundColor = backgroundColor
                ),
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment
            )
        )
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(columnNode)
}

/**
 * Row 레이아웃
 */
fun WidgetScope.Row(
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    horizontalAlignment: HorizontalAlignment = H_ALIGN_START,
    verticalAlignment: VerticalAlignment = V_ALIGN_TOP,
    backgroundColor: ColorProvider? = null,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.block()

    val rowNode = WidgetNode.newBuilder()
        .setRow(
            rowLayoutProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    backgroundColor = backgroundColor
                ),
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment
            )
        )
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(rowNode)
}

/**
 * Box 레이아웃
 */
fun WidgetScope.Box(
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_TOP_START,
    backgroundColor: ColorProvider? = null,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.block()
    val boxNode = WidgetNode.newBuilder()
        .setBox(
            boxLayoutProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    backgroundColor = backgroundColor
                ),
                alignment = alignment
            )
        )
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(boxNode)
}
