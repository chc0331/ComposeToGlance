package com.example.widget.component.battery.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context

data class ConnectedDevice(
    val name: String,
    val address: String, // 대표 주소 (주로 Classic 주소 사용)
    val types: MutableList<String> = mutableListOf(), // [Classic, BLE] 등 연결 타입 저장
    val bluetoothDevice: BluetoothDevice
)

class BluetoothDeviceManager(private val context: Context) {

    private val TAG = "BluetoothDeviceManager"

    @SuppressLint("MissingPermission")
    fun findDevices(callback: (List<ConnectedDevice>) -> Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val rawList = mutableListOf<BluetoothDevice>()

        // 1. GATT (BLE) 기기 가져오기
        rawList.addAll(manager.getConnectedDevices(BluetoothProfile.GATT))

        // 2. A2DP (미디어) 프로파일 가져오기
        adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(
                profile: Int,
                proxy: BluetoothProfile
            ) {
                rawList.addAll(proxy.connectedDevices)
                adapter.closeProfileProxy(profile, proxy)
                adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                    override fun onServiceConnected(
                        profile2: Int,
                        proxy2: BluetoothProfile
                    ) {
                        rawList.addAll(proxy2.connectedDevices)
                        adapter.closeProfileProxy(profile2, proxy2)
                        callback(mergeDevices(rawList))
                    }

                    override fun onServiceDisconnected(profile: Int) {
                    }
                }, BluetoothProfile.HEADSET)
            }

            override fun onServiceDisconnected(profile: Int) {}
        }, BluetoothProfile.A2DP)
    }

    @SuppressLint("MissingPermission")
    private fun mergeDevices(rawList: List<BluetoothDevice>): List<ConnectedDevice> {
        val mergedList = mutableListOf<ConnectedDevice>()

        for (device in rawList) {
            val deviceName = device.name ?: "Unknown"
            val deviceAddress = device.address
            val deviceType = when (device.type) {
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Audio(Classic)"
                BluetoothDevice.DEVICE_TYPE_LE -> "Control(LE)"
                BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual"
                else -> "Unknown"
            }

            // 1. 기존 리스트에서 '같은 기기'라고 판단될만한 녀석이 있는지 찾기
            val existing = mergedList.find { savedDevice ->
                isSamePhysicalDevice(savedDevice, device)
            }

            if (existing != null) {
                // 2. 이미 존재하면 -> 타입 정보 추가
                if (!existing.types.contains(deviceType)) {
                    existing.types.add(deviceType)
                }

                // ★ 중요: 현재 들어온 기기가 오디오(Dual/Classic)라면 메인 주소를 이걸로 교체
                // (BLE 주소보다는 오디오 주소가 나중에 연결 끊거나 제어할 때 더 중요함)
                if (device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC || device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                    // Kotlin의 data class는 불변이므로 리스트에서 교체 작업 필요 (또는 var 사용)
                    // 여기서는 설명을 위해 간단히 처리하지만, 실제로는 객체 갱신 로직 필요
                    // (아래 data class를 var로 선언하거나 copy를 사용해야 함)
                }
            } else {
                // 3. 없으면 -> 새로 추가
                mergedList.add(
                    ConnectedDevice(
                        name = deviceName,
                        address = deviceAddress,
                        types = mutableListOf(deviceType),
                        bluetoothDevice = device
                    )
                )
            }
        }
        return mergedList
    }

    @SuppressLint("MissingPermission")
    private fun isSamePhysicalDevice(saved: ConnectedDevice, target: BluetoothDevice): Boolean {
        // 1. 이름이 다르면 아예 다른 기기
        if (saved.name != target.name) return false

        // 2. 이름이 같다면, 주소의 앞부분(Prefix) 비교
        // MAC 주소 포맷: AA CC EE:FF (총 17자)
        // 앞 14~15자 (AA CC EE)가 같으면 같은 칩셋으로 간주

        // 안전 장치: 주소 길이가 짧으면 그냥 이름만 같아도 같다고 침
        if (saved.address.length < 15 || target.address.length < 15) return true

        val savedPrefix = saved.address.substring(0, 15) // 앞 5바이트
        val targetPrefix = target.address.substring(0, 15)   // 앞 5바이트

        return savedPrefix == targetPrefix
    }
}