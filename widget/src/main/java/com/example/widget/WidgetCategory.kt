package com.example.widget

import android.graphics.drawable.Icon

data class WidgetCategory(
    val id: String,
    val name: String,
    val icon: Icon? = null
)