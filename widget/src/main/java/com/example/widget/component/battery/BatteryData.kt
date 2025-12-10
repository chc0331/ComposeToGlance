package com.example.widget.component.battery

/**
 * Battery 컴포넌트의 데이터 모델
 * 
 * @property level 배터리 잔량 (0.0 ~ 100.0)
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

