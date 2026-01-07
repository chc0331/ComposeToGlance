package com.widgetworld.widgetcomponent.component.devicecare.datausage

import com.widgetworld.widgetcomponent.component.viewid.ViewIdType

sealed class DataUsageViewIdType(override val typeName: String) : ViewIdType() {
    object Text : DataUsageViewIdType("data_usage_text")
    object Progress : DataUsageViewIdType("data_usage_progress")

    companion object {
        fun all(): List<DataUsageViewIdType> = listOf(Text, Progress)
    }
}

