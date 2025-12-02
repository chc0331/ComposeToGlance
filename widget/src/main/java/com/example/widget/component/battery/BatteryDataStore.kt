package com.example.widget.component.battery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.first

object BatteryPreferenceKey {
    object Phone {
        val Level = floatPreferencesKey("phone_battery_value")
        val Charging = booleanPreferencesKey("phone_battery_charging")
    }
}

enum class DeviceType {
    PHONE
}

data class BatteryData(
    val level: Float,
    val charging: Boolean,
    val deviceType: DeviceType = DeviceType.PHONE
)

class BatteryInfoPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private val TAG = "BatteryInfoPreferencesRepo"

    suspend fun updateBatterInfo(data: BatteryData) {
        dataStore.updateData {
            it.toMutablePreferences().also { pref ->
                pref[BatteryPreferenceKey.Phone.Level] = data.level
                pref[BatteryPreferenceKey.Phone.Charging] = data.charging
            }
        }
    }

    suspend fun getBatteryInfo(device: DeviceType = DeviceType.PHONE) = dataStore.data.first().run {
        //todo : need to check device type.
        val level = this[BatteryPreferenceKey.Phone.Level] ?: 0f
        val charging = this[BatteryPreferenceKey.Phone.Charging] ?: false
        BatteryData(level, charging)
    }
}