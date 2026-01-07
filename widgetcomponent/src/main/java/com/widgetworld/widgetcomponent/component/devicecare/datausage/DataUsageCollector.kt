package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import java.util.Calendar

object DataUsageCollector {
    private const val TAG = "DataUsageCollector"

    /**
     * 이번 달 1일 0시부터 현재까지의 데이터 사용량을 수집합니다.
     * 모바일 데이터와 WiFi 데이터를 합산합니다.
     * 
     * @param context Context
     * @return DataUsageData (권한이 없거나 오류 발생 시 기본값 반환)
     */
    fun collect(context: Context): DataUsageData {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "NetworkStatsManager requires API 23+")
            return DataUsageData.create(
                currentUsageBytes = 0L,
                dataLimitBytes = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
            )
        }

        return try {
            val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE)
                as? NetworkStatsManager
                ?: run {
                    Log.e(TAG, "NetworkStatsManager service not available")
                    return DataUsageData.create(
                        currentUsageBytes = 0L,
                        dataLimitBytes = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
                    )
                }

            // 이번 달 1일 0시 0분 0초 계산
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            // 모바일 데이터 사용량
            val mobileBytes = getNetworkUsageBytes(
                networkStatsManager,
                ConnectivityManager.TYPE_MOBILE,
                startTime,
                endTime
            )

            // WiFi 데이터 사용량
            val wifiBytes = getNetworkUsageBytes(
                networkStatsManager,
                ConnectivityManager.TYPE_WIFI,
                startTime,
                endTime
            )

            val totalBytes = mobileBytes + wifiBytes

            Log.d(TAG, "Data usage collected: Mobile=$mobileBytes, WiFi=$wifiBytes, Total=$totalBytes")

            // DataStore에서 제한량 로드
            val dataLimitBytes = try {
                val dataStore = DataUsageDataStore
                val currentData = kotlinx.coroutines.runBlocking {
                    dataStore.loadData(context)
                }
                currentData.dataLimitBytes
            } catch (e: Exception) {
                Log.w(TAG, "Failed to load data limit, using default", e)
                DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
            }

            DataUsageData.create(
                currentUsageBytes = totalBytes,
                dataLimitBytes = dataLimitBytes
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "PACKAGE_USAGE_STATS permission not granted", e)
            DataUsageData.create(
                currentUsageBytes = 0L,
                dataLimitBytes = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to collect data usage", e)
            DataUsageData.create(
                currentUsageBytes = 0L,
                dataLimitBytes = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
            )
        }
    }

    /**
     * 특정 네트워크 타입의 데이터 사용량을 조회합니다.
     */
    private fun getNetworkUsageBytes(
        networkStatsManager: NetworkStatsManager,
        networkType: Int,
        startTime: Long,
        endTime: Long
    ): Long {
        return try {
            val bucket: NetworkStats.Bucket? = networkStatsManager.querySummaryForDevice(
                networkType,
                null, // subscriberId (null for device-wide stats)
                startTime,
                endTime
            )

            val rxBytes = bucket?.rxBytes ?: 0L
            val txBytes = bucket?.txBytes ?: 0L
            rxBytes + txBytes
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException for network type $networkType", e)
            0L
        } catch (e: Exception) {
            Log.e(TAG, "Error querying network stats for type $networkType", e)
            0L
        }
    }
}

