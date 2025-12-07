package com.example.widget.component.battery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first

object BatteryPreferenceKey {
    val Level = floatPreferencesKey("phone_battery_value")
    val Charging = booleanPreferencesKey("phone_battery_charging")
}

data class BatteryData(
    val level: Float,
    val charging: Boolean,
    val deviceType: DeviceType = DeviceType.PHONE,
    val deviceName: String? = null,
    val deviceAddress: String? = null,
    val isConnect: Boolean = true
)

class BatteryInfoPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private val TAG = "BatteryInfoPreferencesRepo"

    suspend fun updateBatterInfo(data: BatteryData) {
        dataStore.updateData {
            it.toMutablePreferences().also { pref ->
                pref[BatteryPreferenceKey.Level] = data.level
                pref[BatteryPreferenceKey.Charging] = data.charging
            }
        }
    }

    suspend fun getBatteryInfo() = dataStore.data.first().run {
        val level = this[BatteryPreferenceKey.Level] ?: 0f
        val charging = this[BatteryPreferenceKey.Charging] ?: false
        BatteryData(level, charging)
    }
}
