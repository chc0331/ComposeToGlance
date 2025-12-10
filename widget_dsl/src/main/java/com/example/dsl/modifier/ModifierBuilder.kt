package com.example.dsl.modifier

import com.example.dsl.WidgetScope
import com.example.dsl.proto.ViewProperty

/**
 * Modifier를 ViewProperty로 변환하는 빌더
 * 
 * Modifier 체인을 순회하며 ViewProperty.Builder에 설정을 적용합니다.
 */
object ModifierBuilder {


    /**
     * Modifier를 ViewProperty로 변환
     * 
     * @param modifier 변환할 Modifier
     * @param scope WidgetScope (viewId 생성용)
     * @return ViewProperty.Builder
     */
    fun buildViewProperty(
        modifier: Modifier,
        scope: WidgetScope
    ): ViewProperty.Builder {
        val builder = ViewProperty.newBuilder()

        // viewId는 기본적으로 scope에서 생성하되, modifier에 명시된 경우 사용
        var viewIdSet = false
        
        // Modifier 체인을 순회하며 설정 적용
        modifier.forEach { element ->
            when (element) {
                is Modifier.WidthModifier -> {
                    builder.width = element.dimension
                }
                is Modifier.HeightModifier -> {
                    builder.height = element.dimension
                }
                is Modifier.PaddingModifier -> {
                    builder.padding = element.padding
                }
                is Modifier.CornerRadiusModifier -> {
                    builder.cornerRadius = element.cornerRadius
                }
                is Modifier.SemanticsModifier -> {
                    builder.semantics = element.semantics
                }
                is Modifier.ClickActionModifier -> {
                    builder.clickAction = element.action
                }
                is Modifier.BackgroundColorModifier -> {
                    builder.backgroundColor = element.colorProvider
                }
                is Modifier.ViewIdModifier -> {
                    builder.viewId = element.viewId
                    viewIdSet = true
                }
                is Modifier.PartiallyUpdateModifier -> {
                    builder.partiallyUpdate = element.partiallyUpdate
                }
                is Modifier.HideModifier -> {
                    builder.hide = element.hide
                }
                Modifier -> {
                    // 빈 Modifier는 무시
                }
                is Modifier.Combined -> {
                    // Combined는 forEach에서 처리됨
                }
            }
        }
        
        // viewId가 설정되지 않았으면 scope에서 생성
        if (!viewIdSet) {
            builder.viewId = scope.nextViewId()
        }
        
        return builder
    }
    
    /**
     * Modifier 체인을 순회하는 헬퍼 함수
     */
    private fun Modifier.forEach(action: (Modifier) -> Unit) {
        when (this) {
            Modifier -> {
                // 빈 Modifier는 처리하지 않음
            }
            is Modifier.Combined -> {
                // 외부부터 내부 순서로 처리 (체이닝 순서 유지)
                outer.forEach(action)
                inner.forEach(action)
            }
            else -> {
                action(this)
            }
        }
    }
}

