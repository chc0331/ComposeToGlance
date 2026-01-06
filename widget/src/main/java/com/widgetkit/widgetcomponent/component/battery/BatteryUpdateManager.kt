package com.widgetkit.widgetcomponent.component.battery

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.widgetcomponent.R
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetkit.widgetcomponent.proto.WidgetLayout
import com.widgetkit.widgetcomponent.provider.LargeAppWidget

object BatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BatteryUpdateManager"
    override val widget: BatteryWidget
        get() = BatteryWidget()

    override suspend fun updateByState(context: Context, widgetId: Int?, data: BatteryData) {
        val glanceManager = GlanceAppWidgetManager(context)
        BatteryComponentDataStore.saveData(context, data)
        
        if (widgetId != null) {
            // 특정 위젯만 업데이트
            val glanceId = glanceManager.getGlanceIdBy(widgetId)
            updateBatteryWidgetState(context, widgetId, data)
            LargeAppWidget().update(context, glanceId)
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component) ->
                    val glanceId = glanceManager.getGlanceIdBy(id)
                    updateBatteryWidgetState(context, id, data)
                    LargeAppWidget().update(context, glanceId)
                }
        }
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: BatteryData) {
        BatteryComponentDataStore.saveData(context, data)
        
        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateBatteryWidgetState(context, widgetId, data)
            val placedComponents = ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
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
                .forEach { (id, component) ->
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
                    Log.i(TAG, "partially update : $id ${data.charging}")
                    ComponentUpdateHelper.partiallyUpdateWidget(context, id, remoteViews)
                }
        }
    }

    override suspend fun syncState(context: Context, data: BatteryData) {
//        val batteryData = BatteryComponentDataStore.loadData(context)
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                Log.i(TAG, "Sync widget state $widgetId $data")
                updateBatteryWidgetState(context, widgetId, data)
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

    fun updateAppWidget(context: Context, widgetId: Int, data: BatteryData) {
        val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
        remoteViews.setTextViewText(R.id.batteryValue, "${data.level.toInt()}%")
        Log.i(TAG, "update : $widgetId ${data.charging} ${R.id.batteryValue}")
        // todo : partiallyUpdate 확인
        AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(widgetId, remoteViews)
    }
}

internal fun WidgetLayout.checkBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("Battery") } != null
