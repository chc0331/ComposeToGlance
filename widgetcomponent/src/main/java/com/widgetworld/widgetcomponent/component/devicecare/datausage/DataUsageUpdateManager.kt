package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.devicecare.DeviceCareWorker
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.provider.LargeAppWidget

object DataUsageUpdateManager : ComponentUpdateManager<DataUsageData> {

    private const val TAG = "DataUsageUpdateManager"

    override val widget: DataUsageTrackerWidget
        get() = DataUsageTrackerWidget()

    override suspend fun updateByState(context: Context, widgetId: Int?, data: DataUsageData) {
        DataUsageDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateWidgetState(context, widgetId, data)
            updateWidget(context, widgetId)
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, _) ->
                    updateWidgetState(context, id, data)
                    updateWidget(context, id)
                }
        }
        DeviceCareWorker.registerWorker(context)
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: DataUsageData) {
        DataUsageDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateWidgetState(context, widgetId, data)
            val placedComponents = ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            val component = placedComponents.find { it.first == widgetId }?.second
            if (component != null) {
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                Log.i(TAG, "updateDataUsage / ${data.usagePercent}%")
                
                // 텍스트 업데이트: "X.X GB / Y.Y GB" 형식
                val usageText = String.format("%.1f GB / %.1f GB", data.currentUsageGb, data.dataLimitGb)
                remoteViews.setTextViewText(
                    widget.getDataUsageTextId(gridIndex),
                    usageText
                )
                
                // Progress 바 업데이트
                remoteViews.setProgressBar(
                    widget.getDataUsageProgressId(gridIndex),
                    100,
                    data.usagePercent.toInt(),
                    false
                )
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component) ->
                    updateWidgetState(context, id, data)
                    val gridIndex = component.gridIndex
                    val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                    Log.i(TAG, "updateDataUsage / ${data.usagePercent}%")
                    
                    // 텍스트 업데이트: "X.X GB / Y.Y GB" 형식
                    val usageText = String.format("%.1f GB / %.1f GB", data.currentUsageGb, data.dataLimitGb)
                    remoteViews.setTextViewText(
                        widget.getDataUsageTextId(gridIndex),
                        usageText
                    )
                    
                    // Progress 바 업데이트
                    remoteViews.setProgressBar(
                        widget.getDataUsageProgressId(gridIndex),
                        100,
                        data.usagePercent.toInt(),
                        false
                    )
                    ComponentUpdateHelper.partiallyUpdateWidget(context, id, remoteViews)
                }
        }
    }

    override suspend fun syncState(context: Context, data: DataUsageData) {
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                updateWidgetState(context, widgetId, data)
                updateWidget(context, widgetId)
            }
    }

    private suspend fun updateWidgetState(context: Context, widgetId: Int, data: DataUsageData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[DataUsagePreferenceKey.DataLimitBytes] = data.dataLimitBytes
            pref[DataUsagePreferenceKey.CurrentUsageBytes] = data.currentUsageBytes
            pref[DataUsagePreferenceKey.UsagePercent] = data.usagePercent.toLong()
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

