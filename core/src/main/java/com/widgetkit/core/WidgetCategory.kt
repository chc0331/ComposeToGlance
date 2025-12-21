package com.widgetkit.core

import android.graphics.drawable.Icon

enum class WidgetCategory(
    val description: String,
    val icon: Icon? = null
) {
    BASIC("Basic"), CLOCK("Clock"), DEVICE_INFO("DeviceInfo");

    fun toProto(): com.widgetkit.core.proto.WidgetCategory {
        return when (this) {
            BASIC -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_BASIC
            CLOCK -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_CLOCK
            DEVICE_INFO -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_DEVICE_INFO
            else -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_UNSPECIFIED
        }
    }
}
