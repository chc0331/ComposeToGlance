package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.devicecare.DeviceCareWorker
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.provider.LargeAppWidget
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

object DataUsageUpdateManager : ComponentUpdateManager<DataUsageData> {

    private const val TAG = "DataUsageUpdateManager"

    override val widget: DataUsageTrackerWidget
        get() = DataUsageTrackerWidget()

    override suspend fun syncState(context: Context, data: DataUsageData) {
        updateByState(context, null, data)
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: DataUsageData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        DataUsageDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            updateDataUsageWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, _, appWidget) ->
                    val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                    updateDataUsageWidgetState(context, id, data)
                    appWidget.update(context, glanceId)
                }
        }
        DeviceCareWorker.registerWorker(context)
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: DataUsageData) {
        DataUsageDataStore.saveData(context, data)

        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateDataUsageWidgetState(context, widgetId, data)
            val placedComponents =
                ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            val component = placedComponents.find { it.first == widgetId }?.second
            if (component != null) {
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                
                // Wi-Fi section updates
                val wifiUsageText = String.format("%.1fGB / %.1fGB", data.wifiUsageGb, data.wifiLimitGb)
                remoteViews.setTextViewText(
                    widget.getWifiUsageTextId(gridIndex),
                    wifiUsageText
                )
                remoteViews.setTextViewText(
                    widget.getWifiPercentId(gridIndex),
                    String.format("%.0f%%", data.wifiUsagePercent)
                )
                remoteViews.setProgressBar(
                    widget.getWifiProgressId(gridIndex),
                    100,
                    data.wifiUsagePercent.toInt(),
                    false
                )
                
                // Mobile Data section updates
                val mobileUsageText = String.format("%.1fGB / %.1fGB", data.mobileUsageGb, data.mobileLimitGb)
                remoteViews.setTextViewText(
                    widget.getMobileUsageTextId(gridIndex),
                    mobileUsageText
                )
                remoteViews.setTextViewText(
                    widget.getMobilePercentId(gridIndex),
                    String.format("%.0f%%", data.mobileUsagePercent)
                )
                remoteViews.setProgressBar(
                    widget.getMobileProgressId(gridIndex),
                    100,
                    data.mobileUsagePercent.toInt(),
                    false
                )
                
                // Date range text update
                val dateRange = getCurrentMonthDateRange()
                remoteViews.setTextViewText(
                    widget.getDateRangeTextId(gridIndex),
                    dateRange
                )
                
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
        } else {
            // 모든 위젯 업데이트
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, component, _) ->
                    updateDataUsageWidgetState(context, id, data)
                    val gridIndex = component.gridIndex
                    val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                    
                    // Wi-Fi section updates
                    val wifiUsageText = String.format("%.1fGB / %.1fGB", data.wifiUsageGb, data.wifiLimitGb)
                    remoteViews.setTextViewText(
                        widget.getWifiUsageTextId(gridIndex),
                        wifiUsageText
                    )
                    remoteViews.setTextViewText(
                        widget.getWifiPercentId(gridIndex),
                        String.format("%.0f%%", data.wifiUsagePercent)
                    )
                    remoteViews.setProgressBar(
                        widget.getWifiProgressId(gridIndex),
                        100,
                        data.wifiUsagePercent.toInt(),
                        false
                    )
                    
                    // Mobile Data section updates
                    val mobileUsageText = String.format("%.1fGB / %.1fGB", data.mobileUsageGb, data.mobileLimitGb)
                    remoteViews.setTextViewText(
                        widget.getMobileUsageTextId(gridIndex),
                        mobileUsageText
                    )
                    remoteViews.setTextViewText(
                        widget.getMobilePercentId(gridIndex),
                        String.format("%.0f%%", data.mobileUsagePercent)
                    )
                    remoteViews.setProgressBar(
                        widget.getMobileProgressId(gridIndex),
                        100,
                        data.mobileUsagePercent.toInt(),
                        false
                    )
                    
                    // Date range text update
                    val dateRange = getCurrentMonthDateRange()
                    remoteViews.setTextViewText(
                        widget.getDateRangeTextId(gridIndex),
                        dateRange
                    )
                    
                    ComponentUpdateHelper.partiallyUpdateWidget(context, id, remoteViews)
                }
        }
    }

    private suspend fun updateDataUsageWidgetState(
        context: Context,
        widgetId: Int,
        data: DataUsageData
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            // Legacy fields (for backward compatibility)
            pref[DataUsagePreferenceKey.DataLimitBytes] = data.dataLimitBytes
            pref[DataUsagePreferenceKey.CurrentUsageBytes] = data.currentUsageBytes
            pref[DataUsagePreferenceKey.UsagePercent] = data.usagePercent.toLong()
            // Wi-Fi fields
            pref[DataUsagePreferenceKey.WifiLimitBytes] = data.wifiLimitBytes
            pref[DataUsagePreferenceKey.WifiUsageBytes] = data.wifiUsageBytes
            // Mobile Data fields
            pref[DataUsagePreferenceKey.MobileLimitBytes] = data.mobileLimitBytes
            pref[DataUsagePreferenceKey.MobileUsageBytes] = data.mobileUsageBytes
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
    
    private fun getCurrentMonthDateRange(): String {
        val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDate = Calendar.getInstance()
        
        val dateFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)
        val startDateStr = dateFormat.format(startDate.time)
        val endDateStr = dateFormat.format(endDate.time)
        
        return "$startDateStr - $endDateStr"
    }
}

