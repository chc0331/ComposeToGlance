package com.example.widget.component.battery.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.widget.component.battery.DeviceType

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
    Log.d(TAG, "Could not retrieve battery level for $name")
    return -1
}

/**
 * BluetoothDevice의 타입을 DeviceType enum으로 반환합니다.
 * BluetoothClass를 사용하여 디바이스 타입을 판별합니다.
 *
 * 판별 방법:
 * 1. BluetoothClass의 Major/Device Class 사용 (가장 정확)
 * 2. 디바이스 이름 기반 추론 (fallback)
 *
 * 구분:
 * - BLUETOOTH_EARBUDS: 무선 이어폰 (Galaxy Buds, AirPods 등)
 * - BLUETOOTH_HEADPHONES: 헤드폰 (오버이어/온이어)
 * - BLUETOOTH_HEADSET: 통화용 헤드셋
 *
 * @return DeviceType - 디바이스 타입 (판별 불가능한 경우 BLUETOOTH_UNKNOWN)
 */
@SuppressLint("MissingPermission")
fun BluetoothDevice.getDeviceType(): DeviceType {
    return try {
        val bluetoothClass = bluetoothClass
        if (bluetoothClass == null) {
            Log.d(TAG, "BluetoothClass is null for device: $name")
            return inferDeviceTypeFromName(name)
        }

        val majorDeviceClass = bluetoothClass.majorDeviceClass
        val deviceClass = bluetoothClass.deviceClass

        Log.d(
            TAG,
            "Device: $name | MajorClass: $majorDeviceClass | DeviceClass: $deviceClass"
        )

        // 주요 디바이스 클래스로 먼저 판별
        when (majorDeviceClass) {
            // 웨어러블 디바이스 (스마트워치 등)
            BluetoothClass.Device.Major.WEARABLE -> {
                when (deviceClass) {
                    BluetoothClass.Device.WEARABLE_WRIST_WATCH -> DeviceType.BLUETOOTH_WATCH
                    else -> {
                        Log.d(TAG, "Unknown wearable device class: ${deviceClass.toHex()}")
                        inferDeviceTypeFromName(name)
                    }
                }
            }

            // 오디오/비디오 디바이스 (헤드폰, 이어폰, 스피커 등)
            BluetoothClass.Device.Major.AUDIO_VIDEO -> {
                when (deviceClass) {
                    // BluetoothClass만으로는 이어폰/헤드폰 구분 어려움
                    // 이름으로 추가 판별 필요
                    BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> {
                        // 이름 기반으로 이어폰인지 헤드폰인지 구분
                        val nameBasedType = inferDeviceTypeFromName(name)
                        when (nameBasedType) {
                            DeviceType.BLUETOOTH_EARBUDS -> DeviceType.BLUETOOTH_EARBUDS
                            DeviceType.BLUETOOTH_HEADPHONES -> DeviceType.BLUETOOTH_HEADPHONES
                            else -> DeviceType.BLUETOOTH_EARBUDS // 기본값: 무선 이어폰
                        }
                    }

                    // 웨어러블 헤드셋 & 핸즈프리
                    // Galaxy Buds 등이 이 클래스로 리포트되므로 이름 체크 필요
                    BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> {
                        val nameBasedType = inferDeviceTypeFromName(name)
                        when (nameBasedType) {
                            DeviceType.BLUETOOTH_EARBUDS -> DeviceType.BLUETOOTH_EARBUDS
                            DeviceType.BLUETOOTH_HEADPHONES -> DeviceType.BLUETOOTH_HEADPHONES
                            else -> DeviceType.BLUETOOTH_HEADSET // 기본값: 헤드셋
                        }
                    }
                    BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE -> {
                        val nameBasedType = inferDeviceTypeFromName(name)
                        when (nameBasedType) {
                            DeviceType.BLUETOOTH_EARBUDS -> DeviceType.BLUETOOTH_EARBUDS
                            DeviceType.BLUETOOTH_HEADPHONES -> DeviceType.BLUETOOTH_HEADPHONES
                            else -> DeviceType.BLUETOOTH_HEADSET // 기본값: 헤드셋
                        }
                    }


                    // 스피커
                    BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO -> DeviceType.BLUETOOTH_SPEAKER
                    BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER -> DeviceType.BLUETOOTH_SPEAKER
                    BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO -> DeviceType.BLUETOOTH_SPEAKER

                    // 보청기 (일부 기기는 0x2414 사용)
                    0x2414 -> DeviceType.BLUETOOTH_HEARING_AID

                    // Uncategorized 오디오 - 이름으로 판별
                    BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED -> {
                        val nameBasedType = inferDeviceTypeFromName(name)
                        if (nameBasedType != DeviceType.BLUETOOTH_UNKNOWN) {
                            nameBasedType
                        } else {
                            DeviceType.BLUETOOTH_HEARING_AID
                        }
                    }

                    else -> {
                        Log.d(TAG, "Unknown audio/video device class: ${deviceClass.toHex()}")
                        inferDeviceTypeFromName(name)
                    }
                }
            }

            else -> {
                Log.d(TAG, "Unknown major device class: ${majorDeviceClass.toHex()}")
                // 이름으로 추정 시도
                inferDeviceTypeFromName(name)
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error determining device type for $name", e)
        DeviceType.BLUETOOTH_UNKNOWN
    }
}

/**
 * 디바이스 이름을 기반으로 디바이스 타입을 추정합니다.
 * BluetoothClass로 판별이 안 될 때 사용하는 보조 메서드입니다.
 * * 순서가 중요합니다:
 * - 더 구체적인 패턴(buds, airpods)을 먼저 체크 → BLUETOOTH_EARBUDS
 * - 헤드폰 패턴 체크 → BLUETOOTH_HEADPHONES
 * - 헤드셋 패턴 체크 → BLUETOOTH_HEADSET
 */
private fun inferDeviceTypeFromName(deviceName: String?): DeviceType {
    if (deviceName == null) {
        return DeviceType.BLUETOOTH_UNKNOWN
    }

    val nameLower = deviceName.lowercase()

    return when {
        // 워치 패턴 (최우선)
        nameLower.contains("watch") || nameLower.contains("시계") ||
            nameLower.contains("galaxy watch") ||
            nameLower.contains("apple watch") ||
            nameLower.contains("gear s") || // Gear S = 워치
            nameLower.contains("fitbit") -> DeviceType.BLUETOOTH_WATCH

        // 무선 이어폰 패턴 (구체적인 것 우선, headset/headphone보다 먼저 체크)
        nameLower.contains("buds") || // Galaxy Buds, Pixel Buds
            nameLower.contains("airpods") || // AirPods
            nameLower.contains("earbud") || // Generic earbuds
            nameLower.contains("이어폰") ||
            nameLower.contains("pods") || // Pods 계열
            nameLower.contains("freebuds") || // Huawei FreeBuds
            nameLower.contains("earphone") || // Earphones
            nameLower.contains("tws") || // True Wireless Stereo
            nameLower.contains("in-ear") || // In-ear type
            nameLower.contains("wireless earphone") -> DeviceType.BLUETOOTH_EARBUDS // 🔄 변경: EARBUDS

        // 헤드폰 패턴 (오버이어/온이어 헤드폰)
        nameLower.contains("headphone") || nameLower.contains("헤드폰") ||
            nameLower.contains("wh-") || // Sony 헤드폰 (WH-1000XM 등)
            nameLower.contains("qc") || // Bose QuietComfort
            nameLower.contains("over-ear") ||
            nameLower.contains("on-ear") -> DeviceType.BLUETOOTH_HEADPHONES

        // 헤드셋 패턴 (일반적으로 한쪽 귀, 통화용)
        nameLower.contains("headset") || nameLower.contains("헤드셋") ||
            nameLower.contains("handsfree") ||
            nameLower.contains("car kit") || // 차량용 헤드셋
            nameLower.contains("mono") -> DeviceType.BLUETOOTH_HEADSET // 모노 헤드셋

        // 스피커 패턴
        nameLower.contains("speaker") || nameLower.contains("스피커") ||
            nameLower.contains("soundbar") ||
            nameLower.contains("homepod") ||
            nameLower.contains("echo") || // Amazon Echo
            nameLower.contains("nest") || // Google Nest
            nameLower.contains("jbl") || // JBL speakers (많은 경우)
            nameLower.contains("boombox") -> DeviceType.BLUETOOTH_SPEAKER

        // 보청기 패턴
        nameLower.contains("hearing") ||
        nameLower.contains("보청기") -> DeviceType.BLUETOOTH_HEARING_AID


        else -> DeviceType.BLUETOOTH_UNKNOWN
    }
}

private fun Int.toHex(): String {
    return "%04X".format(this) // 결과: "000A"
}
