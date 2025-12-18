package com.example.dsl.widget.renderer

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
        NodeRendererRegistry.register("box", BoxRenderer)
        NodeRendererRegistry.register("column", ColumnRenderer)
        NodeRendererRegistry.register("row", RowRenderer)

        // Component Renderers
        NodeRendererRegistry.register("text", TextRenderer)
        NodeRendererRegistry.register("image", ImageRenderer)
        NodeRendererRegistry.register("button", ButtonRenderer)
        NodeRendererRegistry.register("progress", ProgressRenderer)
        NodeRendererRegistry.register("spacer", SpacerRenderer)
        NodeRendererRegistry.register("checkbox", CheckboxRenderer)
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

