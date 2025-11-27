package com.example.dsl.component

import com.example.dsl.WidgetScope
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.WidgetNode

// ==================== 레이아웃 DSL ====================

/**
 * Column 레이아웃 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Column({
 *     viewProperty {
 *         viewId = 1
 *         Width { dp { value = 100f } }
 *         Height { matchParent = true }
 *         Padding {
 *             start = 16f
 *             top = 16f
 *         }
 *     }
 *     horizontalAlignment = H_ALIGN_CENTER
 *     verticalAlignment = V_ALIGN_CENTER
 * }) {
 *     Text("Hello")
 * }
 * ```
 */
fun WidgetScope.Column(
    block: ColumnLayoutDsl.() -> Unit,
    content: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = ColumnLayoutDsl(this)
    dsl.block()

    val columnNode = WidgetNode.newBuilder()
        .setColumn(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(columnNode)
}

/**
 * Row 레이아웃 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Row({
 *     viewProperty {
 *         viewId = 1
 *         Width { dp { value = 100f } }
 *         Height { matchParent = true }
 *         Padding {
 *             start = 16f
 *             top = 16f
 *         }
 *     }
 *     horizontalAlignment = H_ALIGN_CENTER
 *     verticalAlignment = V_ALIGN_CENTER
 * }) {
 *     Text("Hello")
 * }
 * ```
 */
fun WidgetScope.Row(
    block: RowLayoutDsl.() -> Unit,
    content: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = RowLayoutDsl(this)
    dsl.block()

    val rowNode = WidgetNode.newBuilder()
        .setRow(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(rowNode)
}

/**
 * Box 레이아웃 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Box({
 *     viewProperty {
 *         viewId = 1
 *         Width { dp { value = 100f } }
 *         Height { matchParent = true }
 *         Padding {
 *             start = 16f
 *             top = 16f
 *         }
 *     }
 *     contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
 * }) {
 *     Text("Hello")
 * }
 * ```
 */
fun WidgetScope.Box(
    block: BoxLayoutDsl.() -> Unit,
    content: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.content()

    val dsl = BoxLayoutDsl(this)
    dsl.block()

    val boxNode = WidgetNode.newBuilder()
        .setBox(dsl.build())
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(boxNode)
}
