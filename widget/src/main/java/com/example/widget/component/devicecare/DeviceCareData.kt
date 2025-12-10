package com.example.widget.component.devicecare

/**
 * DeviceCare 컴포넌트의 데이터 모델
 * 
 * @property memoryUsageRatio 메모리 사용 비율 (0.0 ~ 1.0)
 * @property storageUsageRatio 저장소 사용 비율 (0.0 ~ 1.0)
 * @property cpuLoad CPU 부하 (0.0 ~ 1.0 이상)
 * @property temperatureCelsius 온도 (섭씨, 예: 25 ~ 50)
 */
data class DeviceState(
    val memoryUsageRatio: Float,   // 0.0 ~ 1.0 (사용 중 비율)
    val storageUsageRatio: Float,  // 0.0 ~ 1.0
    val cpuLoad: Float,            // 0.0 ~ 1.0 이상
    val temperatureCelsius: Float  // ex) 25 ~ 50
)

/**
 * 디바이스 상태 건강도
 */
enum class HealthStatus {
    GOOD,      // 양호
    WARNING,   // 경고
    CRITICAL   // 위험
}

