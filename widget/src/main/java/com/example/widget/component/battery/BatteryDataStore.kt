package com.example.widget.component.battery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first

object BatteryPreferenceKey {
    object Phone {
        val Level = floatPreferencesKey("phone_battery_value")
        val Charging = booleanPreferencesKey("phone_battery_charging")
    }
}

/**
 * 블루투스 디바이스 타입
 * * - PHONE: 스마트폰
 * - BLUETOOTH_EARBUDS: 무선 이어폰 (Galaxy Buds, AirPods 등)
 * - BLUETOOTH_HEADPHONES: 헤드폰 (오버이어/온이어)
 * - BLUETOOTH_HEADSET: 통화용 헤드셋 (모노, 차량용 등)
 * - BLUETOOTH_WATCH: 스마트워치
 * - BLUETOOTH_SPEAKER: 블루투스 스피커
 * - BLUETOOTH_HEARING_AID: 보청기
 * - BLUETOOTH_UNKNOWN: 알 수 없는 기기
 */
enum class DeviceType {
    PHONE,
    BLUETOOTH_EARBUDS, // 무선 이어폰 (TWS)
    BLUETOOTH_HEADPHONES, // 헤드폰 (오버이어/온이어)
    BLUETOOTH_HEADSET, // 통화용 헤드셋
    BLUETOOTH_WATCH,
    BLUETOOTH_SPEAKER,
    BLUETOOTH_HEARING_AID,
    BLUETOOTH_UNKNOWN
}

data class BatteryData(
    val level: Float,
    val charging: Boolean,
    val deviceType: DeviceType = DeviceType.PHONE,
    val deviceName: String? = null,
    val deviceAddress: String? = null
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
        // todo : need to check device type.
        val level = this[BatteryPreferenceKey.Phone.Level] ?: 0f
        val charging = this[BatteryPreferenceKey.Phone.Charging] ?: false
        BatteryData(level, charging)
    }
}
