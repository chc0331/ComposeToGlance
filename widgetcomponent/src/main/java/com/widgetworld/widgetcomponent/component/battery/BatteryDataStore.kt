package com.widgetworld.widgetcomponent.component.battery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

internal object BatteryPreferenceKey {
    val Level = floatPreferencesKey("phone_battery_value")
    val Charging = booleanPreferencesKey("phone_battery_charging")
}

object BatteryComponentDataStore : ComponentDataStore<BatteryData>() {

    override val datastoreName = "battery_info_pf"
    private val Context.batteryDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: BatteryData) {
        context.batteryDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[BatteryPreferenceKey.Level] = data.level
                this[BatteryPreferenceKey.Charging] = data.charging
            }
        }
    }

    override suspend fun loadData(context: Context): BatteryData {
        val preferences = context.batteryDataStore.data.first()
        return BatteryData(
            level = preferences[BatteryPreferenceKey.Level] ?: 0f,
            charging = preferences[BatteryPreferenceKey.Charging] ?: false
        )
    }

    override fun getDefaultData(): BatteryData {
        return BatteryData(level = 0f, charging = false)
    }
}
