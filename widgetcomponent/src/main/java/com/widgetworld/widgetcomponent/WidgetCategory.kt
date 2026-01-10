package com.widgetworld.widgetcomponent

import android.graphics.drawable.Icon

enum class WidgetCategory(
    val description: String,
    val icon: Icon? = null
) {
    CLOCK("Clock"),
    DAILY_SCHEDULE("Reminder & Calendar"),
    DEVICE_STATUS("Device Status");

    fun toProto(): com.widgetworld.widgetcomponent.proto.WidgetCategory {
        return when (this) {
            DAILY_SCHEDULE -> com.widgetworld.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_DAILY_SCHEDULE
            CLOCK -> com.widgetworld.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_CLOCK
            DEVICE_STATUS -> com.widgetworld.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_DEVICE_STATUS
            else -> com.widgetworld.widgetcomponent.proto.WidgetCategory.WIDGET_CATEGORY_UNSPECIFIED
        }
    }
}
