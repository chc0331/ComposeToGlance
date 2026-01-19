package com.widgetworld.widgetcomponent.component.devicecare.datausage

data class DataUsageData(
    // Wi-Fi fields
    val wifiUsageBytes: Long = 0L,
    val wifiLimitBytes: Long = 0L,
    val wifiUsagePercent: Float = 0f,
    val wifiUsageGb: Float = 0f,
    val wifiLimitGb: Float = 0f,
    // Mobile Data fields
    val mobileUsageBytes: Long = 0L,
    val mobileLimitBytes: Long = 0L,
    val mobileUsagePercent: Float = 0f,
    val mobileUsageGb: Float = 0f,
    val mobileLimitGb: Float = 0f
) {
    companion object {
        // Default data limit: 5GB
        const val DEFAULT_DATA_LIMIT_GB = 5L
        const val BYTES_TO_GB = 1024.0 * 1024.0 * 1024.0 // 1 GB = 1024^3 bytes
        
        fun create(
            wifiUsageBytes: Long = 0L,
            wifiLimitBytes: Long = DEFAULT_DATA_LIMIT_GB * (1024 * 1024 * 1024),
            mobileUsageBytes: Long = 0L,
            mobileLimitBytes: Long = DEFAULT_DATA_LIMIT_GB * (1024 * 1024 * 1024)
        ): DataUsageData {
            // Calculate Wi-Fi fields
            val wifiUsageGb = (wifiUsageBytes.toDouble() / BYTES_TO_GB).toFloat()
            val wifiLimitGb = (wifiLimitBytes.toDouble() / BYTES_TO_GB).toFloat()
            val wifiUsagePercent = if (wifiLimitBytes > 0) {
                (wifiUsageBytes.toFloat() / wifiLimitBytes.toFloat() * 100f).coerceIn(0f, 100f)
            } else {
                0f
            }
            
            // Calculate Mobile Data fields
            val mobileUsageGb = (mobileUsageBytes.toDouble() / BYTES_TO_GB).toFloat()
            val mobileLimitGb = (mobileLimitBytes.toDouble() / BYTES_TO_GB).toFloat()
            val mobileUsagePercent = if (mobileLimitBytes > 0) {
                (mobileUsageBytes.toFloat() / mobileLimitBytes.toFloat() * 100f).coerceIn(0f, 100f)
            } else {
                0f
            }
            
            return DataUsageData(
                wifiUsageBytes = wifiUsageBytes,
                wifiLimitBytes = wifiLimitBytes,
                wifiUsagePercent = wifiUsagePercent,
                wifiUsageGb = wifiUsageGb,
                wifiLimitGb = wifiLimitGb,
                mobileUsageBytes = mobileUsageBytes,
                mobileLimitBytes = mobileLimitBytes,
                mobileUsagePercent = mobileUsagePercent,
                mobileUsageGb = mobileUsageGb,
                mobileLimitGb = mobileLimitGb
            )
        }
    }
}

