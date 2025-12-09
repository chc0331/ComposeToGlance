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
        val deviceState = DeviceStateCollector.collect(context = context)
        Log.i(TAG, "Device state : $deviceState")
        reserveWork(context)
        return Result.success()
    }

    companion object {
        private fun createRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<DeviceCareWorker>(15, TimeUnit.MINUTES)
                .addTag("device_care_worker_tag")
                .build()
        }

        fun reserveWork(context: Context) {
            val workRequest = createRequest()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DeviceCareWorker::class.java.name, ExistingPeriodicWorkPolicy.KEEP, workRequest
            )
        }
    }
}