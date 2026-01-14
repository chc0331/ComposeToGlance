package com.widgetworld.widgetcomponent.component.devicecare.datausage

import com.widgetworld.widgetcomponent.component.viewid.ViewIdType

sealed class DataUsageViewIdType(override val typeName: String) : ViewIdType() {
    // Legacy types (for backward compatibility)
    object Text : DataUsageViewIdType("data_usage_text")
    object Progress : DataUsageViewIdType("data_usage_progress")
    
    // Wi-Fi section types
    object WifiIcon : DataUsageViewIdType("wifi_icon")
    object WifiTitle : DataUsageViewIdType("wifi_title")
    object WifiProgress : DataUsageViewIdType("wifi_progress")
    object WifiPercent : DataUsageViewIdType("wifi_percent")
    object WifiUsageText : DataUsageViewIdType("wifi_usage_text")
    
    // Mobile Data section types
    object MobileIcon : DataUsageViewIdType("mobile_icon")
    object MobileTitle : DataUsageViewIdType("mobile_title")
    object MobileProgress : DataUsageViewIdType("mobile_progress")
    object MobilePercent : DataUsageViewIdType("mobile_percent")
    object MobileUsageText : DataUsageViewIdType("mobile_usage_text")
    
    // Date range text
    object DateRangeText : DataUsageViewIdType("date_range_text")
    
    // Limit change buttons
    object WifiLimitButton : DataUsageViewIdType("wifi_limit_button")
    object MobileLimitButton : DataUsageViewIdType("mobile_limit_button")

    companion object {
        fun all(): List<DataUsageViewIdType> = listOf(
            Text, Progress,
            WifiIcon, WifiTitle, WifiProgress, WifiPercent, WifiUsageText,
            MobileIcon, MobileTitle, MobileProgress, MobilePercent, MobileUsageText,
            DateRangeText,
            WifiLimitButton, MobileLimitButton
        )
    }
}

