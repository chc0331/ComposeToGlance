package com.example.widget.component.battery

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.R
import com.example.widget.proto.WidgetLayout
import com.example.widget.provider.LargeWidgetProvider

private const val BATTERY_PREFERENCES_NAME = "battery_info_pf"
internal val Context.batteryDataStore by preferencesDataStore(name = BATTERY_PREFERENCES_NAME)

object BatteryUpdateManager {

    private const val TAG = "BatteryUpdateManager"

    suspend fun updateBatteryWidget(context: Context, data: BatteryData) {
        Log.i(TAG, "updateBatteryWidget $data")
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        batteryRepo.updateBatterInfo(data)

//        val manager = AppWidgetManager.getInstance(context)
//        manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java)).forEach {
//            updateAppWidgetState(context, it, data)
//            updateAppWidget(context, it, data)
//        }
    }

    suspend fun syncBatteryWidgetState(context: Context) {
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        val batteryData = batteryRepo.getBatteryInfo()

        val widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(
                context, LargeWidgetProvider::class.java
            )
        )
        widgetIds.forEach {
            Log.i(TAG, "Sync widget state $it $batteryData")
            updateBatteryWidgetState(context, it, batteryData)
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
            pref[BatteryPreferenceKey.Phone.Level] = data.level
            pref[BatteryPreferenceKey.Phone.Charging] = data.charging
        }
    }

    fun updateAppWidget(context: Context, widgetId: Int, data: BatteryData) {
        val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
        remoteViews.setTextViewText(R.id.batteryValue, "${data.level.toInt()}%")
        Log.i(TAG, "update : $widgetId ${data.charging} ${R.id.batteryValue}")
        //todo : partiallyUpdate 확인
        AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(widgetId, remoteViews)
    }
}

fun WidgetLayout.checkBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("Battery") } != null