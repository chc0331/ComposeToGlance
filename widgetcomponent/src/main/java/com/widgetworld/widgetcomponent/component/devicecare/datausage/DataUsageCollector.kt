package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import java.util.Calendar

object DataUsageCollector {
    private const val TAG = "DataUsageCollector"

    /**
     * 이번 달 1일 0시부터 현재까지의 데이터 사용량을 수집합니다.
     * 모바일 데이터와 WiFi 데이터를 별도로 수집합니다.
     *
     * @param context Context
     * @return DataUsageData (권한이 없거나 오류 발생 시 기본값 반환)
     */
    suspend fun collect(context: Context): DataUsageData {
        return try {
            val networkStatsManager =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            // 이번 달 1일 0시 0분 0초 계산
            val calendar = getStartDayAtCurrentMonth()
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            // 모바일 데이터 사용량
            val mobileBytes = getNetworkUsageBytes(
                networkStatsManager, NetworkCapabilities.TRANSPORT_CELLULAR,
                startTime,
                endTime
            )

            // WiFi 데이터 사용량
            val wifiBytes = getNetworkUsageBytes(
                networkStatsManager, NetworkCapabilities.TRANSPORT_WIFI,
                startTime,
                endTime
            )
            val currentLimitData = DataUsageDataStore.loadData(context)

            DataUsageData.create(
                wifiUsageBytes = wifiBytes,
                wifiLimitBytes = currentLimitData.wifiLimitBytes,
                mobileUsageBytes = mobileBytes,
                mobileLimitBytes = currentLimitData.mobileLimitBytes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to collect data usage", e)
            val defaultLimit = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
            DataUsageData.create(
                wifiUsageBytes = 0L,
                wifiLimitBytes = defaultLimit,
                mobileUsageBytes = 0L,
                mobileLimitBytes = defaultLimit
            )
        }
    }

    private fun getNetworkUsageBytes(
        networkStatsManager: NetworkStatsManager,
        networkType: Int,
        startTime: Long,
        endTime: Long
    ): Long {
        return try {
            //기기 전체 네트워크 사용량을 조회
            val bucket: NetworkStats.Bucket = networkStatsManager.querySummaryForDevice(
                networkType,
                null, // all
                startTime,
                endTime
            )

            val rxBytes = bucket.rxBytes // 받은 데이터(ex:웹페이지 로딩,이미지/영상 스트리밍 등)
            val txBytes = bucket.txBytes // 보낸 데이터(ex:파일 업로드,사진/영상 전송 등)
            rxBytes + txBytes
        } catch (e: Exception) {
            Log.e(TAG, "Error querying network stats for type $networkType", e)
            0L
        }
    }

    private fun getStartDayAtCurrentMonth(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}

