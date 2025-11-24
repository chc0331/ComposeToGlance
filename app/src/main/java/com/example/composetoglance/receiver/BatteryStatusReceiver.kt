package com.example.composetoglance.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log

open class BatteryStatusReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BatteryStatusReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                handleBatteryChanged(intent)
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
    fun handleBatteryIntent(intent: Intent) {
        handleBatteryChanged(intent)
    }

    private fun handleBatteryChanged(context: Context?, intent: Intent) {
        handleBatteryChanged(intent)
    }

    private fun handleBatteryChanged(intent: Intent) {
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

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val wirelessCharge = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
        } else {
            false
        }

        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthString = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }

        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000f

        val statusString = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }

        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        Log.d(TAG, "Battery Status Update:")
        Log.d(TAG, "  Level: ${batteryPct}%")
        Log.d(TAG, "  Status: $statusString")
        Log.d(TAG, "  Charging: $isCharging")
        Log.d(TAG, "  Charge Type: ${getChargeTypeString(usbCharge, acCharge, wirelessCharge)}")
        Log.d(TAG, "  Health: $healthString")
        Log.d(TAG, "  Temperature: ${temperature}°C")
        Log.d(TAG, "  Voltage: ${voltage}V")
        Log.d(TAG, "  Technology: $technology")

        // 여기에 배터리 상태 변경 시 필요한 로직 추가
        // 예: 위젯 업데이트, 알림 표시 등
        onBatteryStatusChanged(batteryPct, isCharging, statusString, healthString)
    }

    private fun getChargeTypeString(usb: Boolean, ac: Boolean, wireless: Boolean): String {
        return when {
            wireless -> "Wireless"
            ac -> "AC"
            usb -> "USB"
            else -> "None"
        }
    }

    /**
     * 배터리 상태가 변경되었을 때 호출되는 콜백
     * 필요에 따라 오버라이드하거나 인터페이스로 분리 가능
     */
    protected open fun onBatteryStatusChanged(
        batteryPct: Float,
        isCharging: Boolean,
        status: String,
        health: String
    ) {
        // 서브클래스에서 오버라이드하여 사용
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