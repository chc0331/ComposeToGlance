package com.widgetkit.core.component.battery.bluetooth.watch

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setImageViewColorFilter
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.core.component.battery.BatteryData
import com.widgetkit.core.component.update.ComponentUpdateHelper
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.proto.WidgetLayout
import com.widgetkit.core.provider.LargeAppWidget

object WatchBatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "WatchBatteryUpdateMgr"
    override val widget: WatchBatteryWidget
        get() = WatchBatteryWidget()

    override suspend fun updateComponent(context: Context, data: BatteryData) {
        WatchBatteryDataStore.saveData(context, data)

        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                updateWidgetState(context, widgetId, data)
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)

                // 배터리 레벨이 유효한지 확인 (0-100 사이)
                val isValidBatteryLevel = data.level in 0f..100f

                remoteViews.setImageViewColorFilter(
                    widget.getWatchIconId(gridIndex),
                    if (data.isConnect) Color.Black.toArgb() else Color.LightGray.toArgb()
                )
                // 유효한 배터리 레벨일 때만 텍스트 업데이트
                if (isValidBatteryLevel) {
                    remoteViews.setTextViewText(
                        widget.getWatchTextId(gridIndex),
                        if (data.isConnect) data.level.toInt().toString() else "--"
                    )
                }
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }

    override suspend fun syncComponentState(context: Context) {
        val batteryData = WatchBatteryDataStore.loadData(context)
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                updateWidgetState(context, widgetId, batteryData)
                updateWidget(context, widgetId)
            }
    }

    private suspend fun updateWidgetState(context: Context, widgetId: Int, data: BatteryData) {
        // 정상적인 배터리 값(0-100 사이)일 때만 업데이트
        val isValidBatteryLevel = data.level in 0f..100f

        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[WatchBatteryPreferenceKey.BatteryConnected] = data.isConnect
            if (isValidBatteryLevel) {
                pref[WatchBatteryPreferenceKey.BatteryLevel] = data.level
            }
        }
    }

    private suspend fun updateWidget(context: Context, widgetId: Int) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        glanceAppWidgetManager.getGlanceIds(LargeAppWidget::class.java).forEach { id ->
            if (id == glanceId) {
                LargeAppWidget().update(context, id)
            }
        }
    }
}

internal fun WidgetLayout.checkWatchBatteryWidgetExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("WatchBattery") } != null
