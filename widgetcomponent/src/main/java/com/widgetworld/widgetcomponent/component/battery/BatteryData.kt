package com.widgetworld.widgetcomponent.component.battery

/**
 * Battery 컴포넌트의 데이터 모델
 * * @property level 배터리 잔량 (0.0 ~ 100.0)
 * @property charging 충전 중 여부
 * @property deviceType 기기 타입 (PHONE, WATCH, EARBUDS 등)
 * @property deviceName 기기 이름 (Bluetooth 기기인 경우)
 * @property deviceAddress 기기 주소 (Bluetooth 기기인 경우)
 * @property isConnect 연결 여부 (Bluetooth 기기인 경우)
 */
data class BatteryData(
    val level: Float,
    val charging: Boolean,
    val deviceType: DeviceType = DeviceType.PHONE,
    val deviceName: String? = null,
    val deviceAddress: String? = null,
    val isConnect: Boolean = true
)

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
