package com.widgetworld.widgetcomponent.component.battery

import android.content.Context
import android.util.Log
import android.view.View
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.proto.WidgetLayout

object BatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BatteryUpdateManager"
    override val widget: BatteryWidget
        get() = BatteryWidget()

    override suspend fun syncState(context: Context, data: BatteryData) {
        updateByState(context, null, data)
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: BatteryData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        BatteryComponentDataStore.saveData(context, data)

        if (widgetId != null) {
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            updateBatteryWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component, glanceAppWidget) ->
                    val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                    updateBatteryWidgetState(context, id, data)
                    glanceAppWidget.update(context, glanceId)
                }
        }
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: BatteryData) {
        BatteryComponentDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateBatteryWidgetState(context, widgetId, data)
            val placedComponents =
                ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            val component = placedComponents.find { it.first == widgetId }?.second
            if (component != null) {
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                remoteViews.setTextViewText(
                    widget.getBatteryTextId(gridIndex),
                    "${data.level.toInt()}"
                )
                remoteViews.setViewVisibility(
                    widget.getChargingIconId(gridIndex),
                    if (data.charging) View.VISIBLE else View.GONE
                )
                Log.i(TAG, "partially update : $widgetId ${data.charging}")
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component, _) ->
                    updateBatteryWidgetState(context, id, data)

                    val gridIndex = component.gridIndex
                    val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                    remoteViews.setTextViewText(
                        widget.getBatteryTextId(gridIndex),
                        "${data.level.toInt()}"
                    )
                    remoteViews.setViewVisibility(
                        widget.getChargingIconId(gridIndex),
                        if (data.charging) View.VISIBLE else View.GONE
                    )
                    ComponentUpdateHelper.partiallyUpdateWidget(context, id, remoteViews)
                }
        }
    }

    private suspend fun updateBatteryWidgetState(
        context: Context,
        widgetId: Int,
        data: BatteryData
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[BatteryPreferenceKey.Level] = data.level
            pref[BatteryPreferenceKey.Charging] = data.charging
        }
    }
}

internal fun WidgetLayout.checkBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("Battery") } != null
