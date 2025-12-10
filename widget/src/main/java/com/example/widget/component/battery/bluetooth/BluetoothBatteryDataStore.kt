package com.example.widget.component.battery.bluetooth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

/**
 * BluetoothBattery 컴포넌트의 PreferenceKey
 */
internal object BluetoothBatteryPreferenceKey {
    val BtEarbudsLevel = floatPreferencesKey("bt_earbuds_battery_value")
    val BtEarbudsConnected = booleanPreferencesKey("bt_earbuds_connected")
    val BtWatchLevel = floatPreferencesKey("bt_watch_battery_value")
    val BtWatchConnected = booleanPreferencesKey("bt_watch_connected")
}

/**
 * BluetoothBattery 컴포넌트의 복합 데이터 모델
 * 여러 Bluetooth 기기의 배터리 정보를 함께 저장합니다.
 */
data class BluetoothBatteryCompositeData(
    val earbudsData: BatteryData,
    val watchData: BatteryData
)

/**
 * BluetoothBattery 컴포넌트의 DataStore
 * 
 * ComponentDataStore를 상속하여 표준화된 데이터 저장/로드를 제공합니다.
 * 여러 Bluetooth 기기의 데이터를 함께 관리합니다.
 */
object BluetoothBatteryComponentDataStore : ComponentDataStore<BluetoothBatteryCompositeData>() {
    
    override val datastoreName = "bluetooth_battery_info_pf"
    
    // DataStore 인스턴스 생성
    private val Context.bluetoothBatteryDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )
    
    override suspend fun saveData(context: Context, data: BluetoothBatteryCompositeData) {
        context.bluetoothBatteryDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                // Earbuds 데이터
                this[BluetoothBatteryPreferenceKey.BtEarbudsLevel] = data.earbudsData.level
                this[BluetoothBatteryPreferenceKey.BtEarbudsConnected] = data.earbudsData.isConnect
                
                // Watch 데이터
                this[BluetoothBatteryPreferenceKey.BtWatchLevel] = data.watchData.level
                this[BluetoothBatteryPreferenceKey.BtWatchConnected] = data.watchData.isConnect
            }
        }
    }
    
    /**
     * 개별 기기의 데이터만 업데이트합니다.
     */
    suspend fun updateDeviceData(context: Context, data: BatteryData) {
        context.bluetoothBatteryDataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                // 정상적인 배터리 값(0-100 사이)일 때만 업데이트
                val isValidBatteryLevel = data.level in 0f..100f
                
                when (data.deviceType) {
                    DeviceType.BLUETOOTH_EARBUDS -> {
                        this[BluetoothBatteryPreferenceKey.BtEarbudsConnected] = data.isConnect
                        if (isValidBatteryLevel) {
                            this[BluetoothBatteryPreferenceKey.BtEarbudsLevel] = data.level
                        }
                    }
                    DeviceType.BLUETOOTH_WATCH -> {
                        this[BluetoothBatteryPreferenceKey.BtWatchConnected] = data.isConnect
                        if (isValidBatteryLevel) {
                            this[BluetoothBatteryPreferenceKey.BtWatchLevel] = data.level
                        }
                    }
                    else -> {
                        // 다른 타입은 무시
                    }
                }
            }
        }
    }
    
    override suspend fun loadData(context: Context): BluetoothBatteryCompositeData {
        val preferences = context.bluetoothBatteryDataStore.data.first()
        return BluetoothBatteryCompositeData(
            earbudsData = BatteryData(
                level = preferences[BluetoothBatteryPreferenceKey.BtEarbudsLevel] ?: 0f,
                charging = false,
                deviceType = DeviceType.BLUETOOTH_EARBUDS,
                isConnect = preferences[BluetoothBatteryPreferenceKey.BtEarbudsConnected] ?: false
            ),
            watchData = BatteryData(
                level = preferences[BluetoothBatteryPreferenceKey.BtWatchLevel] ?: 0f,
                charging = false,
                deviceType = DeviceType.BLUETOOTH_WATCH,
                isConnect = preferences[BluetoothBatteryPreferenceKey.BtWatchConnected] ?: false
            )
        )
    }
    
    /**
     * 특정 기기 타입의 데이터만 로드합니다.
     */
    suspend fun loadDeviceData(context: Context, deviceType: DeviceType): BatteryData {
        val compositeData = loadData(context)
        return when (deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> compositeData.earbudsData
            DeviceType.BLUETOOTH_WATCH -> compositeData.watchData
            else -> getDefaultData().earbudsData
        }
    }
    
    override fun getDefaultData(): BluetoothBatteryCompositeData {
        return BluetoothBatteryCompositeData(
            earbudsData = BatteryData(
                level = 0f,
                charging = false,
                deviceType = DeviceType.BLUETOOTH_EARBUDS,
                isConnect = false
            ),
            watchData = BatteryData(
                level = 0f,
                charging = false,
                deviceType = DeviceType.BLUETOOTH_WATCH,
                isConnect = false
            )
        )
    }
}

