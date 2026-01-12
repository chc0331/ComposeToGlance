package com.widgetworld.widgetcomponent.component.devicecare.ram

import android.content.Context
import android.view.View
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.devicecare.DeviceCareWorker
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.proto.WidgetLayout

object RamUpdateManager : ComponentUpdateManager<RamData> {

    private const val TAG = "RamUpdateManager"

    override val widget: RamWidget
        get() = RamWidget()

    override suspend fun syncState(context: Context, data: RamData) {
        updateByState(context, null, data)
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: RamData) {
        RamWidgetDataStore.saveData(context, data)

        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        if (widgetId != null) {
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            updateRamWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, _, appWidget) ->
                    val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                    updateRamWidgetState(context, id, data)
                    appWidget.update(context, glanceId)
                }
        }
        DeviceCareWorker.registerWorker(context)
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: RamData) {
        RamWidgetDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateRamWidgetState(context, widgetId, data)
            val placedComponents =
                ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            val component = placedComponents.find { it.first == widgetId }?.second
            if (component != null) {
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
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
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component, _) ->
                    updateRamWidgetState(context, id, data)
                    val gridIndex = component.gridIndex
                    val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
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
                    ComponentUpdateHelper.partiallyUpdateWidget(context, id, remoteViews)
                }
        }
    }

    suspend fun showAnimation(context: Context, show: Boolean) {
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component, _) ->
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

    private suspend fun updateRamWidgetState(context: Context, widgetId: Int, data: RamData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[RamPreferenceKey.UsagePercent] = data.usagePercent
        }
    }
}

internal fun WidgetLayout.checkRamWidgetExist(): Boolean =
    this.placedWidgetComponentList.find { it.widgetTag.contains("RAM") } != null
