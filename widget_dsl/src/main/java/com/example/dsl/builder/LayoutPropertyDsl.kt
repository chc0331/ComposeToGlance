package com.example.dsl.builder

import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.BoxLayoutProperty
import com.example.dsl.proto.ColumnLayoutProperty
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.RowLayoutProperty
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.ViewProperty

/**
 * BoxLayoutProperty DSL
 */
class BoxLayoutPropertyDsl(private val builder: BoxLayoutProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    var contentAlignment: AlignmentType
        get() = builder.contentAlignment
        set(value) {
            builder.setContentAlignment(value)
        }
}

/**
 * BoxLayoutProperty DSL 빌더 함수 (대문자로 시작)
 */
fun BoxLayoutProperty(block: BoxLayoutPropertyDsl.() -> Unit): BoxLayoutProperty {
    val builder = BoxLayoutProperty.newBuilder()
    val dsl = BoxLayoutPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * RowLayoutProperty DSL
 */
class RowLayoutPropertyDsl(private val builder: RowLayoutProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    var horizontalAlignment: HorizontalAlignment
        get() = builder.horizontalAlignment
        set(value) {
            builder.setHorizontalAlignment(value)
        }

    var verticalAlignment: VerticalAlignment
        get() = builder.verticalAlignment
        set(value) {
            builder.setVerticalAlignment(value)
        }
}

/**
 * RowLayoutProperty DSL 빌더 함수 (대문자로 시작)
 */
fun RowLayoutProperty(block: RowLayoutPropertyDsl.() -> Unit): RowLayoutProperty {
    val builder = RowLayoutProperty.newBuilder()
    val dsl = RowLayoutPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * ColumnLayoutProperty DSL
 */
class ColumnLayoutPropertyDsl(private val builder: ColumnLayoutProperty.Builder) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    var horizontalAlignment: HorizontalAlignment
        get() = builder.horizontalAlignment
        set(value) {
            builder.setHorizontalAlignment(value)
        }

    var verticalAlignment: VerticalAlignment
        get() = builder.verticalAlignment
        set(value) {
            builder.setVerticalAlignment(value)
        }
}

/**
 * ColumnLayoutProperty DSL 빌더 함수 (대문자로 시작)
 */
fun ColumnLayoutProperty(block: ColumnLayoutPropertyDsl.() -> Unit): ColumnLayoutProperty {
    val builder = ColumnLayoutProperty.newBuilder()
    val dsl = ColumnLayoutPropertyDsl(builder)
    dsl.block()
    return builder.build()
}

