package com.widgetkit.core.component.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.widgetkit.core.receiver.goAsync

open class BatteryStatusReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BatteryStatusReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive / ${intent.action}")
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                handleBatteryChanged(context, intent)
            }

            Intent.ACTION_BATTERY_LOW -> {
                Log.w(TAG, "Battery is low")
                onBatteryLow()
            }

            Intent.ACTION_BATTERY_OKAY -> {
                Log.d(TAG, "Battery is okay")
                onBatteryOkay()
            }

            Intent.ACTION_POWER_CONNECTED -> {
                Log.d(TAG, "Power connected")
                onPowerConnected()
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d(TAG, "Power disconnected")
                onPowerDisconnected()
            }
        }
    }

    /**
     * 배터리 상태 변경을 직접 처리하는 메서드
     * ACTION_BATTERY_CHANGED는 sticky broadcast이므로 직접 호출 가능
     */
    fun handleBatteryIntent(context: Context, intent: Intent) {
        handleBatteryChanged(context, intent)
    }

    private fun handleBatteryChanged(context: Context, intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (scale > 0) {
            (level * 100 / scale.toFloat())
        } else {
            -1f
        }
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        val batteryData = BatteryData(
            batteryPct,
            isCharging
        ).apply {
            Log.d(TAG, "Battery Status Update:")
            Log.d(TAG, " Data: $this")
        }
        // 여기에 배터리 상태 변경 시 필요한 로직 추가
        // 예: 위젯 업데이트, 알림 표시 등
        onBatteryStatusChanged(
            context,
            batteryData
        )
    }

    /**
     * 배터리 상태가 변경되었을 때 호출되는 콜백
     * 필요에 따라 오버라이드하거나 인터페이스로 분리 가능
     */
    protected open fun onBatteryStatusChanged(context: Context, batteryData: BatteryData) {
        goAsync {
            BatteryUpdateManager.updateComponent(
                context,
                data = batteryData
            )
        }
    }

    protected open fun onBatteryLow() {
        // 배터리가 낮을 때 처리
    }

    protected open fun onBatteryOkay() {
        // 배터리가 정상으로 돌아왔을 때 처리
    }

    protected open fun onPowerConnected() {
        // 전원이 연결되었을 때 처리
    }

    protected open fun onPowerDisconnected() {
        // 전원이 분리되었을 때 처리
    }
}
