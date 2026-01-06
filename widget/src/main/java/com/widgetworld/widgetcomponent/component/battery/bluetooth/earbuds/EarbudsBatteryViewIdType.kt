package com.widgetworld.widgetcomponent.component.battery.bluetooth.earbuds

import com.widgetworld.widgetcomponent.component.viewid.ViewIdType

sealed class EarbudsBatteryViewIdType(override val typeName: String) : ViewIdType() {
    object BatteryText : EarbudsBatteryViewIdType("earbuds_battery_text")
    object BatteryIcon : EarbudsBatteryViewIdType("earbuds_battery_icon")

    companion object {
        fun all(): List<EarbudsBatteryViewIdType> = listOf(
            BatteryText,
            BatteryIcon
        )
    }
}
