package com.example.widget.component.battery

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Monitor Bluetooth device battery levels using the latest Android APIs
 */
class BluetoothBatteryMonitor(
    private val context: Context
) {
    companion object {
        private const val TAG = "BluetoothBatteryMonitor"
    }

    private val bluetoothManager: BluetoothManager? = 
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    
    private val _connectedDevices = MutableStateFlow<List<BatteryData>>(emptyList())
    val connectedDevices: StateFlow<List<BatteryData>> = _connectedDevices

    private var batteryReceiver: BroadcastReceiver? = null
    private var connectionReceiver: BroadcastReceiver? = null

    /**
     * Start monitoring Bluetooth device battery levels
     */
    fun startMonitoring() {
        if (!hasBluetoothPermissions()) {
            Log.w(TAG, "Missing Bluetooth permissions")
            return
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.w(TAG, "Bluetooth is not available or not enabled")
            return
        }

        registerReceivers()
        scanConnectedDevices()
        
        Log.d(TAG, "Bluetooth battery monitoring started")
    }

    /**
     * Stop monitoring and clean up
     */
    fun stopMonitoring() {
        unregisterReceivers()
        Log.d(TAG, "Bluetooth battery monitoring stopped")
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun registerReceivers() {
        // Battery level change receiver
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                }
                
                when (intent.action) {
                    // Note: ACTION_BATTERY_LEVEL_CHANGED is not a standard Android constant
                    // We'll handle battery updates through connection changes and periodic scanning
                    BluetoothDevice.ACTION_ACL_CONNECTED,
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        // Trigger a rescan when devices connect/disconnect
                        scanConnectedDevices()
                    }
                }
            }
        }

        // Connection state receiver
        connectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                }
                
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                
                when (intent.action) {
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        device?.let {
                            Log.d(TAG, "Device connected: ${it.name}")
                            scanConnectedDevices()
                        }
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        device?.let {
                            Log.d(TAG, "Device disconnected: ${it.name}")
                            removeDevice(it)
                        }
                    }
                }
            }
        }

        // Only register connection receiver since battery level changes are not standard
        val connectionFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(connectionReceiver, connectionFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(connectionReceiver, connectionFilter)
        }
    }

    private fun unregisterReceivers() {
        batteryReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering battery receiver", e)
            }
            batteryReceiver = null
        }

        connectionReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering connection receiver", e)
            }
            connectionReceiver = null
        }
    }

    /**
     * Scan for currently connected Bluetooth devices
     */
    private fun scanConnectedDevices() {
        if (!hasBluetoothPermissions()) return
        
        scope.launch {
            try {
                val connectedDevicesList = mutableListOf<BatteryData>()

                // Get bonded devices
                bluetoothAdapter?.bondedDevices?.forEach { device ->
                    // Check if device is actually connected
                    if (isDeviceConnected(device)) {
                        val batteryLevel = getDeviceBatteryLevel(device)
                        if (batteryLevel >= 0) {
                            val batteryData = createBatteryData(device, batteryLevel.toFloat())
                            connectedDevicesList.add(batteryData)
                            
                            // Update widget
                            BatteryUpdateManager.updateBatteryWidget(context, batteryData)
                        }
                    }
                }

                _connectedDevices.value = connectedDevicesList
                Log.d(TAG, "Found ${connectedDevicesList.size} connected devices with battery info")
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception while scanning devices", e)
            }
        }
    }

    private fun isDeviceConnected(device: BluetoothDevice): Boolean {
        if (!hasBluetoothPermissions()) return false
        
        return try {
            // Use reflection to check connection state (works across Android versions)
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun getDeviceBatteryLevel(device: BluetoothDevice): Int {
        if (!hasBluetoothPermissions()) return -1
        
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

    private fun updateDeviceBattery(device: BluetoothDevice, batteryLevel: Float) {
        scope.launch {
            val batteryData = createBatteryData(device, batteryLevel)
            
            // Update in state
            val currentDevices = _connectedDevices.value.toMutableList()
            val existingIndex = currentDevices.indexOfFirst { it.deviceAddress == device.address }
            if (existingIndex >= 0) {
                currentDevices[existingIndex] = batteryData
            } else {
                currentDevices.add(batteryData)
            }
            _connectedDevices.value = currentDevices

            // Update widget
            BatteryUpdateManager.updateBatteryWidget(context, batteryData)
        }
    }

    private fun removeDevice(device: BluetoothDevice) {
        scope.launch {
            val currentDevices = _connectedDevices.value.toMutableList()
            currentDevices.removeAll { it.deviceAddress == device.address }
            _connectedDevices.value = currentDevices
            
            // Remove from data store
            val repo = BatteryInfoPreferencesRepository(context.batteryDataStore)
            repo.removeBluetoothDevice(device.address)
            
            // Sync widget state
            BatteryUpdateManager.syncBatteryWidgetState(context)
        }
    }

    private fun createBatteryData(device: BluetoothDevice, batteryLevel: Float): BatteryData {
        if (!hasBluetoothPermissions()) {
            return BatteryData(
                level = batteryLevel,
                charging = false,
                deviceType = DeviceType.BLUETOOTH_UNKNOWN,
                deviceName = "Unknown",
                deviceAddress = device.address
            )
        }
        
        val deviceType = getDeviceType(device)
        val deviceName = try {
            device.name ?: "Unknown Device"
        } catch (e: SecurityException) {
            "Unknown Device"
        }

        return BatteryData(
            level = batteryLevel,
            charging = false, // Bluetooth devices don't report charging state
            deviceType = deviceType,
            deviceName = deviceName,
            deviceAddress = device.address
        )
    }

    private fun getDeviceType(device: BluetoothDevice): DeviceType {
        if (!hasBluetoothPermissions()) return DeviceType.BLUETOOTH_UNKNOWN
        
        return try {
            val bluetoothClass = device.bluetoothClass ?: return DeviceType.BLUETOOTH_UNKNOWN
            
            when (bluetoothClass.majorDeviceClass) {
                0x0400 -> { // Audio/Video
                    when (bluetoothClass.deviceClass) {
                        0x0404, 0x0408 -> DeviceType.BLUETOOTH_HEADSET
                        0x0418 -> DeviceType.BLUETOOTH_HEADPHONES
                        0x0414 -> DeviceType.BLUETOOTH_SPEAKER
                        else -> DeviceType.BLUETOOTH_UNKNOWN
                    }
                }
                0x0700 -> DeviceType.BLUETOOTH_WATCH // Wearable
                else -> DeviceType.BLUETOOTH_UNKNOWN
            }
        } catch (e: Exception) {
            DeviceType.BLUETOOTH_UNKNOWN
        }
    }
}
