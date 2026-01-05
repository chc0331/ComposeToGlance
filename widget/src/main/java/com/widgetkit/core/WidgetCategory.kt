package com.widgetkit.core

import android.graphics.drawable.Icon

enum class WidgetCategory(
    val description: String,
    val icon: Icon? = null
) {
    DAILY_SCHEDULE("Reminder & Calendar"), CLOCK("Clock"), DEVICE_INFO("DeviceInfo");

    fun toProto(): com.widgetkit.core.proto.WidgetCategory {
        return when (this) {
            DAILY_SCHEDULE -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_DAILY_SCHEDULE
            CLOCK -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_CLOCK
            DEVICE_INFO -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_DEVICE_INFO
            else -> com.widgetkit.core.proto.WidgetCategory.WIDGET_CATEGORY_UNSPECIFIED
        }
    }
}
