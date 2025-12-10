package com.example.widget.component.devicecare

private const val W_MEMORY = 0.35f
private const val W_STORAGE = 0.30f
private const val W_CPU = 0.20f
private const val W_TEMP = 0.15f

/**
 * 디바이스 건강도 계산기
 * 
 * 메모리, 저장소, CPU, 온도 상태를 종합하여 전체 점수를 계산합니다.
 */
object DeviceHealthCalculator {

    fun calculateScore(state: DeviceState): Int {
        val mem = memoryScore(state.memoryUsageRatio)
        val storage = storageScore(state.storageUsageRatio)
        val cpu = cpuScore(state.cpuLoad)
        val temp = temperatureScore(state.temperatureCelsius)

        val score =
            mem * W_MEMORY +
                    storage * W_STORAGE +
                    cpu * W_CPU +
                    temp * W_TEMP

        return score.toInt().coerceIn(0, 100)
    }

    fun toStatus(score: Int): HealthStatus = when {
        score >= 80 -> HealthStatus.GOOD
        score >= 50 -> HealthStatus.WARNING
        else -> HealthStatus.CRITICAL
    }

    // --- 아래는 앞에서 정의한 서브 함수들 ---

    private fun linearScore(
        value: Float, //현재 측정된 값
        goodMax: Float, //좋은 상태의 최소 값(해당 값 이하이면 최고 점수)
        badMin: Float //나쁜 상태의 최대 값(해당 값 이상이면 최저 점수)
    ): Int {
        if (value <= goodMax) return 100
        if (value >= badMin) return 0

        /**
         * Linear interpolation
         *
         * value가 goodMax에 가까울수록 높은 점수를 badMin에 가까울수록 낮은 점수를 얻는다.
         * */
        val ratio = (badMin - value) / (badMin - goodMax) // linear interpolation
        return (ratio * 100).toInt().coerceIn(0, 100)
    }

    private fun memoryScore(memoryUsageRatio: Float): Int =
        linearScore(memoryUsageRatio, goodMax = 0.6f, badMin = 0.95f)

    private fun storageScore(storageUsageRatio: Float): Int =
        linearScore(storageUsageRatio, goodMax = 0.7f, badMin = 0.95f)

    private fun cpuScore(cpuLoad: Float): Int =
        linearScore(cpuLoad, goodMax = 0.4f, badMin = 1.0f)

    private fun temperatureScore(temperatureCelsius: Float): Int =
        linearScore(temperatureCelsius, goodMax = 35f, badMin = 45f)
}