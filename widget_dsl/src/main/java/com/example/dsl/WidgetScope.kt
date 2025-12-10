package com.example.dsl

import com.example.dsl.proto.WidgetLayoutDocument
import com.example.dsl.proto.WidgetNode
import com.example.dsl.localprovider.WidgetLocal

/**
 * Widget DSL - Compose 스타일의 선언적 API
 *
 * 사용 예시:
 * ```
 * val layout = widgetLayout {
 *     column(horizontalAlignment = H_ALIGN_CENTER) {
 *         text("Hello", fontSize = 18f, fontWeight = FONT_WEIGHT_BOLD)
 *         text("World", fontSize = 14f)
 *     }
 * }
 * ```
 */

// ==================== 최상위 함수 ====================

/**
 * WidgetLayoutDocument를 생성하는 최상위 DSL 함수
 */
fun WidgetLayout(block: WidgetScope.() -> Unit): WidgetLayoutDocument {
    val scope = WidgetScope()
    scope.block()
    return WidgetLayoutDocument.newBuilder()
        .setRoot(scope.build())
        .build()
}

// ==================== Scope 클래스 ====================

/**
 * Widget DSL의 스코프
 * 자식 노드를 관리하고 최종 WidgetNode를 생성
 */
class WidgetScope {
    private var viewIdCounter = 0
    internal val children = mutableListOf<WidgetNode>()
    private val locals = mutableMapOf<WidgetLocal<out Any?>, Any?>()

    /**
     * 다음 viewId를 생성
     */
    fun nextViewId(): Int = viewIdCounter++

    /**
     * 자식 노드 추가
     */
    fun addChild(node: WidgetNode) {
        children.add(node)
    }

    /**
     * CompositionLocal 값을 설정.
     * */
    fun <T> setLocal(key: WidgetLocal<T>, value: T) {
        locals[key] = value
    }

    /**
     * CompositionLocal 값을 가져옴.
     * */
    fun <T> getLocal(key: WidgetLocal<T>): T? {
        return locals[key] as? T ?: key.getDefaultValue()
    }

    /**
     * 부모 스코프의 locals를 복사.
     * */
    internal fun copyLocalsFrom(parent: WidgetScope) {
        parent.locals.forEach { (key, value) ->
            locals[key as WidgetLocal<out Any?>] = value
        }
    }

    /**
     * 최종 WidgetNode 빌드 (단일 루트 노드인 경우)
     */
    fun build(): WidgetNode {
        require(children.size == 1) { "WidgetScope must have exactly one root node" }
        return children[0]
    }

    /**
     * 여러 자식 노드를 가진 컨테이너 빌드
     */
    fun buildContainer(
        setter: (WidgetNode.Builder, List<WidgetNode>) -> Unit
    ): WidgetNode {
        val builder = WidgetNode.newBuilder()
        setter(builder, children)
        return builder.build()
    }
}

