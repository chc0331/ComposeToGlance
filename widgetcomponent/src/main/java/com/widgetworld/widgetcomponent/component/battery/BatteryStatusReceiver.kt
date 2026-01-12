package com.widgetworld.widgetcomponent.component.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import com.widgetworld.widgetcomponent.receiver.goAsync

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

            Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY,
            Intent.ACTION_POWER_CONNECTED, Intent.ACTION_POWER_DISCONNECTED -> {
                Log.w(TAG, "Other intent action : ${intent.action}")
            }
        }
    }

    private fun handleBatteryChanged(context: Context, intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPercent = if (scale > 0) {
            (level * 100 / scale.toFloat())
        } else {
            -1f
        }
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val batteryData = BatteryData(batteryPercent, isCharging).apply {
            Log.d(TAG, "Battery Status Update: $this")
        }
        onBatteryStatusChanged(
            context,
            batteryData
        )
    }

    protected open fun onBatteryStatusChanged(context: Context, batteryData: BatteryData) {
        goAsync {
            BatteryUpdateManager.updateByState(context, null, batteryData)
        }
    }
}
