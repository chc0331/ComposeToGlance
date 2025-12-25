package com.widgetkit.core.component.devicecare.ram

import android.content.Context
import android.util.Log
import android.view.View
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.core.component.devicecare.DeviceCareWorker
import com.widgetkit.core.component.devicecare.DeviceStateCollector
import com.widgetkit.core.component.update.ComponentUpdateHelper
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.proto.WidgetLayout
import com.widgetkit.core.provider.LargeAppWidget

object RamUpdateManager : ComponentUpdateManager<RamData> {

    private const val TAG = "RamUpdateManager"

    override val widget: RamWidget
        get() = RamWidget()

    override suspend fun syncComponentState(context: Context) {
        val syncData = DeviceStateCollector.collect(context)
        val ramUsage = (syncData.memoryUsage * 100f) / syncData.totalMemory
        val ramData = RamData(ramUsage)
        RamWidgetDataStore.saveData(context, ramData)
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                updateWidgetState(context, widgetId, ramData)
                updateWidget(context, widgetId)
            }
        DeviceCareWorker.registerWorker(context)
    }

    override suspend fun updateComponent(context: Context, data: RamData) {
        RamWidgetDataStore.saveData(context, data)

        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                updateWidgetState(context, widgetId, data)
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                Log.i(TAG, "updateRam / ${data.usagePercent}")
                remoteViews.setTextViewText(
                    widget.getRamTextId(gridIndex),
                    "${data.usagePercent}%"
                )
                remoteViews.setProgressBar(
                    widget.getRamProgressId(gridIndex),
                    100,
                    data.usagePercent.toInt(),
                    false
                )
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }

    suspend fun showAnimation(context: Context, show: Boolean) {
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                remoteViews.setViewVisibility(
                    widget.getRamAnimationId(gridIndex),
                    if (show) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                )
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }

    private suspend fun updateWidgetState(context: Context, widgetId: Int, data: RamData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[RamPreferenceKey.UsagePercent] = data.usagePercent
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

internal fun WidgetLayout.checkRamWidgetExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("RAM") } != null
