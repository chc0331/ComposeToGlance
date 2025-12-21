package com.widgetkit.dsl.widget

import com.widgetkit.dsl.proto.WidgetNode

/**
 * NodeRenderer를 등록하고 조회하는 레지스트리
 * 
 * 새로운 컴포넌트를 추가할 때, 해당 Renderer를 등록하면
 * GlanceRenderer에서 자동으로 사용할 수 있습니다.
 * 
 * 사용 예시:
 * ```
 * NodeRendererRegistry.register("box", BoxRenderer)
 * NodeRendererRegistry.register("text", TextRenderer)
 * ```
 */
internal object NodeRendererRegistry {
    /**
     * 노드 타입별 Renderer 매핑
     * 키: 노드 타입 문자열 (예: "box", "text", "image")
     * 값: 해당 타입의 NodeRenderer 구현체
     */
    private val renderers = mutableMapOf<String, NodeRenderer>()

    /**
     * Renderer를 등록합니다.
     * 
     * @param type 노드 타입 식별자 (예: "box", "text", "image")
     * @param renderer 해당 타입의 NodeRenderer 구현체
     */
    fun register(type: String, renderer: NodeRenderer) {
        renderers[type] = renderer
    }

    /**
     * 등록된 Renderer를 조회합니다.
     * 
     * @param type 노드 타입 식별자
     * @return 등록된 NodeRenderer 또는 null
     */
    fun getRenderer(type: String): NodeRenderer? {
        return renderers[type]
    }

    /**
     * WidgetNode에서 적절한 Renderer를 찾아 반환합니다.
     * 
     * @param node WidgetNode
     * @return 해당 노드 타입의 NodeRenderer 또는 null
     */
    fun getRendererForNode(node: WidgetNode): NodeRenderer? {
        val type = getNodeType(node)
        return type?.let { getRenderer(it) }
    }

    /**
     * WidgetNode의 타입을 문자열로 반환합니다.
     * 
     * @param node WidgetNode
     * @return 노드 타입 문자열 또는 null
     */
    private fun getNodeType(node: WidgetNode): String? {
        return when {
            node.hasBox() -> "box"
            node.hasColumn() -> "column"
            node.hasRow() -> "row"
            node.hasText() -> "text"
            node.hasImage() -> "image"
            node.hasButton() -> "button"
            node.hasProgress() -> "progress"
            node.hasSpacer() -> "spacer"
            else -> null
        }
    }

    /**
     * 모든 등록된 Renderer 타입 목록을 반환합니다.
     */
    fun getRegisteredTypes(): Set<String> {
        return renderers.keys.toSet()
    }

    /**
     * 모든 등록된 Renderer를 제거합니다.
     */
    fun clear() {
        renderers.clear()
    }
}

