package com.example.widget.component.battery

import WidgetComponentRegistry
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.widget.R
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.proto.WidgetLayout
import com.example.widget.provider.LargeWidgetProvider
import com.example.widget.provider.layoutKey

private const val BATTERY_PREFERENCES_NAME = "battery_info_pf"
internal val Context.batteryDataStore by preferencesDataStore(name = BATTERY_PREFERENCES_NAME)

object BatteryUpdateManager {

    private const val TAG = "BatteryUpdateManager"

    suspend fun updateBatteryWidget(context: Context, data: BatteryData) {
        Log.i(TAG, "updateBatteryWidget $data")
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        batteryRepo.updateBatterInfo(data)

        val glanceManager = GlanceAppWidgetManager(context)
        val manager = AppWidgetManager.getInstance(context)
        manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
            .forEach { widgetId ->
                val glanceId = glanceManager.getGlanceIdBy(widgetId)
                val currentState =
                    getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                val currentLayout: WidgetLayout = WidgetLayout.parseFrom(currentState[layoutKey])

                // 실제 배치된 Battery 컴포넌트만 필터링
                val batteryComponents = currentLayout.placedWidgetComponentList
                    .filter { it.widgetTag.contains("Battery") && !it.widgetTag.contains("Bluetooth") }
                // Battery 컴포넌트 인스턴스 조회
                val batteryComponent =
                    batteryComponents.firstOrNull()?.let { placed: PlacedWidgetComponent ->
                        WidgetComponentRegistry.getComponent(placed.widgetTag) as? BatteryComponent
                    }

                if (batteryComponent == null) return

                batteryComponents.forEach { component ->
                    val gridIndex = component.gridIndex
                    val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
                    remoteViews.setProgressBar(
                        batteryComponent.getBatteryProgressId(gridIndex),
                        100, data.level.toInt(), false
                    )
                    remoteViews.setTextViewText(
                        batteryComponent.getBatteryTextId(gridIndex),
                        "${data.level.toInt()}"
                    )
                    remoteViews.setViewVisibility(
                        batteryComponent.getChargingIconId(gridIndex),
                        if (data.charging) View.VISIBLE else View.GONE
                    )
                    Log.i(TAG, "partially update : $widgetId ${data.charging} ${R.id.batteryValue}")
                    AppWidgetManager.getInstance(context)
                        .partiallyUpdateAppWidget(widgetId, remoteViews)
                }
            }
    }

    suspend fun syncBatteryWidgetState(context: Context) {
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        val batteryData = batteryRepo.getBatteryInfo()

        val widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(
                context,
                LargeWidgetProvider::class.java
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
