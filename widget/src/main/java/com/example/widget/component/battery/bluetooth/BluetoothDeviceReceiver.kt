package com.example.widget.component.battery.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.widget.receiver.goAsync

private val TAG = "BluetoothDeviceReceiver"

fun BluetoothDeviceReceiver.register(context: Context) {
    val connectionFilter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        // 배터리 레벨 변경 액션 추가
        addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
        // 추가 배터리 관련 액션들
        addAction("android.bluetooth.BluetoothDevice.action.BATTERY_LEVEL_CHANGED")
        addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")
    }

    try {
        // 시스템 브로드캐스트(블루투스 연결/해제)를 받으려면 RECEIVER_EXPORTED 사용
        // RECEIVER_NOT_EXPORTED는 앱 내부 브로드캐스트만 받음 (시스템 브로드캐스트 차단됨)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, connectionFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(this, connectionFilter)
        }
    } catch (e: Exception) {
        Log.e(TAG, "❌ Failed to register receiver", e)
    }
}

fun BluetoothDeviceReceiver.unregister(context: Context) {
    try {
        context.unregisterReceiver(this)
    } catch (e: Exception) {
        Log.e(TAG, "❌ Failed to unregister receiver", e)
    }
}

class BluetoothDeviceReceiver : BroadcastReceiver() {

    companion object {
        // BluetoothDevice.EXTRA_BATTERY_LEVEL is not a public API constant
        private const val EXTRA_BATTERY_LEVEL = "android.bluetooth.device.extra.BATTERY_LEVEL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // 권한 체크
        val hasPermission = hasBluetoothPermissions(context)
        if (!hasPermission) {
            Log.w(TAG, "Missing Bluetooth permissions - stopping processing")
            return
        }

        val device: BluetoothDevice? = intent.getBluetoothDevice()

        Log.i(TAG, "onReceive / ${intent.action}")

        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                goAsync {
                    device?.let {
                        Log.d(TAG, "Device connected: ${it.name} (${it.address})")

                        // Intent에서 배터리 레벨 확인 (일부 기기는 연결 시 배터리 정보 포함)
                        val batteryFromIntent = intent.getBatteryLevel()
                        if (batteryFromIntent >= 0) {
                            Log.i(TAG, "${it.name} Battery from intent: $batteryFromIntent%")
                        }

                        // 모든 연결된 기기 스캔
                        val bluetoothDeviceManager = BluetoothDeviceManager(context)
                        bluetoothDeviceManager.findDevices { connectedDevices ->
                            Log.i(TAG, "Connected devices: ${connectedDevices.size}")
                            connectedDevices.forEach { connectedDevice ->
                                val bluetoothDevice = connectedDevice.bluetoothDevice
                                val batteryLevel = bluetoothDevice.getDeviceBatteryLevel()
                                val deviceName = connectedDevice.name
                                val deviceType = bluetoothDevice.getDeviceType()
                                Log.i(
                                    TAG,
                                    "Connected device : $deviceName $batteryLevel $deviceType"
                                )

                                if (batteryLevel >= 0) {
                                    // TODO: 배터리 정보를 위젯에 업데이트
                                    Log.d(
                                        TAG,
                                        "Valid battery level found for ${connectedDevice.name}"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                goAsync {
                    device?.let {
                        Log.d(TAG, "Device disconnected: ${it.name} (${it.address})")
                        // TODO: 위젯에서 해당 기기 제거
                    }
                }
            }

            "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED",
            "android.bluetooth.BluetoothDevice.action.BATTERY_LEVEL_CHANGED" -> {
                goAsync {
                    device?.let {
                        val batteryLevel = intent.getBatteryLevel()
                        Log.i(TAG, "Battery level changed for ${it.name}: $batteryLevel%" +
                                "" +
                                "${it.getDeviceType()}")

                        if (batteryLevel >= 0) {
                            // TODO: 배터리 정보를 위젯에 업데이트
                        }
                    }
                }
            }

            "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED" -> {
                goAsync {
                    device?.let {
                        Log.d(TAG, "Headset connection state changed: ${it.name}")
                        // 헤드셋 연결 상태 변경 시 배터리 정보 다시 확인
                        val batteryLevel = it.getDeviceBatteryLevel()
                        if (batteryLevel >= 0) {
                            Log.i(TAG, "${it.name} Battery level: $batteryLevel%")
                        }
                    }
                }
            }
        }
    }

    private fun hasBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
