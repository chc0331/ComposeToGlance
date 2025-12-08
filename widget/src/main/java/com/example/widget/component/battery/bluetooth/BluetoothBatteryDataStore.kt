package com.example.widget.component.battery.bluetooth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.DeviceType
import kotlinx.coroutines.flow.first

object BluetoothBatteryPreferenceKey {
    val BtEarbudsLevel = floatPreferencesKey("bt_earbuds_battery_value")
    val BtEarbudsConnected = booleanPreferencesKey("bt_earbuds_connected")
    val BtWatchLevel = floatPreferencesKey("bt_watch_battery_value")
    val BtWatchConnected = booleanPreferencesKey("bt_watch_connected")
}

class BluetoothBatteryInfoPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    suspend fun updateBluetoothBatteryInfo(data: BatteryData) {
        dataStore.updateData {
            it.toMutablePreferences().also { pref ->
                if (data.deviceType == DeviceType.BLUETOOTH_EARBUDS) {
                    pref[BluetoothBatteryPreferenceKey.BtEarbudsConnected] = data.isConnect
                    pref[BluetoothBatteryPreferenceKey.BtEarbudsLevel] = data.level
                } else if (data.deviceType == DeviceType.BLUETOOTH_WATCH) {
                    pref[BluetoothBatteryPreferenceKey.BtWatchConnected] = data.isConnect
                    pref[BluetoothBatteryPreferenceKey.BtWatchLevel] = data.level
                }
            }
        }
    }

    suspend fun getBluetoothBatteryInfo(deviceType: DeviceType) = dataStore.data.first().run {
        if (deviceType == DeviceType.BLUETOOTH_EARBUDS) {
            BatteryData(
                level = this[BluetoothBatteryPreferenceKey.BtEarbudsLevel] ?: 0f,
                charging = false,
                isConnect = this[BluetoothBatteryPreferenceKey.BtEarbudsConnected] ?: false
            )
        } else if (deviceType == DeviceType.BLUETOOTH_WATCH) {
            BatteryData(
                level = this[BluetoothBatteryPreferenceKey.BtWatchLevel] ?: 0f,
                charging = false,
                isConnect = this[BluetoothBatteryPreferenceKey.BtWatchConnected] ?: false
            )
        } else {
            BatteryData(level = 0f, charging = false)
        }
    }
}
