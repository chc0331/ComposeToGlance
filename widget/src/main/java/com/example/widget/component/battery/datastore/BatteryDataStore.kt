package com.example.widget.component.battery.datastore

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
    
    // Bluetooth devices - use device address as key
    object Bluetooth {
        // Set of all connected Bluetooth device addresses
        val DeviceAddresses = stringSetPreferencesKey("bluetooth_device_addresses")
        
        // Dynamic keys for each device
        fun levelKey(address: String) = floatPreferencesKey("bluetooth_${address}_level")
        fun chargingKey(address: String) = booleanPreferencesKey("bluetooth_${address}_charging")
        fun nameKey(address: String) = stringPreferencesKey("bluetooth_${address}_name")
        fun typeKey(address: String) = stringPreferencesKey("bluetooth_${address}_type")
    }
}

enum class DeviceType {
    PHONE,
    BLUETOOTH_HEADSET,
    BLUETOOTH_HEADPHONES,
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
                when (data.deviceType) {
                    DeviceType.PHONE -> {
                        pref[BatteryPreferenceKey.Phone.Level] = data.level
                        pref[BatteryPreferenceKey.Phone.Charging] = data.charging
                    }
                    else -> {
                        // Bluetooth device
                        data.deviceAddress?.let { address ->
                            // Add to device addresses set
                            val addresses = pref[BatteryPreferenceKey.Bluetooth.DeviceAddresses] ?: emptySet()
                            pref[BatteryPreferenceKey.Bluetooth.DeviceAddresses] = addresses + address
                            
                            // Store device data
                            pref[BatteryPreferenceKey.Bluetooth.levelKey(address)] = data.level
                            pref[BatteryPreferenceKey.Bluetooth.chargingKey(address)] = data.charging
                            pref[BatteryPreferenceKey.Bluetooth.nameKey(address)] = data.deviceName ?: "Unknown"
                            pref[BatteryPreferenceKey.Bluetooth.typeKey(address)] = data.deviceType.name
                        }
                    }
                }
            }
        }
    }

    suspend fun getBatteryInfo(device: DeviceType = DeviceType.PHONE) = dataStore.data.first().run {
        //todo : need to check device type.
        val level = this[BatteryPreferenceKey.Phone.Level] ?: 0f
        val charging = this[BatteryPreferenceKey.Phone.Charging] ?: false
        BatteryData(level, charging)
    }
    
    suspend fun getBluetoothDevices(): List<BatteryData> {
        return dataStore.data.first().run {
            val addresses = this[BatteryPreferenceKey.Bluetooth.DeviceAddresses] ?: emptySet()
            addresses.mapNotNull { address ->
                val level = this[BatteryPreferenceKey.Bluetooth.levelKey(address)] ?: return@mapNotNull null
                val charging = this[BatteryPreferenceKey.Bluetooth.chargingKey(address)] ?: false
                val name = this[BatteryPreferenceKey.Bluetooth.nameKey(address)] ?: "Unknown"
                val typeString = this[BatteryPreferenceKey.Bluetooth.typeKey(address)] ?: DeviceType.BLUETOOTH_UNKNOWN.name
                val deviceType = try {
                    DeviceType.valueOf(typeString)
                } catch (e: IllegalArgumentException) {
                    DeviceType.BLUETOOTH_UNKNOWN
                }
                
                BatteryData(
                    level = level,
                    charging = charging,
                    deviceType = deviceType,
                    deviceName = name,
                    deviceAddress = address
                )
            }
        }
    }
    
    suspend fun removeBluetoothDevice(address: String) {
        dataStore.updateData { pref ->
            pref.toMutablePreferences().also {
                val addresses = it[BatteryPreferenceKey.Bluetooth.DeviceAddresses] ?: emptySet()
                it[BatteryPreferenceKey.Bluetooth.DeviceAddresses] = addresses - address
                
                // Remove device data
                it.remove(BatteryPreferenceKey.Bluetooth.levelKey(address))
                it.remove(BatteryPreferenceKey.Bluetooth.chargingKey(address))
                it.remove(BatteryPreferenceKey.Bluetooth.nameKey(address))
                it.remove(BatteryPreferenceKey.Bluetooth.typeKey(address))
            }
        }
    }
}