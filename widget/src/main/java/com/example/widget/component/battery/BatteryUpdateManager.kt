package com.example.widget.component.battery

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.R
import com.example.widget.component.update.ComponentUpdateHelper
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.proto.WidgetLayout

private const val BATTERY_PREFERENCES_NAME = "battery_info_pf"
internal val Context.batteryDataStore by preferencesDataStore(name = BATTERY_PREFERENCES_NAME)

object BatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BatteryUpdateManager"
    override val widget: BatteryWidget
        get() = BatteryWidget()

    override suspend fun updateComponent(context: Context, data: BatteryData) {
        Log.i(TAG, "updateComponent $data")
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        batteryRepo.updateBatterInfo(data)
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                updateBatteryWidgetState(context, widgetId, data)

                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                remoteViews.setProgressBar(
                    widget.getBatteryProgressId(gridIndex),
                    100,
                    data.level.toInt(),
                    false
                )
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
    }

    override suspend fun syncComponentState(context: Context) {
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        val batteryData = batteryRepo.getBatteryInfo()
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                Log.i(TAG, "Sync widget state $widgetId $batteryData")
                updateBatteryWidgetState(context, widgetId, batteryData)
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
