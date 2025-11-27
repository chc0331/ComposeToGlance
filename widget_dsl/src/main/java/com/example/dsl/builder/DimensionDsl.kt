package com.example.dsl.builder

import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Dp
import com.example.dsl.proto.Padding

/**
 * Dp DSL
 */
class DpDsl(private val builder: Dp.Builder) {
    var value: Float
        get() = builder.value
        set(value) {
            builder.setValue(value)
        }
}

/**
 * Dp DSL 빌더 함수
 */
fun Dp(block: DpDsl.() -> Unit): Dp {
    val builder = Dp.newBuilder()
    val dsl = DpDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * CornerRadius DSL
 */
class CornerRadiusDsl(private val builder: CornerRadius.Builder) {
    var radius: Float
        get() = builder.radius
        set(value) {
            builder.setRadius(value)
        }
}

/**
 * CornerRadius DSL 빌더 함수
 */
fun CornerRadius(block: CornerRadiusDsl.() -> Unit): CornerRadius {
    val builder = CornerRadius.newBuilder()
    val dsl = CornerRadiusDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Padding DSL
 */
class PaddingDsl(private val builder: Padding.Builder) {
    fun start(block: DpDsl.() -> Unit) {
        val dpBuilder = Dp.newBuilder()
        DpDsl(dpBuilder).block()
        builder.setStart(dpBuilder.build())
    }

    fun top(block: DpDsl.() -> Unit) {
        val dpBuilder = Dp.newBuilder()
        DpDsl(dpBuilder).block()
        builder.setTop(dpBuilder.build())
    }

    fun end(block: DpDsl.() -> Unit) {
        val dpBuilder = Dp.newBuilder()
        DpDsl(dpBuilder).block()
        builder.setEnd(dpBuilder.build())
    }

    fun bottom(block: DpDsl.() -> Unit) {
        val dpBuilder = Dp.newBuilder()
        DpDsl(dpBuilder).block()
        builder.setBottom(dpBuilder.build())
    }

    // 편의를 위한 Float 직접 설정
    var start: Float
        get() = if (builder.hasStart()) builder.start.value else 0f
        set(value) {
            builder.setStart(Dp { this.value = value })
        }

    var top: Float
        get() = if (builder.hasTop()) builder.top.value else 0f
        set(value) {
            builder.setTop(Dp { this.value = value })
        }

    var end: Float
        get() = if (builder.hasEnd()) builder.end.value else 0f
        set(value) {
            builder.setEnd(Dp { this.value = value })
        }

    var bottom: Float
        get() = if (builder.hasBottom()) builder.bottom.value else 0f
        set(value) {
            builder.setBottom(Dp { this.value = value })
        }
}

/**
 * Padding DSL 빌더 함수
 */
fun Padding(block: PaddingDsl.() -> Unit): Padding {
    val builder = Padding.newBuilder()
    val dsl = PaddingDsl(builder)
    dsl.block()
    return builder.build()
}

/**
 * Dimension DSL
 */
class DimensionDsl(private val builder: Dimension.Builder) {
    fun dp(block: DpDsl.() -> Unit) {
        val dpBuilder = Dp.newBuilder()
        DpDsl(dpBuilder).block()
        builder.setDp(dpBuilder.build())
    }

    var wrapContent: Boolean
        get() = builder.wrapContent
        set(value) {
            if (value) {
                builder.setWrapContent(true)
            }
        }

    var matchParent: Boolean
        get() = builder.matchParent
        set(value) {
            if (value) {
                builder.setMatchParent(true)
            }
        }

    var weight: Float
        get() = if (builder.hasWeight()) builder.weight else 0f
        set(value) {
            if (value != 0f) {
                builder.setWeight(value)
            }
        }
}

/**
 * Dimension DSL 빌더 함수
 */
fun Dimension(block: DimensionDsl.() -> Unit): Dimension {
    val builder = Dimension.newBuilder()
    val dsl = DimensionDsl(builder)
    dsl.block()
    return builder.build()
}

