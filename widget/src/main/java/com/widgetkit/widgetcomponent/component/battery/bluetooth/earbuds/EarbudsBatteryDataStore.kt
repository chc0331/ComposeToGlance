package com.widgetkit.widgetcomponent.component.battery.bluetooth.earbuds

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.widgetcomponent.component.battery.BatteryData
import com.widgetkit.widgetcomponent.component.battery.DeviceType
import com.widgetkit.widgetcomponent.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

internal object EarbudsBatteryPreferenceKey {
    val BatteryLevel = floatPreferencesKey("earbuds_battery_level")
    val BatteryConnected = booleanPreferencesKey("earbuds_battery_connected")
}

object EarbudsBatteryDataStore : ComponentDataStore<BatteryData>() {

    override val datastoreName = "earbuds_battery_pf"

    private val Context.earbudsBatteryDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: BatteryData) {
        context.earbudsBatteryDataStore.edit { preferences ->
            preferences[EarbudsBatteryPreferenceKey.BatteryLevel] = data.level
            preferences[EarbudsBatteryPreferenceKey.BatteryConnected] = data.isConnect
        }
    }

    override suspend fun loadData(context: Context): BatteryData {
        val preferences = context.earbudsBatteryDataStore.data.first()
        return BatteryData(
            level = preferences[EarbudsBatteryPreferenceKey.BatteryLevel] ?: 0f,
            charging = false,
            deviceType = DeviceType.BLUETOOTH_EARBUDS,
            isConnect = preferences[EarbudsBatteryPreferenceKey.BatteryConnected] ?: false
        )
    }

    override fun getDefaultData(): BatteryData {
        return BatteryData(
            level = 0f,
            charging = false,
            deviceType = DeviceType.BLUETOOTH_EARBUDS,
            isConnect = false
        )
    }
}
