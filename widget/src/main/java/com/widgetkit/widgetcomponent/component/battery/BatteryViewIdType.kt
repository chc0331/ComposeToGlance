package com.widgetkit.widgetcomponent.component.battery

import com.widgetkit.widgetcomponent.component.viewid.ViewIdType

sealed class BatteryViewIdType(override val typeName: String) : ViewIdType() {
    object Text : BatteryViewIdType("battery_text")
    object Icon : BatteryViewIdType("battery_icon")
    object ChargingIcon : BatteryViewIdType("charging_icon")
    companion object {
        fun all(): List<BatteryViewIdType> = listOf(Text, Icon, ChargingIcon)
    }
}
