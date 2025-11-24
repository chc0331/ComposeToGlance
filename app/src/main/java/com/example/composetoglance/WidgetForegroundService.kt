package com.example.composetoglance

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class WidgetForegroundService : Service() {
    companion object {
        private const val TAG = "WidgetForegroundService"
        const val CHANNEL_ID = "widget_foreground_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate called")
        createNotificationChannel()
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
}
