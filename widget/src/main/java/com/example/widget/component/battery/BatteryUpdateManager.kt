package com.example.widget.component.battery

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.R
import com.example.widget.WidgetComponentRegistry
import com.example.widget.component.core.ComponentUpdateHelper
import com.example.widget.component.core.ComponentUpdateManager
import com.example.widget.proto.WidgetLayout

object BatteryUpdateManager : ComponentUpdateManager<BatteryData> {

    private const val TAG = "BatteryUpdateManager"

    override suspend fun updateComponent(context: Context, data: BatteryData) {
        Log.i(TAG, "updateComponent $data")
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        batteryRepo.updateBatterInfo(data)

        // Battery 컴포넌트 태그들을 동적으로 찾기 (하드코딩 제거)
        val batteryComponentTags = WidgetComponentRegistry.getAllComponents()
            .filterIsInstance<BatteryComponent>()
            .map { it.getWidgetTag() }

        batteryComponentTags.forEach { componentTag ->
            val placedComponents = ComponentUpdateHelper.findPlacedComponents(context, componentTag)

            placedComponents.forEach { (widgetId, placedComponent) ->
                val batteryComponent = ComponentUpdateHelper.getComponentInstance(componentTag) as? BatteryComponent
                    ?: return@forEach

                // GlanceAppWidgetState 업데이트 (위젯 재렌더링 시 올바른 값 표시)
                updateBatteryWidgetState(context, widgetId, data)

                val gridIndex = placedComponent.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)

                remoteViews.setProgressBar(
                    batteryComponent.getBatteryProgressId(gridIndex),
                    100,
                    data.level.toInt(),
                    false
                )
                remoteViews.setTextViewText(
                    batteryComponent.getBatteryTextId(gridIndex),
                    "${data.level.toInt()}"
                )
                remoteViews.setViewVisibility(
                    batteryComponent.getChargingIconId(gridIndex),
                    if (data.charging) View.VISIBLE else View.GONE
                )

                Log.i(TAG, "partially update : $widgetId ${data.charging}")
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
        }
    }

    override suspend fun syncComponentState(context: Context) {
        val batteryRepo = BatteryInfoPreferencesRepository(context.batteryDataStore)
        val batteryData = batteryRepo.getBatteryInfo()

        val batteryComponentTags = WidgetComponentRegistry.getAllComponents()
            .filterIsInstance<BatteryComponent>()
            .map { it.getWidgetTag() }

        batteryComponentTags.forEach { componentTag ->
            val placedComponents = ComponentUpdateHelper.findPlacedComponents(context, componentTag)

            placedComponents.forEach { (widgetId, _) ->
                Log.i(TAG, "Sync widget state $widgetId $batteryData")
                updateBatteryWidgetState(context, widgetId, batteryData)
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
