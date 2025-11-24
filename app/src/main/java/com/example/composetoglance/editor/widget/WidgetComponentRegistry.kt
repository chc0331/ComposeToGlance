package com.example.composetoglance.editor.widget

import com.example.dsl.WidgetScope

/**
 * 위젯 컴포넌트를 등록하고 조회하는 레지스트리
 * componentId를 키로 WidgetScope extension function을 저장합니다.
 */
object WidgetComponentRegistry {
    private val components = mutableMapOf<String, WidgetScope.() -> Unit>()

    /**
     * 위젯 컴포넌트를 등록합니다.
     * @param componentId 컴포넌트 고유 ID
     * @param component DSL 컴포넌트 함수
     */
    fun registerComponent(componentId: String, component: WidgetScope.() -> Unit) {
        components[componentId] = component
    }

    /**
     * 등록된 컴포넌트를 조회합니다.
     * @param componentId 컴포넌트 고유 ID
     * @return DSL 컴포넌트 함수, 없으면 null
     */
    fun getComponent(componentId: String): (WidgetScope.() -> Unit)? {
        return components[componentId]
    }

    /**
     * 컴포넌트가 등록되어 있는지 확인합니다.
     * @param componentId 컴포넌트 고유 ID
     * @return 등록되어 있으면 true
     */
    fun hasComponent(componentId: String): Boolean {
        return components.containsKey(componentId)
    }

    /**
     * 모든 등록된 컴포넌트 ID 목록을 반환합니다.
     */
    fun getAllComponentIds(): Set<String> {
        return components.keys.toSet()
    }

    /**
     * 모든 컴포넌트를 제거합니다. (테스트용)
     */
    fun clear() {
        components.clear()
    }
}

