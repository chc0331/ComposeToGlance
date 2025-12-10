package com.example.widget.component.battery.bluetooth

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setImageViewColorFilter
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.update.ComponentUpdateHelper
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.proto.WidgetLayout

object BluetoothBatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BluetoothBatteryUpdateMgr"
    override val widget: BluetoothBatteryWidget
        get() = BluetoothBatteryWidget()

    override suspend fun updateComponent(context: Context, data: BatteryData) {
        // 새로운 ComponentDataStore 사용
        BluetoothBatteryComponentDataStore.updateDeviceData(context, data)

        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                // GlanceAppWidgetState 업데이트 (위젯 재렌더링 시 올바른 값 표시)
                updateBluetoothBatteryWidgetState(context, widgetId, data)

                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)

                // 배터리 레벨이 유효한지 확인 (0-100 사이)
                val isValidBatteryLevel = data.level in 0f..100f

                if (data.deviceType == DeviceType.BLUETOOTH_EARBUDS) {
                    remoteViews.setProgressBar(
                        widget.getEarBudsProgressId(gridIndex),
                        100,
                        if (data.isConnect && isValidBatteryLevel) data.level.toInt() else 0,
                        false
                    )
                    remoteViews.setImageViewColorFilter(
                        widget.getEarBudsIconId(gridIndex),
                        if (data.isConnect) Color.Black.toArgb() else Color.LightGray.toArgb()
                    )
                    // 유효한 배터리 레벨일 때만 텍스트 업데이트
                    if (isValidBatteryLevel) {
                        remoteViews.setTextViewText(
                            widget.getEarBudsTextId(gridIndex),
                            if (data.isConnect) data.level.toInt().toString() else ""
                        )
                    }
                } else if (data.deviceType == DeviceType.BLUETOOTH_WATCH) {
                    // Watch 업데이트 로직 추가 필요 시 여기에 구현
                    remoteViews.setProgressBar(
                        widget.getWatchProgressId(gridIndex),
                        100,
                        if (data.isConnect && isValidBatteryLevel) data.level.toInt() else 0,
                        false
                    )
                    remoteViews.setImageViewColorFilter(
                        widget.getWatchIconId(gridIndex),
                        if (data.isConnect) Color.Black.toArgb() else Color.LightGray.toArgb()
                    )
                    // 유효한 배터리 레벨일 때만 텍스트 업데이트
                    if (isValidBatteryLevel) {
                        remoteViews.setTextViewText(
                            widget.getWatchTextId(gridIndex),
                            if (data.isConnect) data.level.toInt().toString() else ""
                        )
                    }
                }
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }


    override suspend fun syncComponentState(context: Context) {
        // 새로운 ComponentDataStore 사용
        val compositeData = BluetoothBatteryComponentDataStore.loadData(context)
        val earBudsData = compositeData.earbudsData
        val watchData = compositeData.watchData
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                updateBluetoothBatteryWidgetState(context, widgetId, earBudsData)
                updateBluetoothBatteryWidgetState(context, widgetId, watchData)
            }
    }

    private suspend fun updateBluetoothBatteryWidgetState(
        context: Context,
        widgetId: Int,
        data: BatteryData
    ) {
        // 정상적인 배터리 값(0-100 사이)일 때만 업데이트
        val isValidBatteryLevel = data.level in 0f..100f

        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            if (data.deviceType == DeviceType.BLUETOOTH_EARBUDS) {
                pref[BluetoothBatteryPreferenceKey.BtEarbudsConnected] = data.isConnect
                if (isValidBatteryLevel) {
                    pref[BluetoothBatteryPreferenceKey.BtEarbudsLevel] = data.level
                }
            } else if (data.deviceType == DeviceType.BLUETOOTH_WATCH) {
                pref[BluetoothBatteryPreferenceKey.BtWatchConnected] = data.isConnect
                if (isValidBatteryLevel) {
                    pref[BluetoothBatteryPreferenceKey.BtWatchLevel] = data.level
                }
            }
        }
    }
}

internal fun WidgetLayout.checkBluetoothBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("BluetoothBattery") } != null
