package com.example.widget.component

import com.example.dsl.WidgetScope
import com.example.dsl.proto.WidgetNode
import com.example.widget.SizeType
import com.example.widget.WidgetCategory

abstract class WidgetComponent {

    abstract fun getName(): String
    abstract fun getDescription(): String
    abstract fun getWidgetCategory(): WidgetCategory
    abstract fun getSizeType(): SizeType
    abstract fun getWidgetTag(): String
    abstract fun WidgetScope.Content()

    fun provideContent(): WidgetNode {
        val scope = WidgetScope()
        scope.Content()
        return scope.build()
    }
}