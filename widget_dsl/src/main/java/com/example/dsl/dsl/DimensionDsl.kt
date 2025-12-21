package com.example.dsl.dsl

import com.example.dsl.proto.CornerRadius
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.Dp
import com.example.dsl.proto.Padding

/**
 * Dimension 관련 DSL 클래스 및 DSL 빌더 함수
 * 
 * 이 파일은 block을 받는 DSL 빌더 함수와 DSL 클래스를 포함합니다.
 * - DSL 클래스: DpDsl, CornerRadiusDsl, PaddingDsl, DimensionDsl
 * - DSL 빌더 함수: Dp(block), CornerRadius(block), Padding(block), Dimension(block)
 * 
 * 간단한 빌더 함수(파라미터를 직접 받는)는 DimensionBuilders.kt를 참조하세요.
 */

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
            builder.setStart(com.example.dsl.dsl.builder.Dp(value))
        }

    var top: Float
        get() = if (builder.hasTop()) builder.top.value else 0f
        set(value) {
            builder.setTop(com.example.dsl.dsl.builder.Dp(value))
        }

    var end: Float
        get() = if (builder.hasEnd()) builder.end.value else 0f
        set(value) {
            builder.setEnd(com.example.dsl.dsl.builder.Dp(value))
        }

    var bottom: Float
        get() = if (builder.hasBottom()) builder.bottom.value else 0f
        set(value) {
            builder.setBottom(com.example.dsl.dsl.builder.Dp(value))
        }
}

/**
 * Dimension DSL
 */
class DimensionDsl(private val builder: Dimension.Builder) {
    fun Dp(block: DpDsl.() -> Unit) {
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