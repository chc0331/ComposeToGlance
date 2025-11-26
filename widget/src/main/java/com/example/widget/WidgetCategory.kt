package com.example.widget

import android.graphics.drawable.Icon
import androidx.compose.runtime.mutableStateListOf

enum class WidgetCategory(
    val description: String,
    val icon: Icon? = null
) {
    BASIC("Basic"), CLOCK("Clock"), DEVICE_INFO("DeviceInfo")
}