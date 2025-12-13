package com.example.widget.component.battery.bluetooth

import com.example.widget.component.viewid.ViewIdType

sealed class BluetoothBatteryViewIdType(override val typeName: String) : ViewIdType() {
    object EarBudsBatteryText : BluetoothBatteryViewIdType("ear_buds_battery_text")
    object EarBudsBatteryIcon : BluetoothBatteryViewIdType("ear_buds_battery_icon")
    object WatchBatteryText : BluetoothBatteryViewIdType("watch_battery_text")
    object WatchBatteryIcon : BluetoothBatteryViewIdType("watch_battery_icon")

    companion object {
        fun all(): List<BluetoothBatteryViewIdType> = listOf(
            EarBudsBatteryText,
            EarBudsBatteryIcon,
            WatchBatteryText,
            WatchBatteryIcon
        )
    }
}