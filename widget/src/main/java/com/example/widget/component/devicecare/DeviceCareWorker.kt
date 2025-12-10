package com.example.widget.component.devicecare

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class DeviceCareWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val TAG = "DeviceCareWorker"
    override suspend fun doWork(): Result {
        return try {
            val deviceState = DeviceStateCollector.collect(context = context)
            Log.i(TAG, "Device state : $deviceState")
            
            // 새로운 ComponentDataStore 사용
            DeviceCareComponentDataStore.saveData(context, deviceState)
            
            // UpdateManager를 통해 위젯 업데이트
            DeviceCareUpdateManager.updateComponent(context, deviceState)
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to collect device state", e)
            Result.failure()
        }
    }

}