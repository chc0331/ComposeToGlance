package com.widgetkit.dsl.widget

import com.widgetkit.dsl.widget.render.component.ButtonNode
import com.widgetkit.dsl.widget.render.component.CheckBoxNode
import com.widgetkit.dsl.widget.render.component.ImageNode
import com.widgetkit.dsl.widget.render.component.ProgressNode
import com.widgetkit.dsl.widget.render.component.SpacerNode
import com.widgetkit.dsl.widget.render.component.TextNode
import com.widgetkit.dsl.widget.render.layout.BoxNode
import com.widgetkit.dsl.widget.render.layout.ColumnNode
import com.widgetkit.dsl.widget.render.layout.RowNode

/**
 * 기본 Renderer들을 NodeRendererRegistry에 등록하는 초기화 클래스
 *
 * 앱 시작 시 이 클래스의 initialize() 메서드를 호출하여
 * 모든 기본 Renderer를 등록합니다.
 *
 * 새로운 기본 Renderer를 추가할 때는 이 파일에 등록 로직을 추가하면 됩니다.
 */
object RendererInitializer {
    /**
     * 기본 Renderer들을 모두 등록합니다.
     *
     * 이 메서드는 앱 초기화 시 한 번 호출되어야 합니다.
     * 예: Application.onCreate() 또는 초기화 시점
     */
    fun initialize() {
        // Layout Renderers
        NodeRendererRegistry.register("box", BoxNode())
        NodeRendererRegistry.register("column", ColumnNode())
        NodeRendererRegistry.register("row", RowNode())

        // Component Renderers
        NodeRendererRegistry.register("text", TextNode())
        NodeRendererRegistry.register("image", ImageNode())
        NodeRendererRegistry.register("button", ButtonNode())
        NodeRendererRegistry.register("progress", ProgressNode())
        NodeRendererRegistry.register("spacer", SpacerNode())
        NodeRendererRegistry.register("checkbox", CheckBoxNode())
    }

    /**
     * 등록된 Renderer가 올바르게 초기화되었는지 확인합니다.
     *
     * @return 모든 기본 Renderer가 등록되었으면 true
     */
    fun isInitialized(): Boolean {
        val requiredTypes = setOf(
            "box", "column", "row",
            "text", "image", "button", "progress", "spacer", "checkbox"
        )
        val registeredTypes = NodeRendererRegistry.getRegisteredTypes()
        return registeredTypes.containsAll(requiredTypes)
    }
}

