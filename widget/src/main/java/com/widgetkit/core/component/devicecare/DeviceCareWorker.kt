package com.widgetkit.core.component.devicecare

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.widgetkit.core.component.devicecare.ram.RamData
import com.widgetkit.core.component.devicecare.ram.RamUpdateManager
import com.widgetkit.core.component.devicecare.ram.RamWidgetDataStore
import java.util.concurrent.TimeUnit

class DeviceCareWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val deviceState = DeviceStateCollector.collect(context = context)
            Log.i(TAG, "Device state : $deviceState")

            val ramUsagePercent =
                String.format("%.1f", (deviceState.memoryUsage * 100) / deviceState.totalMemory)
                    .toFloat()
            val ramData = RamData(ramUsagePercent)
            RamWidgetDataStore.saveData(context, ramData)
            RamUpdateManager.updateByPartially(context, ramData)

            // DeviceCare위젯 컴포넌트가 추가되어 있으면 registe
            registerWorker(context)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to collect device state", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "DeviceCareWorker"
        private const val WORK_NAME = "device_care_worker"

        fun buildRequest() = PeriodicWorkRequestBuilder<DeviceCareWorker>(
            15,
            TimeUnit.MINUTES
        ).addTag("device_care_worker_tag").build()

        fun registerWorker(context: Context) {
            val workRequest = buildRequest()
            Log.i(TAG, "registerWorker - ${System.currentTimeMillis()}")
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
