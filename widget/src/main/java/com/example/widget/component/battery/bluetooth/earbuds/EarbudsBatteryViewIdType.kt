package com.example.widget.component.battery.bluetooth.earbuds

import com.example.widget.component.viewid.ViewIdType

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
