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


fun BluetoothDeviceReceiver.register(context: Context) {
    val connectionFilter = IntentFilter().apply {
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
    }
    context.registerReceiver(
        this,
        connectionFilter
    )
}

fun BluetoothDeviceReceiver.unregister(context: Context) {
    context.unregisterReceiver(
        this
    )
}

class BluetoothDeviceReceiver : BroadcastReceiver() {

    private val TAG = "BluetoothDeviceReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (!hasBluetoothPermissions(context)) return

        val device: BluetoothDevice? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    BluetoothDevice.EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }

        Log.i(TAG, "onReceive ${intent.action} - ${device?.name}")

        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                goAsync {
                    val bluetoothDeviceManager = BluetoothDeviceManager(context)
                    bluetoothDeviceManager.findDevices { connectedDevices ->
                        Log.i(TAG, "Connected device : $connectedDevices")
                        connectedDevices.forEach { device ->
                            val batteryLevel = getDeviceBatteryLevel(device.bluetoothDevice)
                            Log.i(TAG, "${device.name} BatteryLevel : $batteryLevel")
                            // connected
                        }
                    }

                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                goAsync {
                    //disconnected
                }
            }
        }
    }

    private fun getDeviceBatteryLevel(device: BluetoothDevice): Int {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use the official API for Android 13+
                // Note: batteryLevel property is not available in all API levels
                // We'll use reflection as a fallback
                try {
                    val method = device.javaClass.getMethod("getBatteryLevel")
                    val level = method.invoke(device) as? Int ?: -1
                    if (level < 0 || level > 100) -1 else level
                } catch (e: Exception) {
                    -1
                }
            } else {
                // For older versions, use reflection
                val method = device.javaClass.getMethod("getBatteryLevel")
                val level = method.invoke(device) as? Int ?: -1
                if (level == -1 || level > 100) -1 else level
            }
        } catch (e: Exception) {
            -1
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