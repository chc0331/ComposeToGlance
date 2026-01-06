package com.widgetworld.widgetcomponent.component.devicecare.storage

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.devicecare.DeviceCareWorker
import com.widgetworld.widgetcomponent.component.devicecare.DeviceStateCollector
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.proto.WidgetLayout
import com.widgetworld.widgetcomponent.provider.LargeAppWidget

//object StorageUpdateManager : ComponentUpdateManager<StorageData> {
//    override val widget: StorageWidget
//        get() = StorageWidget()
//
//    override suspend fun syncComponentState(context: Context) {
//        val syncData = DeviceStateCollector.collect(context)
//        val storageUsage = (syncData.storageUsage * 100f) / syncData.totalStorage
//        val storageData = StorageData(storageUsage)
//        StorageDataStore.saveData(context, storageData)
//        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
//            .forEach { (widgetId, _) ->
//                updateWidgetState(context, widgetId, storageData)
//                updateWidget(context, widgetId)
//            }
//        DeviceCareWorker.registerWorker(context)
//    }
//
//    override suspend fun updateComponent(context: Context, data: StorageData) {
//        StorageDataStore.saveData(context, data)
//        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
//            .forEach { (widgetId, component) ->
//                updateWidgetState(context, widgetId, data)
//                val gridIndex = component.gridIndex
//                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
//                remoteViews.setTextViewText(
//                    widget.getStorageTextId(gridIndex),
//                    data.usagePercent.toString()
//                )
//                remoteViews.setProgressBar(
//                    widget.getStorageProgressId(gridIndex),
//                    100,
//                    data.usagePercent.toInt(),
//                    false
//                )
//                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
//            }
//    }
//
//    private suspend fun updateWidgetState(context: Context, widgetId: Int, data: StorageData) {
//        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
//        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
//        updateAppWidgetState(context, glanceId) { pref ->
//            pref[StoragePreferenceKey.UsagePercent] = data.usagePercent
//        }
//    }
//
//    private suspend fun updateWidget(context: Context, widgetId: Int) {
//        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
//        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
//        glanceAppWidgetManager.getGlanceIds(LargeAppWidget::class.java).forEach { id ->
//            if (id == glanceId) {
//                LargeAppWidget().update(context, id)
//            }
//        }
//    }
//}
//
//internal fun WidgetLayout.checkStorageWidgetExist(): Boolean =
//    this.placedWidgetComponentList.find { it.widgetTag.contains("Storage") } != null
