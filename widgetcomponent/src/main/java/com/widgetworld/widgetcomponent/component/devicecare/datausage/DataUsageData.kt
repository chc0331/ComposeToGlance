package com.widgetworld.widgetcomponent.component.devicecare.datausage

data class DataUsageData(
    val currentUsageBytes: Long,
    val dataLimitBytes: Long,
    val usagePercent: Float,
    val currentUsageGb: Float,
    val dataLimitGb: Float
) {
    companion object {
        // Default data limit: 5GB
        const val DEFAULT_DATA_LIMIT_GB = 5L
        const val BYTES_TO_GB = 1024.0 * 1024.0 * 1024.0 // 1 GB = 1024^3 bytes
        
        fun create(
            currentUsageBytes: Long,
            dataLimitBytes: Long = DEFAULT_DATA_LIMIT_GB * (1024 * 1024 * 1024)
        ): DataUsageData {
            val currentUsageGb = (currentUsageBytes.toDouble() / BYTES_TO_GB).toFloat()
            val dataLimitGb = (dataLimitBytes.toDouble() / BYTES_TO_GB).toFloat()
            val usagePercent = if (dataLimitBytes > 0) {
                (currentUsageBytes.toFloat() / dataLimitBytes.toFloat() * 100f).coerceIn(0f, 100f)
            } else {
                0f
            }
            
            return DataUsageData(
                currentUsageBytes = currentUsageBytes,
                dataLimitBytes = dataLimitBytes,
                usagePercent = usagePercent,
                currentUsageGb = currentUsageGb,
                dataLimitGb = dataLimitGb
            )
        }
    }
}

