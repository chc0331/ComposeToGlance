package com.example.widget

import android.graphics.drawable.Icon
import androidx.compose.runtime.mutableStateListOf

data class WidgetCategory(
    val id: String,
    val name: String,
    val icon: Icon? = null
)

// 카테고리 목록
val categories: List<WidgetCategory> = listOf(
    WidgetCategory("basic", "기본"),
    WidgetCategory("clock", "시계"),
    WidgetCategory("device_info", "디바이스 정보")
)

// 위젯 목록
val widgets = mutableStateListOf(
    // 기본 카테고리
    Widget("텍스트", "텍스트 위젯 컴포넌트", SizeType.TINY, WidgetCategory("basic", "basic"), "text"),
    Widget("이미지", "이미지 위젯 컴포넌트", SizeType.TINY, WidgetCategory("basic", "basic"), "image"),
    Widget("버튼", "버튼 위젯 컴포넌트", SizeType.SMALL, WidgetCategory("basic", "basic"), "button"),

    // 시계 카테고리
    Widget(
        "아날로그 시계",
        "아날로그 시계 컴포넌트",
        SizeType.MEDIUM,
        WidgetCategory("clock", "clock"),
        "analog_clock"
    ),
    Widget(
        "디지털 시계",
        "디지털 시계 컴포넌트",
        SizeType.SMALL,
        WidgetCategory("clock", "clock"),
        "digital_clock"
    ),

    // 디바이스 정보 카테고리
    Widget(
        "배터리",
        "배터리 정보 컴포넌트",
        SizeType.MEDIUM,
        WidgetCategory("device_info", "device_info"),
        "battery"
    ),
    Widget(
        "스토리지",
        "스토리지 정보 컴포넌트",
        SizeType.SMALL,
        WidgetCategory("device_info", "device_info"),
        "storage"
    )
)