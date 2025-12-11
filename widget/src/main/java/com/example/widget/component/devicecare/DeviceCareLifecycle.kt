package com.example.widget.component.devicecare

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.widget.component.lifecycle.ComponentLifecycle
import java.util.concurrent.TimeUnit

/**
 * DeviceCare 컴포넌트의 생명주기를 관리합니다.
 * 
 * WorkManager를 사용하여 주기적으로 디바이스 상태를 수집합니다.
 */
object DeviceCareLifecycle : ComponentLifecycle {
    
    private const val TAG = "DeviceCareLifecycle"
    private const val WORK_NAME = "device_care_worker"
    private var registered = false
    
    override fun register(context: Context) {
        if (registered) {
            Log.w(TAG, "DeviceCare worker already registered")
            return
        }
        
        try {
            val workRequest = PeriodicWorkRequestBuilder<DeviceCareWorker>(
                15, TimeUnit.MINUTES
            ).addTag("device_care_worker_tag")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            
            registered = true
            Log.i(TAG, "DeviceCare worker registered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register DeviceCare worker", e)
            throw e
        }
    }
    
    override fun unregister(context: Context) {
        if (!registered) {
            Log.w(TAG, "DeviceCare worker not registered")
            return
        }
        
        try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            registered = false
            Log.i(TAG, "DeviceCare worker unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister DeviceCare worker", e)
        }
    }
    
    override fun isRegistered(): Boolean = registered
}


