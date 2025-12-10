package com.example.widget.component.devicecare

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.widget.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

/**
 * DeviceCare 컴포넌트의 PreferenceKey
 */
internal object DeviceCarePreferenceKey {
    val MemoryUsageRatio = floatPreferencesKey("device_care_memory_usage_ratio")
    val StorageUsageRatio = floatPreferencesKey("device_care_storage_usage_ratio")
    val CpuLoad = floatPreferencesKey("device_care_cpu_load")
    val TemperatureCelsius = floatPreferencesKey("device_care_temperature_celsius")
}

/**
 * DeviceCare 컴포넌트의 DataStore
 * 
 * ComponentDataStore를 상속하여 표준화된 데이터 저장/로드를 제공합니다.
 */
object DeviceCareComponentDataStore : ComponentDataStore<DeviceState>() {
    
    override val datastoreName = "device_care_pf"
    
    // DataStore 인스턴스 생성
    private val Context.deviceCareDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )
    
    override suspend fun saveData(context: Context, data: DeviceState) {
        context.deviceCareDataStore.edit { preferences ->
            preferences[DeviceCarePreferenceKey.MemoryUsageRatio] = data.memoryUsageRatio
            preferences[DeviceCarePreferenceKey.StorageUsageRatio] = data.storageUsageRatio
            preferences[DeviceCarePreferenceKey.CpuLoad] = data.cpuLoad
            preferences[DeviceCarePreferenceKey.TemperatureCelsius] = data.temperatureCelsius
        }
    }
    
    override suspend fun loadData(context: Context): DeviceState {
        val preferences = context.deviceCareDataStore.data.first()
        return DeviceState(
            memoryUsageRatio = preferences[DeviceCarePreferenceKey.MemoryUsageRatio] ?: 0f,
            storageUsageRatio = preferences[DeviceCarePreferenceKey.StorageUsageRatio] ?: 0f,
            cpuLoad = preferences[DeviceCarePreferenceKey.CpuLoad] ?: 0f,
            temperatureCelsius = preferences[DeviceCarePreferenceKey.TemperatureCelsius] ?: 0f
        )
    }
    
    override fun getDefaultData(): DeviceState {
        return DeviceState(
            memoryUsageRatio = 0f,
            storageUsageRatio = 0f,
            cpuLoad = 0f,
            temperatureCelsius = 0f
        )
    }
}

