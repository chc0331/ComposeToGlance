package com.example.widget.component.devicecare

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager

data class DeviceState(
    val memoryUsage: Float,
    val totalMemory: Float,
    val storageUsage: Float,
    val totalStorage: Float
)
object DeviceStateCollector {

    fun collect(context: Context): DeviceState {
        val memoryInfo = MemoryCollector().collect(context)
        val storageInfo = StorageCollector().collect(context)

        return DeviceState(
            memoryUsage = memoryInfo.first,
            totalMemory = memoryInfo.second,
            storageUsage = storageInfo.first,
            totalStorage = storageInfo.second
        )
    }
}

class MemoryCollector {
    fun collect(context: Context): Pair<Float, Float> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: throw IllegalStateException("ActivityManager service not available.")

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamBytes = memoryInfo.totalMem // Total memory in bytes
        val availRamBytes = memoryInfo.availMem // Currently available memory in bytes
        val usedRamBytes = totalRamBytes - availRamBytes // Used memory in bytes

        // Constant for converting bytes to gigabytes
        val bytesToGb = 1024.0 * 1024.0 * 1024.0 // 1 GB = 1024^3 bytes

        // Convert bytes to GB
        val totalRamGb = totalRamBytes / bytesToGb
        val usedRamGb = usedRamBytes / bytesToGb

        // Format to two decimal places
        val formattedTotalRamGb = String.format("%.2f", totalRamGb).toFloat()
        val formattedUsedRamGb = String.format("%.2f", usedRamGb).toFloat()

        return Pair(formattedUsedRamGb, formattedTotalRamGb)
    }
}

class StorageCollector {

    fun collect(context: Context): Pair<Float, Float> {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
            ?: throw IllegalStateException("StorageManager service not available.")

        val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as? StorageStatsManager
            ?: throw IllegalStateException("StorageStatsManager service not available.")

        // Get UUID for the internal storage volume
        // Use getUuidForPath to get UUID type directly
        val uuid = storageManager.getUuidForPath(Environment.getDataDirectory())

        // Get total and free bytes using StorageStatsManager
        val totalStorageBytes = storageStatsManager.getTotalBytes(uuid)
        val freeStorageBytes = storageStatsManager.getFreeBytes(uuid)
        val usedStorageBytes = totalStorageBytes - freeStorageBytes

        // Constant for converting bytes to gigabytes
        val bytesToGb = 1024.0 * 1024.0 * 1024.0 // 1 GB = 1024^3 bytes

        // Convert bytes to GB
        val totalStorageGb = totalStorageBytes.toDouble() / bytesToGb
        val usedStorageGb = usedStorageBytes.toDouble() / bytesToGb

        // Format to two decimal places
        val formattedTotalStorageGb = String.format("%.1f", totalStorageGb).toFloat()
        val formattedUsedStorageGb = String.format("%.1f", usedStorageGb).toFloat()


        return Pair(formattedUsedStorageGb, formattedTotalStorageGb)
    }
}