package com.example.widget.component.battery

import com.example.widget.component.viewid.ViewIdType

sealed class BatteryViewIdType(override val typeName: String) : ViewIdType() {
    object Text : BatteryViewIdType("battery_text")
    object Progress : BatteryViewIdType("battery_progress")
    object Icon : BatteryViewIdType("battery_icon")
    object ChargingIcon : BatteryViewIdType("charging_icon")

    companion object {
        fun all(): List<BatteryViewIdType> = listOf(Text, Progress, Icon, ChargingIcon)
    }
}