package com.widgetkit.core.component.battery.bluetooth.watch

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.core.component.battery.BatteryData
import com.widgetkit.core.component.battery.DeviceType
import com.widgetkit.core.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

internal object WatchBatteryPreferenceKey {
    val BatteryLevel = floatPreferencesKey("watch_battery_level")
    val BatteryConnected = booleanPreferencesKey("watch_battery_connected")
}

object WatchBatteryDataStore : ComponentDataStore<BatteryData>() {

    override val datastoreName = "watch_battery_pf"

    private val Context.watchBatteryDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: BatteryData) {
        context.watchBatteryDataStore.edit { preferences ->
            preferences[WatchBatteryPreferenceKey.BatteryLevel] = data.level
            preferences[WatchBatteryPreferenceKey.BatteryConnected] = data.isConnect
        }
    }

    override suspend fun loadData(context: Context): BatteryData {
        val preferences = context.watchBatteryDataStore.data.first()
        return BatteryData(
            level = preferences[WatchBatteryPreferenceKey.BatteryLevel] ?: 0f,
            charging = false,
            deviceType = DeviceType.BLUETOOTH_WATCH,
            isConnect = preferences[WatchBatteryPreferenceKey.BatteryConnected] ?: false
        )
    }

    override fun getDefaultData(): BatteryData {
        return BatteryData(
            level = 0f,
            charging = false,
            deviceType = DeviceType.BLUETOOTH_WATCH,
            isConnect = false
        )
    }
}
