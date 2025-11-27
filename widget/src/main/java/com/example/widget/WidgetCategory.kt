package com.example.widget

import android.graphics.drawable.Icon
import androidx.compose.runtime.mutableStateListOf

enum class WidgetCategory(
    val description: String,
    val icon: Icon? = null
) {
    BASIC("Basic"), CLOCK("Clock"), DEVICE_INFO("DeviceInfo");

    fun toProto(): com.example.widget.proto.WidgetCategory{
        return when(this){
            BASIC -> com.example.widget.proto.WidgetCategory.WIDGET_CATEGORY_BASIC
            CLOCK -> com.example.widget.proto.WidgetCategory.WIDGET_CATEGORY_CLOCK
            DEVICE_INFO -> com.example.widget.proto.WidgetCategory.WIDGET_CATEGORY_DEVICE_INFO
            else -> com.example.widget.proto.WidgetCategory.WIDGET_CATEGORY_UNSPECIFIED
        }
    }
}