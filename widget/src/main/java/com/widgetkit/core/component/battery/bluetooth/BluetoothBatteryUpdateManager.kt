package com.widgetkit.core.component.battery.bluetooth

import android.content.Context
import com.widgetkit.core.component.battery.BatteryData
import com.widgetkit.core.component.battery.DeviceType
import com.widgetkit.core.component.battery.bluetooth.earbuds.EarbudsBatteryUpdateManager
import com.widgetkit.core.component.battery.bluetooth.watch.WatchBatteryUpdateManager
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.proto.WidgetLayout

/**
 * Bluetooth 배터리 업데이트를 각 디바이스 타입별 UpdateManager로 라우팅하는 중앙 관리자
 * BluetoothDeviceReceiver에서 호출되며, 디바이스 타입에 따라 적절한 UpdateManager를 호출합니다.
 */
object BluetoothBatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BluetoothBatteryUpdateMgr"

    // 더 이상 직접 위젯을 가지지 않음 (라우터 역할만 수행)
    override val widget: com.widgetkit.core.component.WidgetComponent
        get() = throw UnsupportedOperationException("BluetoothBatteryUpdateManager is a router, use specific device UpdateManagers")

    override suspend fun updateComponent(context: Context, data: BatteryData) {
        when (data.deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> {
                EarbudsBatteryUpdateManager.updateComponent(context, data)
            }
            DeviceType.BLUETOOTH_WATCH -> {
                WatchBatteryUpdateManager.updateComponent(context, data)
            }
            else -> {
                // 다른 타입은 무시
            }
        }
    }

    override suspend fun syncComponentState(context: Context) {
        // 각 디바이스 타입별로 상태 동기화
        EarbudsBatteryUpdateManager.syncComponentState(context)
        WatchBatteryUpdateManager.syncComponentState(context)
    }
}

internal fun WidgetLayout.checkBluetoothBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("BluetoothBattery") } != null
