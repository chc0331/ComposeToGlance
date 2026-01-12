package com.widgetworld.widgetcomponent.component.battery.bluetooth.earbuds

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.widget.RemoteViewsCompat.setImageViewColorFilter
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.battery.BatteryData
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.proto.WidgetLayout

object EarbudsBatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "EarbudsBatteryUpdateMgr"
    override val widget: EarbudsBatteryWidget
        get() = EarbudsBatteryWidget()

    override suspend fun syncState(context: Context, data: BatteryData) {
       updateByState(context, null, data)
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: BatteryData) {
        val glanceManager = GlanceAppWidgetManager(context)

        EarbudsBatteryDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            val glanceId = glanceManager.getGlanceIdBy(widgetId)
            updateBluetoothBatteryWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component, glanceAppWidget) ->
                    val glanceId = glanceManager.getGlanceIdBy(id)
                    updateBluetoothBatteryWidgetState(context, id, data)
                    glanceAppWidget.update(context, glanceId)
                }
        }
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: BatteryData) {
        EarbudsBatteryDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateBluetoothBatteryWidgetState(context, widgetId, data)
            val placedComponents =
                ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            val component = placedComponents.find { it.first == widgetId }?.second
            if (component != null) {
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                val isValidBatteryLevel = data.level in 0f..100f
                remoteViews.setImageViewColorFilter(
                    widget.getEarbudsIconId(gridIndex),
                    if (data.isConnect) Color.Black.toArgb() else Color.LightGray.toArgb()
                )
                // 유효한 배터리 레벨일 때만 텍스트 업데이트
                if (isValidBatteryLevel) {
                    remoteViews.setTextViewText(
                        widget.getEarbudsTextId(gridIndex),
                        if (data.isConnect) data.level.toInt().toString() else "--"
                    )
                }
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
        } else {
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component, _) ->
                    updateBluetoothBatteryWidgetState(context, id, data)
                    val gridIndex = component.gridIndex
                    val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                    val isValidBatteryLevel = data.level in 0f..100f

                    remoteViews.setImageViewColorFilter(
                        widget.getEarbudsIconId(gridIndex),
                        if (data.isConnect) Color.Black.toArgb() else Color.LightGray.toArgb()
                    )
                    // 유효한 배터리 레벨일 때만 텍스트 업데이트
                    if (isValidBatteryLevel) {
                        remoteViews.setTextViewText(
                            widget.getEarbudsTextId(gridIndex),
                            if (data.isConnect) data.level.toInt().toString() else "--"
                        )
                    }
                    ComponentUpdateHelper.partiallyUpdateWidget(context, id, remoteViews)
                }
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
            pref[EarbudsBatteryPreferenceKey.BatteryConnected] = data.isConnect
            if (isValidBatteryLevel) {
                pref[EarbudsBatteryPreferenceKey.BatteryLevel] = data.level
            }
        }
    }
}

internal fun WidgetLayout.checkEarbudsBatteryWidgetExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("EarbudsBattery") } != null
