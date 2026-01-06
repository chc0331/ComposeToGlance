package com.widgetkit.widgetcomponent

import android.graphics.drawable.Icon

enum class WidgetCategory(
    val description: String,
    val icon: Icon? = null
) {
    DAILY_SCHEDULE("Reminder & Calendar"), CLOCK("Clock"), DEVICE_STATUS("Device Status");

    fun toProto(): com.widgetkit.widgetcomponent.proto.WidgetCategory {
        return when (this) {
            DAILY_SCHEDULE -> com.widgetkit.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_DAILY_SCHEDULE
            CLOCK -> com.widgetkit.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_CLOCK
            DEVICE_STATUS -> com.widgetkit.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_DEVICE_STATUS
            else -> com.widgetkit.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_UNSPECIFIED
        }
    }
}
