package com.widgetkit.core.component.battery.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.widgetkit.core.component.battery.BatteryData
import com.widgetkit.core.receiver.goAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val TAG = "BluetoothDeviceReceiver"

@SuppressLint("UnspecifiedRegisterReceiverFlag")
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
        Log.e(TAG, ":x: Failed to register receiver", e)
    }
}

fun BluetoothDeviceReceiver.unregister(context: Context) {
    try {
        context.unregisterReceiver(this)
    } catch (e: Exception) {
        Log.e(TAG, ":x: Failed to unregister receiver", e)
    }
}

class BluetoothDeviceReceiver : BroadcastReceiver() {

    companion object {
        // BluetoothDevice.EXTRA_BATTERY_LEVEL is not a public API constant
        private const val EXTRA_BATTERY_LEVEL = "android.bluetooth.device.extra.BATTERY_LEVEL"
    }

    @SuppressLint("MissingPermission")
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

                        // 모든 연결된 기기 스캔하여 위젯 업데이트
                        val bluetoothDeviceManager = BluetoothDeviceManager(context)
                        bluetoothDeviceManager.findDevices { connectedDevices ->
                            Log.i(TAG, "Connected devices: ${connectedDevices.size}")

                            // 블루투스 디바이스 정보 리스트 생성
                            val deviceInfoList = connectedDevices.mapNotNull { connectedDevice ->
                                val bluetoothDevice = connectedDevice.bluetoothDevice
                                val batteryLevel = bluetoothDevice.getDeviceBatteryLevel()
                                val deviceName = connectedDevice.name
                                val deviceType = bluetoothDevice.getDeviceType()

                                Log.i(
                                    TAG,
                                    "Connected device: $deviceName, Battery: $batteryLevel%, Type: $deviceType"
                                )

                                // 정상적인 배터리 값(>= 0)이 들어왔을 때만 업데이트
                                if (batteryLevel >= 0) {
                                    BatteryData(
                                        level = batteryLevel.toFloat(),
                                        charging = false,
                                        deviceType = deviceType,
                                        deviceName = deviceName,
                                        deviceAddress = connectedDevice.address,
                                        isConnect = true
                                    )
                                } else {
                                    Log.d(
                                        TAG,
                                        "Skipping update for $deviceName: invalid battery level ($batteryLevel)"
                                    )
                                    null
                                }
                            }

                            // 위젯 업데이트
                            CoroutineScope(Dispatchers.Default).launch {
                                deviceInfoList.forEach {
                                    BluetoothBatteryUpdateManager.updateComponent(context, it)
                                }
                            }
                        }
                    }
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                goAsync {
                    device?.let {
                        val deviceName = it.name
                        val deviceType = it.getDeviceType()
                        val batteryData =
                            BatteryData(0f, false, deviceType, deviceName, isConnect = false)
                        // 위젯 업데이트
                        CoroutineScope(Dispatchers.Default).launch {
                            BluetoothBatteryUpdateManager.updateComponent(
                                context,
                                batteryData
                            )
                        }
                    }
                }
            }

            "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED",
            "android.bluetooth.BluetoothDevice.action.BATTERY_LEVEL_CHANGED" -> {
                goAsync {
                    device?.let {
                        val batteryLevel = intent.getBatteryLevel()
                        val deviceType = it.getDeviceType()

                        Log.i(
                            "heec.choi",
                            "Battery level changed for ${it.name}: $batteryLevel%, Type: $deviceType"
                        )

                        if (batteryLevel >= 0) {
                            // 배터리 정보를 위젯에 업데이트
                            val data = BatteryData(
                                level = batteryLevel.toFloat(),
                                charging = false,
                                deviceType = deviceType,
                                deviceName = it.name,
                                deviceAddress = it.address,
                                isConnect = true
                            )

                            BluetoothBatteryUpdateManager.updateComponent(
                                context,
                                data
                            )
                        }
                    }
                }
            }

            "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED" -> {
                goAsync {
                    device?.let {
                        Log.d(TAG, "Headset connection state changed: ${it.name}")

                        // 헤드셋 연결 상태 변경 시 배터리 정보 다시 확인하고 업데이트
                        val batteryLevel = it.getDeviceBatteryLevel()
                        val deviceType = it.getDeviceType()

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
