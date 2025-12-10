package com.example.widget.component.battery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.widget.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

/**
 * Battery 컴포넌트의 PreferenceKey
 */
internal object BatteryPreferenceKey {
    val Level = floatPreferencesKey("phone_battery_value")
    val Charging = booleanPreferencesKey("phone_battery_charging")
}

/**
 * Battery 컴포넌트의 DataStore
 * 
 * ComponentDataStore를 상속하여 표준화된 데이터 저장/로드를 제공합니다.
 */
object BatteryComponentDataStore : ComponentDataStore<BatteryData>() {
    
    override val datastoreName = "battery_info_pf"
    
    // DataStore 인스턴스 생성
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

