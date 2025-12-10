package com.example.dsl.localprovider

import com.example.dsl.WidgetScope


/**
 * 데이터를 위젯 트리 내에서 하위 컴포넌트로 전달하는 데 사용. (CompositionLocal API와 유사)
 * */
class WidgetLocal<T>(private val defaultValue: T? = null) {
    companion object {
        fun <T> of(defaultValue: T? = null): WidgetLocal<T> = WidgetLocal(defaultValue)
    }

    infix fun provides(value: T): WidgetLocalProvider<T> = WidgetLocalProvider(this, value)
    
    fun getDefaultValue(): T? = defaultValue
}

class WidgetLocalProvider<T>(
    val key: WidgetLocal<T>,
    val value: T
)

fun WidgetScope.WidgetLocalProvider(
    vararg providers: WidgetLocalProvider<*>,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    // 부모 스코프의 locals를 복사
    childScope.copyLocalsFrom(this)
    // 새로운 providers를 locals에 추가
    providers.forEach { provider ->
        childScope.setLocal(provider.key as WidgetLocal<Any?>, provider.value)
    }
    childScope.block()
    // 생성된 노드들을 현재 스코프에 추가.
    childScope.children.forEach { addChild(it) }
}
