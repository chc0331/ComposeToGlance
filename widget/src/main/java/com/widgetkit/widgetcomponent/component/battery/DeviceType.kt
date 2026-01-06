package com.widgetkit.widgetcomponent.component.battery

import com.widgetkit.widgetcomponent.R

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

fun getDeviceIcon(deviceType: DeviceType): Int {
    return when (deviceType) {
        DeviceType.PHONE -> R.drawable.ic_mobile_device
        DeviceType.BLUETOOTH_EARBUDS -> R.drawable.ic_bluetooth_earbuds // 무선 이어폰 아이콘
        DeviceType.BLUETOOTH_WATCH -> R.drawable.ic_bluetooth_watch
        else -> R.drawable.ic_mobile_device
    }
}
