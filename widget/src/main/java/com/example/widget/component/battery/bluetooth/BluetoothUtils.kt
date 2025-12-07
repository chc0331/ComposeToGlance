package com.example.widget.component.battery.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.util.Log


private const val EXTRA_BATTERY_LEVEL = "android.bluetooth.device.extra.BATTERY_LEVEL"
private const val TAG = "BluetoothDeviceUtils"

internal fun Intent.getBluetoothDevice(): BluetoothDevice? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(
            BluetoothDevice.EXTRA_DEVICE,
            BluetoothDevice::class.java
        )
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }
}

internal fun Intent.getBatteryLevel(): Int {
    return try {
        // 표준 EXTRA_BATTERY_LEVEL
        if (hasExtra(EXTRA_BATTERY_LEVEL)) {
            val level = getIntExtra(EXTRA_BATTERY_LEVEL, -1)
            if (level in 0..100) {
                return level
            }
        }

        // 일부 제조사는 다른 키를 사용
        val possibleKeys = arrayOf(
            "battery_level",
            "BatteryLevel",
            "BATTERY_LEVEL"
        )

        for (key in possibleKeys) {
            if (hasExtra(key)) {
                val level = getIntExtra(key, -1)
                if (level in 0..100) {
                    Log.d(TAG, "Found battery level using key: $key = $level")
                    return level
                }
            }
        }

        -1
    } catch (e: Exception) {
        Log.e(TAG, "Error extracting battery level from intent", e)
        -1
    }
}

fun BluetoothDevice.getDeviceBatteryLevel(): Int {


    fun getLevelFromReflection(): Int {
        return try {
            val method = javaClass.getMethod("getBatteryLevel")
            val level = method.invoke(this) as? Int ?: -1

            if (level in 0..100) level else -1
        } catch (e: Exception) {
            -1
        }
    }

    fun getLevelFromMetadata(): Int {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val method = javaClass.getMethod("getMetadata", Int::class.java)
                // METADATA_MAIN_BATTERY = 0
                val mainBattery = method.invoke(this, 0) as? ByteArray
                if (mainBattery != null && mainBattery.isNotEmpty()) {
                    val level = String(mainBattery).toIntOrNull()
                    if (level != null && level in 0..100) {
                        return level
                    }
                }
            }
            -1
        } catch (e: Exception) {
            -1
        }
    }

    // 방법 1: getBatteryLevel() 리플렉션 (Android 9+에서 일부 기기 지원)
    var batteryLevel = getLevelFromReflection()
    if (batteryLevel >= 0) {
        Log.d(TAG, "Battery via reflection: $batteryLevel%")
        return batteryLevel
    }

    // 방법 2: getMetadata() 사용 (더 안정적)
    batteryLevel = getLevelFromMetadata()
    if (batteryLevel >= 0) {
        Log.d(TAG, "Battery via metadata: $batteryLevel%")
        return batteryLevel
    }
    Log.d(TAG, "Could not retrieve battery level for ${name}")
    return -1
}