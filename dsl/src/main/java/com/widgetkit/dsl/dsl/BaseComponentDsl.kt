package com.widgetkit.dsl.dsl

import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.dsl.modifier.WidgetModifier
import com.widgetkit.dsl.dsl.modifier.ModifierBuilder
import com.widgetkit.dsl.proto.ViewProperty

/**
 * 컴포넌트 DSL의 공통 베이스 클래스
 * 
 * ViewProperty 처리 로직을 공통화하여 각 컴포넌트 DSL의 보일러플레이트 코드를 줄입니다.
 */
abstract class BaseComponentDsl(
    protected val scope: WidgetScope
) {
    private var modifier: WidgetModifier = WidgetModifier
    private var viewPropertyBlock: (ViewPropertyDsl.() -> Unit)? = null
    private var viewPropertySet = false

    /**
     * Modifier 설정
     */
    fun modifier(value: WidgetModifier) {
        modifier = value
    }

    /**
     * ViewProperty 설정 블록 (하위 호환성을 위해 유지)
     */
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        viewPropertySet = true
        viewPropertyBlock = block
    }

    /**
     * ViewProperty를 빌드
     * 
     * Modifier와 ViewProperty 블록을 모두 고려하여 ViewProperty를 생성합니다.
     * ViewProperty 블록이 있으면 우선 적용하고, Modifier는 추가로 적용됩니다.
     */
    protected fun buildViewProperty(): ViewProperty {
        val builder = ModifierBuilder.buildViewProperty(modifier, scope)
        
        // ViewProperty 블록이 있으면 적용 (Modifier보다 우선)
        if (viewPropertySet && viewPropertyBlock != null) {
            val viewPropertyDsl = ViewPropertyDsl(builder)
            viewPropertyBlock!!(viewPropertyDsl)
        }
        
        return builder.build()
    }

    /**
     * ViewProperty가 설정되었는지 확인
     */
    protected fun hasViewProperty(): Boolean {
        return viewPropertySet || modifier !== WidgetModifier
    }
}

