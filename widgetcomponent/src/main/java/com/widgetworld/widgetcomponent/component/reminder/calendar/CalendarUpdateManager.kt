package com.widgetworld.widgetcomponent.component.reminder.calendar

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.provider.LargeAppWidget
import com.widgetworld.widgetcomponent.provider.common.DslAppWidget

/**
 * Calendar 위젯 업데이트 관리자
 */
object CalendarUpdateManager : ComponentUpdateManager<CalendarData> {

    private const val TAG = "CalendarUpdateManager"

    override val widget: CalendarWidget
        get() = CalendarWidget()

    override suspend fun syncState(context: Context, data: CalendarData) {
        Log.d(
            TAG,
            "Sync widget state for month ${data.yearMonth.year}-${data.yearMonth.month}"
        )
        // DataStore에 저장
        CalendarDataStore.saveData(context, data)
        updateByState(context, null, data)
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: CalendarData) {
        // 캘린더 위젯은 부분 업데이트를 지원하지 않음
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: CalendarData) {
        CalendarDataStore.saveData(context, data)
        
        if (widgetId != null) {
            // 특정 위젯만 업데이트
            updateWidget(context, widgetId)
        } else {
            // 모든 위젯 업데이트
            val placedComponents =
                ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            placedComponents.forEach { (id, _) ->
                updateWidget(context, id)
            }
        }
    }

    /**
     * 특정 widget id만 업데이트
     */
    suspend fun updateWidgetById(context: Context, widgetId: Int, data: CalendarData) {
        // widget id별 데이터 저장
        CalendarDataStore.saveData(context, widgetId, data)
        // 해당 위젯만 업데이트
        updateWidget(context, widgetId)
    }

    /**
     * 위젯 전체 업데이트
     */
    private suspend fun updateWidget(context: Context, widgetId: Int) {
        try {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)

            Log.d(TAG, "Updating widget $widgetId with glanceId $glanceId")

            // WIDGET_SYNC_KEY를 업데이트하여 위젯 갱신 트리거
            updateAppWidgetState(context, glanceId) { state ->
                state[DslAppWidget.WIDGET_SYNC_KEY] =
                    System.currentTimeMillis()
            }

            // LargeAppWidget을 직접 업데이트
            LargeAppWidget().update(context, glanceId)

            Log.d(TAG, "Widget updated: $widgetId")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget: $widgetId", e)
        }
    }
}

