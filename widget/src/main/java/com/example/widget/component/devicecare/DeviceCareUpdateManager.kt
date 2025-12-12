package com.example.widget.component.devicecare

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.component.update.ComponentUpdateHelper
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.proto.WidgetLayout
import com.example.widget.provider.LargeAppWidget

object DeviceCareUpdateManager : ComponentUpdateManager<DeviceState> {

    private const val TAG = "DeviceCareUpdateManager"

    override val widget: DeviceCareWidget
        get() = DeviceCareWidget()

    override suspend fun updateComponent(
        context: Context,
        data: DeviceState
    ) {
        Log.i(TAG, "updateComponent: $data")
        DeviceCareComponentDataStore.saveData(context, data)

        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                updateWidgetState(context, widgetId, data)
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                val ramProgress = (data.memoryUsage / data.totalMemory) * 100f
                remoteViews.setTextViewText(
                    widget.getRamUsageText(gridIndex),
                    data.memoryUsage.toString()
                )
                remoteViews.setProgressBar(
                    widget.getRamUsageProgress(gridIndex),
                    100, ramProgress.toInt(), false
                )

                val storageProgress = (data.storageUsage / data.totalStorage) * 100f
                remoteViews.setTextViewText(
                    widget.getStorageUsageText(gridIndex),
                    data.storageUsage.toString()
                )
                remoteViews.setProgressBar(
                    widget.getStorageUsageProgress(gridIndex),
                    100, storageProgress.toInt(), false
                )
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }

    override suspend fun syncComponentState(context: Context) {
        val syncData = DeviceStateCollector.collect(context)
        val deviceState = DeviceCareComponentDataStore.saveData(context, syncData)
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                Log.i(TAG, "Sync widget state: $widgetId $deviceState")
                updateWidgetState(context, widgetId, syncData)
                updateWidget(context, widgetId)
            }
        DeviceCareWorker.registerWorker(context)
    }

    private suspend fun updateWidgetState(
        context: Context,
        widgetId: Int,
        data: DeviceState
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[DeviceCarePreferenceKey.MemoryUsage] = data.memoryUsage
            pref[DeviceCarePreferenceKey.TotalMemory] = data.totalMemory
            pref[DeviceCarePreferenceKey.StorageUsage] = data.storageUsage
            pref[DeviceCarePreferenceKey.TotalStorage] = data.totalStorage
        }
    }

    private suspend fun updateWidget(
        context: Context,
        widgetId: Int
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        glanceAppWidgetManager.getGlanceIds(LargeAppWidget::class.java).forEach { id ->
            if (id == glanceId) {
                LargeAppWidget().update(context, id)
            }
        }
    }
}

internal fun WidgetLayout.checkDeviceCareComponentExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("DeviceCare") } != null
