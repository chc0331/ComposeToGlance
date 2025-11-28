package com.example.widget.component

import com.example.dsl.WidgetScope
import com.example.dsl.proto.WidgetNode
import com.example.dsl.provider.DslLocalProvider
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.localprovider.DslLocalSizeType

abstract class WidgetComponent {

    abstract fun getName(): String
    abstract fun getDescription(): String
    abstract fun getWidgetCategory(): WidgetCategory
    abstract fun getSizeType(): SizeType
    abstract fun getWidgetTag(): String
    abstract fun WidgetScope.Content()

    /**
     * Content를 주어진 scope에서 실행합니다.
     * 이 메서드는 DslLocalProvider 내에서 호출되어야 하며,
     * 생성된 children은 DslLocalProvider가 자동으로 수집합니다.
     */
    fun renderContent(scope: WidgetScope) {
        scope.Content()
    }

    private fun WidgetScope.WidgetContent() {
        DslLocalProvider(DslLocalSizeType provides getSizeType()) {
            Content()
        }
    }

    /**
     * Content를 주어진 scope에서 실행하여 WidgetNode를 생성합니다.
     * 이 메서드는 DslLocalProvider 내에서 호출되어야 합니다.
     */
    fun provideContent(scope: WidgetScope): WidgetNode {
        scope.Content()
        return scope.build()
    }

    /**
     * 새로운 scope를 생성하여 Content를 실행합니다.
     * 주의: 이 메서드는 DslLocalProvider의 locals에 접근할 수 없습니다.
     */
    fun provideContent(): WidgetNode {
        val scope = WidgetScope()
        scope.Content()
        return scope.build()
    }
}