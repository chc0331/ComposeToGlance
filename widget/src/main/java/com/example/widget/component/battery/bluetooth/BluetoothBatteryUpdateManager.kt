package com.example.widget.component.battery.bluetooth

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.widget.R
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.BatteryUpdateManager
import com.example.widget.component.battery.DeviceType
import com.example.widget.proto.WidgetLayout
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.provider.LargeAppWidget
import com.example.widget.provider.LargeWidgetProvider
import WidgetComponentRegistry
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    /**
     * 블루투스 배터리 위젯을 업데이트합니다.
     * Layout 정보를 기반으로 실제 배치된 BluetoothBattery 컴포넌트만 업데이트합니다.
     */
    suspend fun updateBluetoothBatteryWidget(context: Context, data: BatteryData) {
        val btBatteryRepo =
            BluetoothBatteryInfoPreferencesRepository(context.bluetoothBatteryDataStore)
        btBatteryRepo.updateBluetoothBatteryInfo(data)
        
        val manager = AppWidgetManager.getInstance(context)
        val glanceManager = GlanceAppWidgetManager(context)
        
        manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
            .forEach { widgetId ->
                try {
                    val glanceId = glanceManager.getGlanceIdBy(widgetId)
                    val currentState = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                    val currentLayout: WidgetLayout = WidgetLayout.parseFrom(
                        currentState[com.example.widget.provider.layoutKey]
                    )
                    
                    // 실제 배치된 BluetoothBattery 컴포넌트 필터링
                    val bluetoothComponents = currentLayout.placedWidgetComponentList
                        .filter { it.widgetTag.contains("BluetoothBattery") }
                    
                    if (bluetoothComponents.isEmpty()) {
                        Log.d(TAG, "No BluetoothBattery components found in widget $widgetId")
                        return@forEach
                    }
                    
                    // BluetoothBattery 컴포넌트 인스턴스 조회
                    val bluetoothComponent = bluetoothComponents.firstOrNull()?.let { placed ->
                        WidgetComponentRegistry.getComponent(placed.widgetTag) as? BluetoothBatteryWidget
                    }
                    
                    if (bluetoothComponent == null) {
                        Log.w(TAG, "BluetoothBattery component not found in registry")
                        return@forEach
                    }
                    
                    val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
                    
                    // 실제 배치된 각 BluetoothBattery 컴포넌트 업데이트
                    bluetoothComponents.forEach { placedComponent ->
                        val gridIndex = placedComponent.gridIndex
                        
                        // 디바이스 타입에 따라 적절한 View ID 선택
                        val textViewId = when (data.deviceType) {
                            DeviceType.BLUETOOTH_EARBUDS -> bluetoothComponent.getEarBudsTextId(gridIndex)
                            DeviceType.BLUETOOTH_WATCH -> bluetoothComponent.getWatchTextId(gridIndex)
                            else -> {
                                Log.w(TAG, "Unknown device type: ${data.deviceType}")
                                return@forEach
                            }
                        }
                        
                        remoteViews.setTextViewText(
                            textViewId,
                            "${data.level.toInt()}"
                        )
                        
                        Log.d(TAG, "Updated BluetoothBattery at gridIndex $gridIndex for ${data.deviceType}")
                    }
                    
                    AppWidgetManager.getInstance(context)
                        .partiallyUpdateAppWidget(widgetId, remoteViews)
                        
                    Log.i(TAG, "Partially updated widget $widgetId with ${bluetoothComponents.size} BluetoothBattery components")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update widget $widgetId", e)
                }
            }
    }
}

internal fun WidgetLayout.checkBluetoothBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("BluetoothBattery") } != null
