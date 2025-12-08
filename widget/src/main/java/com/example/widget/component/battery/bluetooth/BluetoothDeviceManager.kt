package com.example.widget.component.battery.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.widget.component.battery.DeviceType

data class ConnectedDevice(
    val name: String,
    val address: String, // 대표 주소 (주로 Classic 주소 사용)
    val types: MutableList<String> = mutableListOf(), // [Classic, BLE] 등 연결 타입 저장
    val bluetoothDevice: BluetoothDevice,
    val deviceType: DeviceType // 디바이스 타입 (이어폰, 헤드폰, 워치 등)
)

class BluetoothDeviceManager(private val context: Context) {

    private val TAG = "BluetoothDeviceManager"
    private val mainHandler = Handler(Looper.getMainLooper())
    private val PROFILE_TIMEOUT_MS = 3000L // 3초 타임아웃

    private var callbackInvoked = false

    fun findDevices(callback: (List<ConnectedDevice>) -> Unit) {
        // 권한 체크
        if (!hasBluetoothPermissions()) {
            Log.w(TAG, "Missing Bluetooth permissions")
            callback(emptyList())
            return
        }

        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null) {
            Log.w(TAG, "BluetoothAdapter is null")
            callback(emptyList())
            return
        }

        if (!adapter.isEnabled) {
            Log.w(TAG, "Bluetooth is not enabled")
            callback(emptyList())
            return
        }

        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if (manager == null) {
            Log.w(TAG, "BluetoothManager is null")
            callback(emptyList())
            return
        }

        scanDevicesInternal(adapter, manager, callback)
    }

    @SuppressLint("MissingPermission")
    private fun scanDevicesInternal(
        adapter: BluetoothAdapter,
        manager: BluetoothManager,
        callback: (List<ConnectedDevice>) -> Unit
    ) {
        val rawList = mutableListOf<BluetoothDevice>()
        callbackInvoked = false

        // 로드할 프로파일 목록 결정
        val profilesToLoad = mutableListOf(
            BluetoothProfile.A2DP,
            BluetoothProfile.HEADSET
        )

        // Android 11 이상에서는 LE_AUDIO도 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            profilesToLoad.add(BluetoothProfile.LE_AUDIO)
        }

        try {
            // 1. GATT (BLE) 기기 가져오기
            val gattDevices = manager.getConnectedDevices(BluetoothProfile.GATT)
            Log.i(TAG, "GATT devices: ${gattDevices.size}")
            rawList.addAll(gattDevices)

            // 2. 본드된(페어링된) 기기도 확인 - 연결된 기기 중에 포함될 수 있음
            val bondedDevices = adapter.bondedDevices?.filter { device ->
                isDeviceConnected(device)
            } ?: emptyList()
            Log.i(TAG, "Bonded and connected devices: ${bondedDevices.size}")
            rawList.addAll(bondedDevices)

            // 3. 타임아웃 설정 - 프로파일 로딩이 실패할 경우를 대비
            mainHandler.postDelayed({
                if (!callbackInvoked) {
                    Log.w(TAG, "Profile loading timeout, returning current devices")
                    invokeCallback(callback, mergeDevices(rawList))
                }
            }, PROFILE_TIMEOUT_MS)

            // 4. 각 프로파일에서 연결된 기기 가져오기
            loadProfileDevices(adapter, profilesToLoad, 0, rawList, callback)
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning devices", e)
            invokeCallback(callback, emptyList())
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadProfileDevices(
        adapter: BluetoothAdapter,
        profiles: List<Int>,
        currentIndex: Int,
        rawList: MutableList<BluetoothDevice>,
        callback: (List<ConnectedDevice>) -> Unit
    ) {
        if (currentIndex >= profiles.size) {
            // 모든 프로파일 로딩 완료
            Log.i(TAG, "All profiles loaded. Total devices: ${rawList.size}")
            invokeCallback(callback, mergeDevices(rawList))
            return
        }

        val profileType = profiles[currentIndex]
        val profileName = getProfileName(profileType)

        try {
            val success =
                adapter.getProfileProxy(
                    context,
                    object : BluetoothProfile.ServiceListener {
                        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                            try {
                                val devices = proxy.connectedDevices ?: emptyList()
                                Log.i(TAG, "$profileName connected devices: ${devices.size}")
                                rawList.addAll(devices)
                                adapter.closeProfileProxy(profile, proxy)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error getting $profileName devices", e)
                            } finally {
                                // 다음 프로파일 로드
                                loadProfileDevices(
                                    adapter,
                                    profiles,
                                    currentIndex + 1,
                                    rawList,
                                    callback
                                )
                            }
                        }

                        override fun onServiceDisconnected(profile: Int) {
                            Log.d(TAG, "$profileName service disconnected")
                        }
                    },
                    profileType
                )

            if (!success) {
                Log.w(TAG, "Failed to get $profileName profile proxy")
                // 실패해도 다음 프로파일 진행
                loadProfileDevices(adapter, profiles, currentIndex + 1, rawList, callback)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading $profileName profile", e)
            // 예외 발생해도 다음 프로파일 진행
            loadProfileDevices(adapter, profiles, currentIndex + 1, rawList, callback)
        }
    }

    private fun invokeCallback(
        callback: (List<ConnectedDevice>) -> Unit,
        devices: List<ConnectedDevice>
    ) {
        if (!callbackInvoked) {
            callbackInvoked = true
            mainHandler.removeCallbacksAndMessages(null) // 타임아웃 취소
            callback(devices)
            Log.i(TAG, "Callback invoked with ${devices.size} devices")
        }
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

    @SuppressLint("MissingPermission")
    private fun isDeviceConnected(device: BluetoothDevice): Boolean {
        if (!hasBluetoothPermissions()) return false

        return try {
            // 리플렉션을 사용하여 연결 상태 확인
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as? Boolean ?: false
        } catch (e: Exception) {
            Log.d(TAG, "Unable to check connection state for ${device.name}", e)
            false
        }
    }

    private fun getProfileName(profile: Int): String {
        return when (profile) {
            BluetoothProfile.A2DP -> "A2DP"
            BluetoothProfile.HEADSET -> "HEADSET"
            BluetoothProfile.GATT -> "GATT"
            BluetoothProfile.GATT_SERVER -> "GATT_SERVER"
            33 -> "LE_AUDIO" // BluetoothProfile.LE_AUDIO constant value
            else -> "Profile($profile)"
        }
    }

    @SuppressLint("MissingPermission")
    private fun mergeDevices(rawList: List<BluetoothDevice>): List<ConnectedDevice> {
        val mergedList = mutableListOf<ConnectedDevice>()
        val processedAddresses = mutableSetOf<String>()

        Log.d(TAG, "Merging ${rawList.size} devices")

        for (device in rawList) {
            try {
                val deviceName = device.name ?: "Unknown"
                val deviceAddress = device.address

                // 이미 처리한 주소는 스킵 (중복 제거)
                if (processedAddresses.contains(deviceAddress)) {
                    Log.d(TAG, "Skipping duplicate device: $deviceName ($deviceAddress)")
                    continue
                }

                val deviceType = when (device.type) {
                    BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Audio(Classic)"
                    BluetoothDevice.DEVICE_TYPE_LE -> "Control(LE)"
                    BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual"
                    else -> "Unknown"
                }

                Log.d(TAG, "Processing device: $deviceName ($deviceAddress) - Type: $deviceType")

                // 1. 기존 리스트에서 '같은 기기'라고 판단될만한 녀석이 있는지 찾기
                val existing = mergedList.find { savedDevice ->
                    isSamePhysicalDevice(savedDevice, device)
                }

                if (existing != null) {
                    // 2. 이미 존재하면 -> 타입 정보 추가
                    if (!existing.types.contains(deviceType)) {
                        existing.types.add(deviceType)
                        Log.d(TAG, "Added type $deviceType to existing device: $deviceName")
                    }

                    // ★ 중요: 현재 들어온 기기가 오디오(Dual/Classic)라면 메인 주소를 이걸로 교체
                    // (BLE 주소보다는 오디오 주소가 나중에 연결 끊거나 제어할 때 더 중요함)
                    if (device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC ||
                        device.type == BluetoothDevice.DEVICE_TYPE_DUAL
                    ) {
                        // 기존 디바이스를 업데이트된 것으로 교체
                        val index = mergedList.indexOf(existing)
                        if (index >= 0) {
                            val detectedDeviceType = device.getDeviceType()
                            mergedList[index] = ConnectedDevice(
                                name = deviceName,
                                address = deviceAddress,
                                types = existing.types,
                                bluetoothDevice = device,
                                deviceType = detectedDeviceType
                            )
                            Log.d(
                                TAG,
                                "Updated device with Classic/Dual address: $deviceName, Type: $detectedDeviceType"
                            )
                        }
                    }
                } else {
                    // 3. 없으면 -> 새로 추가
                    val detectedDeviceType = device.getDeviceType()
                    mergedList.add(
                        ConnectedDevice(
                            name = deviceName,
                            address = deviceAddress,
                            types = mutableListOf(deviceType),
                            bluetoothDevice = device,
                            deviceType = detectedDeviceType
                        )
                    )
                    Log.d(
                        TAG,
                        "Added new device: $deviceName ($deviceAddress), Type: $detectedDeviceType"
                    )
                }

                processedAddresses.add(deviceAddress)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing device", e)
            }
        }

        Log.i(TAG, "Merged result: ${mergedList.size} unique devices")
        mergedList.forEach { device ->
            Log.d(
                TAG,
                "  - ${device.name} (${device.address}): ${device.types.joinToString()}, Type: ${device.deviceType}"
            )
        }

        return mergedList
    }

    @SuppressLint("MissingPermission")
    private fun isSamePhysicalDevice(saved: ConnectedDevice, target: BluetoothDevice): Boolean {
        try {
            val targetName = target.name ?: "Unknown"

            // 1. 주소가 정확히 같으면 같은 기기
            if (saved.address == target.address) {
                return true
            }

            // 2. 이름이 다르면 아예 다른 기기
            if (saved.name != targetName) {
                return false
            }

            // 3. 이름이 같다면, 주소의 앞부분(Prefix) 비교
            // MAC 주소 포맷: AA:BB:CC:DD:EE:FF (총 17자)
            // 앞 14~15자 (AA:BB:CC:DD:EE)가 같으면 같은 칩셋으로 간주

            // 안전 장치: 주소 길이가 짧으면 그냥 이름만 같아도 같다고 판단
            if (saved.address.length < 15 || target.address.length < 15) {
                Log.d(TAG, "Address too short, using name match: ${saved.name}")
                return true
            }

            val savedPrefix = saved.address.substring(0, 15) // 앞 5바이트
            val targetPrefix = target.address.substring(0, 15) // 앞 5바이트

            val isSame = savedPrefix == targetPrefix
            if (isSame) {
                Log.d(TAG, "Same physical device detected by prefix: ${saved.name}")
            }

            return isSame
        } catch (e: Exception) {
            Log.e(TAG, "Error comparing devices", e)
            return false
        }
    }
}
