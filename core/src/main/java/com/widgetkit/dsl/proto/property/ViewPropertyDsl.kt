package com.widgetkit.dsl.proto.property

import com.widgetkit.dsl.proto.ViewProperty

class ViewPropertyDsl(private val builder: ViewProperty.Builder) {
    var viewId: Int
        get() = builder.viewId
        set(value) {
            builder.setViewId(value)
        }
}