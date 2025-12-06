package com.example.composetoglance.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.composetoglance.R
import com.example.widget.component.battery.BatteryStatusReceiver
import com.example.widget.component.battery.bluetooth.BluetoothDeviceReceiver
import com.example.widget.component.battery.bluetooth.register
import com.example.widget.component.battery.bluetooth.unregister

class WidgetForegroundService : Service() {
    companion object {
        private const val TAG = "WidgetForegroundService"
        const val CHANNEL_ID = "widget_foreground_channel"
        const val NOTIFICATION_ID = 1001
    }

    private var batteryReceiver: BatteryStatusReceiver? = null
    private var bluetoothReceiver:BluetoothDeviceReceiver?=null
    private val handler = Handler(Looper.getMainLooper())
    private var batteryCheckRunnable: Runnable? = null
    private var lastBatteryLevel = -1

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate called")
        createNotificationChannel()
        registerBatteryReceiver()

        bluetoothReceiver = BluetoothDeviceReceiver()
        bluetoothReceiver?.register(this)
//        startBatteryMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy called")
        stopBatteryMonitoring()
        unregisterBatteryReceiver()
        bluetoothReceiver?.unregister(this)
        bluetoothReceiver = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand called")
        try {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Foreground service started successfully")
            // 여기에 살아있게 하고 싶은 코드(예: 백그라운드 타이머, 주기적 작업 등)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service", e)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ComposeToGlance 위젯 서비스 활성화 중")
            .setContentText("항상 활성화되어 사용 가능합니다.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // 기존 채널이 있으면 삭제 (중요도 변경을 위해)
            try {
                manager.deleteNotificationChannel(CHANNEL_ID)
                Log.d(TAG, "Existing notification channel deleted")
            } catch (e: Exception) {
                Log.d(TAG, "No existing channel to delete")
            }

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Widget Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "ComposeToGlance 위젯 서비스를 위한 알림 채널"
            channel.setShowBadge(false)
            manager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created with IMPORTANCE_DEFAULT")
        }
    }

    private fun registerBatteryReceiver() {
        batteryReceiver = BatteryStatusReceiver()
        val filter = IntentFilter().apply {
            // ACTION_BATTERY_CHANGED는 sticky broadcast이므로 registerReceiver로 등록 불가
            // 대신 주기적으로 확인하거나 다른 액션들을 등록
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(batteryReceiver, filter)
        }
        Log.d(TAG, "Battery receiver registered")
    }

    private fun unregisterBatteryReceiver() {
        batteryReceiver?.let {
            try {
                unregisterReceiver(it)
                Log.d(TAG, "Battery receiver unregistered")
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering battery receiver", e)
            }
            batteryReceiver = null
        }
    }

    private fun startBatteryMonitoring() {
        // ACTION_BATTERY_CHANGED는 sticky broadcast이므로 주기적으로 확인
        batteryCheckRunnable = object : Runnable {
            override fun run() {
                checkBatteryStatus()
                handler.postDelayed(this, 30000) // 30초마다 확인
            }
        }
        handler.post(batteryCheckRunnable!!)
        Log.d(TAG, "Battery monitoring started")
    }

    private fun stopBatteryMonitoring() {
        batteryCheckRunnable?.let {
            handler.removeCallbacks(it)
            batteryCheckRunnable = null
        }
        Log.d(TAG, "Battery monitoring stopped")
    }

    private fun checkBatteryStatus() {
        val batteryStatusIntent = registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        batteryStatusIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = if (scale > 0) {
                (level * 100 / scale.toFloat())
            } else {
                -1f
            }

            // 배터리 레벨이 변경된 경우에만 처리
            if (batteryPct.toInt() != lastBatteryLevel) {
                lastBatteryLevel = batteryPct.toInt()
//                batteryReceiver?.handleBatteryIntent(intent)
            }
        }
    }
}