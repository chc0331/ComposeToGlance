package com.widgetworld.core.proto.property

import com.widgetworld.core.proto.ViewProperty

class ViewPropertyDsl(private val builder: ViewProperty.Builder) {
    var viewId: Int
        get() = builder.viewId
        set(value) {
            builder.setViewId(value)
        }
}