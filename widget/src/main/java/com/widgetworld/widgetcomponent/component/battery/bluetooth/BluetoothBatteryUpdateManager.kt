package com.widgetworld.widgetcomponent.component.battery.bluetooth

import android.content.Context
import com.widgetworld.widgetcomponent.component.battery.BatteryData
import com.widgetworld.widgetcomponent.component.battery.DeviceType
import com.widgetworld.widgetcomponent.component.battery.bluetooth.earbuds.EarbudsBatteryUpdateManager
import com.widgetworld.widgetcomponent.component.battery.bluetooth.watch.WatchBatteryUpdateManager
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.proto.WidgetLayout

/**
 * Bluetooth 배터리 업데이트를 각 디바이스 타입별 UpdateManager로 라우팅하는 중앙 관리자
 * BluetoothDeviceReceiver에서 호출되며, 디바이스 타입에 따라 적절한 UpdateManager를 호출합니다.
 */
object BluetoothBatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BluetoothBatteryUpdateMgr"

    // 더 이상 직접 위젯을 가지지 않음 (라우터 역할만 수행)
    override val widget: com.widgetworld.widgetcomponent.component.WidgetComponent
        get() = throw UnsupportedOperationException(
            "BluetoothBatteryUpdateManager is a router, use specific device UpdateManagers"
        )

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: BatteryData) {
        when (data.deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> {
                EarbudsBatteryUpdateManager.updateByPartially(context, widgetId, data)
            }

            DeviceType.BLUETOOTH_WATCH -> {
                WatchBatteryUpdateManager.updateByPartially(context, widgetId, data)
            }

            else -> {
                // 다른 타입은 무시
            }
        }
    }

    override suspend fun syncState(context: Context, data: BatteryData) {
        when (data.deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> {
                EarbudsBatteryUpdateManager.updateByPartially(context, null, data)
            }

            DeviceType.BLUETOOTH_WATCH -> {
                WatchBatteryUpdateManager.updateByPartially(context, null, data)
            }

            else -> {
                // 다른 타입은 무시
            }
        }
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: BatteryData) {
        when (data.deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> {
                EarbudsBatteryUpdateManager.updateByState(context, widgetId, data)
            }

            DeviceType.BLUETOOTH_WATCH -> {
                WatchBatteryUpdateManager.updateByState(context, widgetId, data)
            }

            else -> {
                // 다른 타입은 무시
            }
        }
    }
}

internal fun WidgetLayout.checkBluetoothBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("BluetoothBattery") } != null
