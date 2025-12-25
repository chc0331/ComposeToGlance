package com.widgetkit.core.component.devicecare

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log

data class DeviceState(
    val memoryUsage: Float,
    val totalMemory: Float,
    val storageUsage: Float,
    val totalStorage: Float
)

data class StorageDetailInfo(
    val totalStorageGb: Float,
    val usedStorageGb: Float,
    val freeStorageGb: Float,
    val usagePercent: Float,
    val appStorageList: List<AppStorageInfo>,
    val storageBreakdown: StorageBreakdown
)

data class StorageBreakdown(
    val appSizeGb: Float, // 앱 크기 (APK)
    val appDataGb: Float, // 앱 데이터
    val appCacheGb: Float, // 앱 캐시
    val totalAppStorageGb: Float, // 앱 관련 총합
    val otherStorageGb: Float // 기타 (시스템, 미디어 등)
)

data class AppStorageInfo(
    val packageName: String,
    val appName: String,
    val storageGb: Float
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

    suspend fun collectDetailed(context: Context): StorageDetailInfo {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
            ?: throw IllegalStateException("StorageManager service not available.")

        val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as? StorageStatsManager
            ?: throw IllegalStateException("StorageStatsManager service not available.")

        val packageManager = context.packageManager

        // Get UUID for the internal storage volume
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
        val freeStorageGb = freeStorageBytes.toDouble() / bytesToGb

        // Format to one decimal place
        val formattedTotalStorageGb = String.format("%.1f", totalStorageGb).toFloat()
        val formattedUsedStorageGb = String.format("%.1f", usedStorageGb).toFloat()
        val formattedFreeStorageGb = String.format("%.1f", freeStorageGb).toFloat()

        // Calculate usage percentage
        val usagePercent = if (totalStorageGb > 0) {
            (usedStorageGb / totalStorageGb * 100).toFloat()
        } else {
            0f
        }

        // Collect app storage information and calculate breakdown
        val appStorageList = mutableListOf<AppStorageInfo>()
        var totalAppBytes: Long = 0
        var totalDataBytes: Long = 0
        var totalCacheBytes: Long = 0

        try {
            val userHandle = android.os.Process.myUserHandle()

            // Get packages that can be queried (launcher apps from queries tag)
            // This works without QUERY_ALL_PACKAGES permission on Android 11+
            val launcherIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            }
            val launcherApps = packageManager.queryIntentActivities(launcherIntent, 0)
            val queryablePackages = launcherApps.map { it.activityInfo.packageName }.distinct()

            Log.d("StorageCollector", "Found ${launcherApps.size} launcher activities")
            Log.d("StorageCollector", "Unique queryable packages: ${queryablePackages.size}")
            Log.d("StorageCollector", "Android version: ${Build.VERSION.SDK_INT}")

            // Also include current app package if not already in list
            val packagesToQuery = if (context.packageName !in queryablePackages) {
                queryablePackages + context.packageName
            } else {
                queryablePackages
            }

            Log.d("StorageCollector", "Total packages to query: ${packagesToQuery.size}")

            var skippedApplicationInfo = 0
            var skippedStorageStats = 0
            var skippedZeroStorage = 0
            var processedCount = 0

            // Process each queryable package directly
            for (packageName in packagesToQuery) {
                try {
                    // First, try to get ApplicationInfo to filter and get app name
                    val applicationInfo = try {
                        packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                        )
                    } catch (e: Exception) {
                        skippedApplicationInfo++
                        Log.d(
                            "StorageCollector",
                            "Failed to get ApplicationInfo for $packageName: ${e.message}"
                        )
                        continue
                    }

                    // Get app name
                    val appName = try {
                        packageManager.getApplicationLabel(applicationInfo).toString()
                    } catch (e: Exception) {
                        packageName
                    }

                    // Try to query storage stats
                    val appBytes = try {
                        storageStatsManager.queryStatsForPackage(
                            uuid,
                            packageName,
                            userHandle
                        )
                    } catch (e: SecurityException) {
                        // PACKAGE_USAGE_STATS permission not granted
                        if (e.message?.contains("PACKAGE_USAGE_STATS") == true) {
                            Log.w(
                                "StorageCollector",
                                "PACKAGE_USAGE_STATS permission required for $packageName"
                            )
                            // If this is the first app and we get permission error, we might want to throw
                            // But for now, just skip
                        }
                        skippedStorageStats++
                        continue
                    } catch (e: Exception) {
                        skippedStorageStats++
                        Log.d(
                            "StorageCollector",
                            "Failed to query storage stats for $packageName: ${e.message}"
                        )
                        continue
                    }

                    val appStorageBytes = appBytes.appBytes + appBytes.dataBytes + appBytes.cacheBytes
                    if (appStorageBytes > 0) {
                        // Accumulate totals for breakdown
                        totalAppBytes += appBytes.appBytes
                        totalDataBytes += appBytes.dataBytes
                        totalCacheBytes += appBytes.cacheBytes

                        val appStorageGb = appStorageBytes.toDouble() / bytesToGb
                        val formattedAppStorageGb = String.format("%.2f", appStorageGb).toFloat()

                        appStorageList.add(
                            AppStorageInfo(
                                packageName = packageName,
                                appName = appName,
                                storageGb = formattedAppStorageGb
                            )
                        )
                        processedCount++
                        Log.d(
                            "StorageCollector",
                            "Added: $appName ($packageName) - $formattedAppStorageGb GB"
                        )
                    } else {
                        skippedZeroStorage++
                    }
                } catch (e: Exception) {
                    Log.e("StorageCollector", "Unexpected error processing $packageName", e)
                    continue
                }
            }

            Log.d(
                "StorageCollector",
                "Summary: processed=$processedCount, " +
                    "skippedApplicationInfo=$skippedApplicationInfo, skippedStorageStats=$skippedStorageStats, " +
                    "skippedZeroStorage=$skippedZeroStorage, totalApps=${appStorageList.size}"
            )

            // Sort by storage usage (descending)
            appStorageList.sortByDescending { it.storageGb }

            // Limit to top 50 apps
            val limitedList = appStorageList.take(50)

            // Calculate breakdown
            val appSizeGb = (totalAppBytes.toDouble() / bytesToGb).let { String.format("%.2f", it).toFloat() }
            val appDataGb = (totalDataBytes.toDouble() / bytesToGb).let { String.format("%.2f", it).toFloat() }
            val appCacheGb = (totalCacheBytes.toDouble() / bytesToGb).let { String.format("%.2f", it).toFloat() }
            val totalAppStorageGb = appSizeGb + appDataGb + appCacheGb
            val otherStorageGb = formattedUsedStorageGb - totalAppStorageGb

            val breakdown = StorageBreakdown(
                appSizeGb = appSizeGb,
                appDataGb = appDataGb,
                appCacheGb = appCacheGb,
                totalAppStorageGb = totalAppStorageGb,
                otherStorageGb = if (otherStorageGb >= 0) otherStorageGb else 0f
            )

            return StorageDetailInfo(
                totalStorageGb = formattedTotalStorageGb,
                usedStorageGb = formattedUsedStorageGb,
                freeStorageGb = formattedFreeStorageGb,
                usagePercent = usagePercent,
                appStorageList = limitedList,
                storageBreakdown = breakdown
            )
        } catch (e: Exception) {
            // If app storage collection fails, return basic info with empty breakdown
            val emptyBreakdown = StorageBreakdown(
                appSizeGb = 0f,
                appDataGb = 0f,
                appCacheGb = 0f,
                totalAppStorageGb = 0f,
                otherStorageGb = formattedUsedStorageGb
            )

            return StorageDetailInfo(
                totalStorageGb = formattedTotalStorageGb,
                usedStorageGb = formattedUsedStorageGb,
                freeStorageGb = formattedFreeStorageGb,
                usagePercent = usagePercent,
                appStorageList = emptyList(),
                storageBreakdown = emptyBreakdown
            )
        }
    }
}
