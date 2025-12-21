package com.example.dsl.widget

import com.example.dsl.widget.glance.render.Box
import com.example.dsl.widget.glance.render.Button
import com.example.dsl.widget.glance.render.CheckBox
import com.example.dsl.widget.glance.render.Column
import com.example.dsl.widget.glance.render.Image
import com.example.dsl.widget.glance.render.Progress
import com.example.dsl.widget.glance.render.Row
import com.example.dsl.widget.glance.render.Spacer
import com.example.dsl.widget.glance.render.Text

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
        NodeRendererRegistry.register("box", Box)
        NodeRendererRegistry.register("column", Column)
        NodeRendererRegistry.register("row", Row)

        // Component Renderers
        NodeRendererRegistry.register("text", Text)
        NodeRendererRegistry.register("image", Image)
        NodeRendererRegistry.register("button", Button)
        NodeRendererRegistry.register("progress", Progress)
        NodeRendererRegistry.register("spacer", Spacer)
        NodeRendererRegistry.register("checkbox", CheckBox)
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

