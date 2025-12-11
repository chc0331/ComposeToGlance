package com.example.widget.component.devicecare

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.widget.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

internal object DeviceCarePreferenceKey {
    val MemoryUsage = floatPreferencesKey("device_care_memory_usage_ratio")
    val TotalMemory = floatPreferencesKey("device_care_total_memory")
    val StorageUsage = floatPreferencesKey("device_care_storage_usage_ratio")
    val TotalStorage = floatPreferencesKey("device_care_total_storage")
}

object DeviceCareComponentDataStore : ComponentDataStore<DeviceState>() {

    override val datastoreName = "device_care_pf"

    private val Context.deviceCareDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: DeviceState) {
        context.deviceCareDataStore.edit { preferences ->
            preferences[DeviceCarePreferenceKey.MemoryUsage] = data.memoryUsage
            preferences[DeviceCarePreferenceKey.TotalMemory] = data.totalMemory
            preferences[DeviceCarePreferenceKey.StorageUsage] = data.storageUsage
            preferences[DeviceCarePreferenceKey.TotalStorage] = data.totalStorage
        }
    }

    override suspend fun loadData(context: Context): DeviceState {
        val preferences = context.deviceCareDataStore.data.first()
        return DeviceState(
            memoryUsage = preferences[DeviceCarePreferenceKey.MemoryUsage] ?: 0f,
            totalMemory = preferences[DeviceCarePreferenceKey.TotalMemory] ?: 0f,
            storageUsage = preferences[DeviceCarePreferenceKey.StorageUsage] ?: 0f,
            totalStorage = preferences[DeviceCarePreferenceKey.TotalStorage] ?: 0f
        )
    }

    override fun getDefaultData(): DeviceState {
        return DeviceState(
            memoryUsage = 0f,
            totalMemory = 0f,
            storageUsage = 0f,
            totalStorage = 0f
        )
    }
}

