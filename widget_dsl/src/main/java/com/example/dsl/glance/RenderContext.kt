package com.example.dsl.glance

import android.content.Context
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment

/**
 * 렌더링 컨텍스트
 * 노드 렌더링 시 필요한 정보를 전달
 */
data class RenderContext(
    val context: Context,
    val modifier: GlanceModifier = GlanceModifier,
    val parentAlignment: Alignment? = null
) {
    /**
     * Modifier를 추가하여 새로운 컨텍스트 생성
     */
    fun withModifier(newModifier: GlanceModifier): RenderContext {
        return copy(modifier = this.modifier.then(newModifier))
    }

    /**
     * Alignment를 설정하여 새로운 컨텍스트 생성
     */
    fun withAlignment(alignment: Alignment): RenderContext {
        return copy(parentAlignment = alignment)
    }
}

