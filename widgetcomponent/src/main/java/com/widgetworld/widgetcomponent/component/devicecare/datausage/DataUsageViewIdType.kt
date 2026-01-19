package com.widgetworld.widgetcomponent.component.devicecare.datausage

import com.widgetworld.widgetcomponent.component.viewid.ViewIdType

sealed class DataUsageViewIdType(override val typeName: String) : ViewIdType() {
    object WifiProgress : DataUsageViewIdType("wifi_progress")
    object WifiPercent : DataUsageViewIdType("wifi_percent")
    object WifiUsageText : DataUsageViewIdType("wifi_usage_text")

    object MobileProgress : DataUsageViewIdType("mobile_progress")
    object MobilePercent : DataUsageViewIdType("mobile_percent")
    object MobileUsageText : DataUsageViewIdType("mobile_usage_text")

    // Date range text
    object DateRangeText : DataUsageViewIdType("date_range_text")

    companion object {
        fun all(): List<DataUsageViewIdType> = listOf(
            WifiProgress, WifiPercent, WifiUsageText,
            MobileProgress, MobilePercent, MobileUsageText, DateRangeText
        )
    }
}

