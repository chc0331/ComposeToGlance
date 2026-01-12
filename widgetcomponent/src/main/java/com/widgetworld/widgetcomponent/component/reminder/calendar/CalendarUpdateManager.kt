package com.widgetworld.widgetcomponent.component.reminder.calendar

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidget

/**
 * Calendar 위젯 업데이트 관리자
 */
object CalendarUpdateManager : ComponentUpdateManager<CalendarData> {

    private const val TAG = "CalendarUpdateManager"

    override val widget: CalendarWidget
        get() = CalendarWidget()

    override suspend fun syncState(context: Context, data: CalendarData) {
        updateByState(context, null, data)
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: CalendarData) {
        // 캘린더 위젯은 부분 업데이트를 지원하지 않음
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: CalendarData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        CalendarDataStore.saveData(context, data)

        if (widgetId != null) {
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            updateCalendarWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, _, glanceAppWidget) ->
                    val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                    updateCalendarWidgetState(context, id, data)
                    glanceAppWidget.update(context, glanceId)
                }
        }
    }

    private suspend fun updateCalendarWidgetState(
        context: Context,
        widgetId: Int,
        data: CalendarData
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[ComponentContainerWidget.WIDGET_SYNC_KEY] =
                System.currentTimeMillis()
        }
    }
}

