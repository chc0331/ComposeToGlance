package com.example.widget.component.battery

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
import com.example.widget.proto.WidgetLayout
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.provider.LargeWidgetProvider
import com.example.widget.provider.layoutKey
import WidgetComponentRegistry

private const val BATTERY_PREFERENCES_NAME = "battery_info_pf"
internal val Context.batteryDataStore by preferencesDataStore(name = BATTERY_PREFERENCES_NAME)

object BatteryUpdateManager {

    private const val TAG = "BatteryUpdateManager"

    /**
     * 배터리 정보를 업데이트하고 모든 위젯에 반영합니다.
     * Layout 정보를 기반으로 실제 배치된 Battery 컴포넌트만 업데이트합니다.
     */
    suspend fun updateBatteryWidget(context: Context, data: BatteryData) {
        Log.i(TAG, "updateBatteryWidget $data")
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        batteryRepo.updateBatterInfo(data)

        val manager = AppWidgetManager.getInstance(context)
        val glanceManager = GlanceAppWidgetManager(context)
        
        manager.getAppWidgetIds(ComponentName(context, LargeWidgetProvider::class.java))
            .forEach { widgetId ->
                try {
                    val glanceId = glanceManager.getGlanceIdBy(widgetId)
                    val currentState = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                    val currentLayout: WidgetLayout = WidgetLayout.parseFrom(currentState[layoutKey])
                    
                    // 실제 배치된 Battery 컴포넌트만 필터링
                    val batteryComponents = currentLayout.placedWidgetComponentList
                        .filter { it.widgetTag.contains("Battery") && !it.widgetTag.contains("Bluetooth") }
                    
                    if (batteryComponents.isEmpty()) {
                        Log.d(TAG, "No Battery components found in widget $widgetId")
                        return@forEach
                    }
                    
                    // Battery 컴포넌트 인스턴스 조회
                    val batteryComponent = batteryComponents.firstOrNull()?.let { placed: PlacedWidgetComponent ->
                        WidgetComponentRegistry.getComponent(placed.widgetTag) as? BatteryComponent
                    }
                    
                    if (batteryComponent == null) {
                        Log.w(TAG, "Battery component not found in registry")
                        return@forEach
                    }
                    
                    val remoteViews = RemoteViews(context.packageName, R.layout.glance_root_layout)
                    
                    // 실제 배치된 각 Battery 컴포넌트 업데이트
                    batteryComponents.forEach { placedComponent ->
                        val gridIndex = placedComponent.gridIndex
                        
                        remoteViews.setTextViewText(
                            batteryComponent.getBatteryTextId(gridIndex),
                            "${data.level.toInt()}"
                        )
                        remoteViews.setViewVisibility(
                            batteryComponent.getChargingIconId(gridIndex),
                            if (data.charging) View.VISIBLE else View.GONE
                        )
                        
                        Log.d(TAG, "Updated Battery at gridIndex $gridIndex in widget $widgetId")
                    }
                    
                    AppWidgetManager.getInstance(context)
                        .partiallyUpdateAppWidget(widgetId, remoteViews)
                        
                    Log.i(TAG, "Partially updated widget $widgetId with ${batteryComponents.size} Battery components")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update widget $widgetId", e)
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


}

internal fun WidgetLayout.checkBatteryComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("Battery") } != null
