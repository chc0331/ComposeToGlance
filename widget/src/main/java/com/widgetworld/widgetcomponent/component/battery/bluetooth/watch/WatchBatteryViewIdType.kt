package com.widgetworld.widgetcomponent.component.battery.bluetooth.watch

import com.widgetworld.widgetcomponent.component.viewid.ViewIdType

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
