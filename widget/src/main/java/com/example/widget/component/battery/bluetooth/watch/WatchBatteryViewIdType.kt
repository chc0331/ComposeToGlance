package com.example.widget.component.battery.bluetooth.watch

import com.example.widget.component.viewid.ViewIdType

sealed class WatchBatteryViewIdType(override val typeName: String) : ViewIdType() {
    object BatteryText : WatchBatteryViewIdType("watch_battery_text")
    object BatteryIcon : WatchBatteryViewIdType("watch_battery_icon")

    companion object {
        fun all(): List<WatchBatteryViewIdType> = listOf(
            BatteryText,
            BatteryIcon
        )
    }
}
