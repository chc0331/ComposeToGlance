package com.example.widget.component.battery.bluetooth

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.RemoteViews
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.R
import com.example.widget.ViewKey
import com.example.widget.ViewKey.Battery.getBatteryTextId
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.BatteryPreferenceKey
import com.example.widget.component.battery.BatteryUpdateManager
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice1AddressKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice1BatteryKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice1ConnectedKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice1NameKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice1TypeKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice2AddressKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice2BatteryKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice2ConnectedKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice2NameKey
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget.Companion.btDevice2TypeKey
import com.example.widget.proto.WidgetLayout
import com.example.widget.provider.LargeAppWidget
import com.example.widget.provider.LargeWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.getOrNull

private const val BLUETOOTH_BATTERY_PREFERENCES_NAME = "bluetooth_battery_info_pf"
internal val Context.bluetoothBatteryDataStore by preferencesDataStore(name = BLUETOOTH_BATTERY_PREFERENCES_NAME)

object BluetoothBatteryUpdateManager {

    private const val TAG = "BluetoothBatteryUpdateMgr"


    /**
     * 연결된 블루투스 디바이스 목록으로 위젯 업데이트
     * 최대 2개의 디바이스만 표시 (위젯의 좌우 슬롯)
     */
    suspend fun syncAndUpdateBluetoothBatteryWidgetState(
        context: Context,
        connectedDevices: List<BatteryData>
    ) {
        Log.i("heec.choi", "updateBluetoothBatteryWidget: ${connectedDevices.size} devices")
        val btBatteryRepo =
            BluetoothBatteryInfoPreferencesRepository(context.bluetoothBatteryDataStore)
        // 최대 2개 디바이스만 처리
        val device1 = connectedDevices.getOrNull(0)
        val device2 = connectedDevices.getOrNull(1)
        device1?.let {
            btBatteryRepo.updateBluetoothBatteryInfo(it)
        }
        device2?.let {
            btBatteryRepo.updateBluetoothBatteryInfo(it)
        }

        // 모든 위젯 ID 가져오기
        val widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(
                context,
                LargeWidgetProvider::class.java
            )
        )

        // 각 위젯의 상태 업데이트
        widgetIds.forEach { widgetId ->
            Log.i("heec.choi", "Updating widget state for widgetId: $widgetId $device1 $device2")
            device1?.let {
                updateBluetoothBatteryWidgetState(context, widgetId, it)
            }
            device2?.let {
                updateBluetoothBatteryWidgetState(context, widgetId, it)
            }
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(widgetId)
            LargeAppWidget().update(context, glanceId)
        }
    }

    /**
     * 모든 연결된 디바이스 스캔 및 위젯 동기화
     */
    suspend fun syncBluetoothDeviceState(context: Context) {
        Log.i(TAG, "Syncing bluetooth devices...")
        val btBatteryRepo =
            BluetoothBatteryInfoPreferencesRepository(context.bluetoothBatteryDataStore)

        BluetoothDeviceManager(context).findDevices { connectedDevices ->
            CoroutineScope(Dispatchers.Default).launch {
                connectedDevices.forEach { connectedDevice ->
                    val batteryData =
                        btBatteryRepo.getBluetoothBatteryInfo(connectedDevice.deviceType)
                    val widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
                        ComponentName(
                            context,
                            LargeWidgetProvider::class.java
                        )
                    )
                    widgetIds.forEach {
                        updateBluetoothBatteryWidgetState(context, it, batteryData)
                    }
                }
            }
        }
    }

    private suspend fun updateBluetoothBatteryWidgetState(
        context: Context,
        widgetId: Int,
        data: BatteryData
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            Log.i("heec.choi", "updateBluetoothBatteryWidgetState $glanceId $data")
            if (data.deviceType == DeviceType.BLUETOOTH_EARBUDS) {
                pref[BluetoothBatteryPreferenceKey.BtEarbudsLevel] = data.level
                pref[BluetoothBatteryPreferenceKey.BtEarbudsConnected] = data.isConnect
            } else if (data.deviceType == DeviceType.BLUETOOTH_WATCH) {
                pref[BluetoothBatteryPreferenceKey.BtWatchLevel] = data.level
                pref[BluetoothBatteryPreferenceKey.BtWatchConnected] = data.isConnect
            }
        }
    }

    suspend fun updateBluetoothBatteryWidget(context: Context, data: BatteryData) {
        val btBatteryRepo =
            BluetoothBatteryInfoPreferencesRepository(context.bluetoothBatteryDataStore)
        btBatteryRepo.updateBluetoothBatteryInfo(data)
        val manager = AppWidgetManager.getInstance(context)
        manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
            .forEach { widgetId ->
                val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
                // todo : Current is brute force, need to refactoring
                (0 until 9).forEach {
                    remoteViews.setTextViewText(
                        ViewKey.Bluetooth.getEarBudsTextId(it),
                        "${data.level.toInt()}"
                    )
                }
                Log.i(
                    TAG,
                    "partially update : $widgetId ${data.charging} ${R.id.batteryValue}"
                )
                AppWidgetManager.getInstance(context)
                    .partiallyUpdateAppWidget(widgetId, remoteViews)
            }
    }
}

internal fun WidgetLayout.checkBluetoothBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("BluetoothBattery") } != null
