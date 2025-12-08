package com.example.widget.component.battery.bluetooth

import WidgetComponentRegistry
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setImageViewColorFilter
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.widget.R
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.DeviceType
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.proto.WidgetLayout
import com.example.widget.provider.LargeAppWidget
import com.example.widget.provider.LargeWidgetProvider
import com.example.widget.provider.layoutKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        val glanceManager = GlanceAppWidgetManager(context)
        val manager = AppWidgetManager.getInstance(context)
        manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
            .forEach { widgetId ->
                val glanceId = glanceManager.getGlanceIdBy(widgetId)
                val currentState =
                    getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                val currentLayout: WidgetLayout = WidgetLayout.parseFrom(currentState[layoutKey])
                // 실제 배치된 Battery 컴포넌트만 필터링
                val bluetoothComponents = currentLayout.placedWidgetComponentList
                    .filter { it.widgetTag.contains("Bluetooth") }
                val bluetoothComponent =
                    bluetoothComponents.firstOrNull()?.let { placed: PlacedWidgetComponent ->
                        WidgetComponentRegistry.getComponent(placed.widgetTag) as? BluetoothBatteryWidget
                    }

                if (bluetoothComponent == null) return
                bluetoothComponents.forEach { component ->
                    val gridIndex = component.gridIndex
                    val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
                    if (data.deviceType == DeviceType.BLUETOOTH_EARBUDS) {
                        remoteViews.setProgressBar(
                            bluetoothComponent.getEarBudsProgressId(gridIndex),
                            100, if (data.isConnect) data.level.toInt() else 0, false
                        )
                        remoteViews.setImageViewColorFilter(
                            bluetoothComponent.getEarBudsIconId(gridIndex),
                            if (data.isConnect) Color.Black.toArgb() else Color.LightGray.toArgb()
                        )
                        remoteViews.setTextViewText(
                            bluetoothComponent.getEarBudsTextId(gridIndex),
                            if (data.isConnect) data.level.toInt().toString() else ""
                        )
                    } else if (data.deviceType == DeviceType.BLUETOOTH_WATCH) {

                    }
                    AppWidgetManager.getInstance(context)
                        .partiallyUpdateAppWidget(widgetId, remoteViews)
                }
            }
    }
}

internal fun WidgetLayout.checkBluetoothBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("BluetoothBattery") } != null