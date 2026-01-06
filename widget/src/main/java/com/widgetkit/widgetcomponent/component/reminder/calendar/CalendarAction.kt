package com.widgetkit.widgetcomponent.component.reminder.calendar

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.state.GlanceState
import com.widgetkit.dsl.widget.action.WidgetActionCallback
import com.widgetkit.dsl.widget.action.WidgetActionParameters

/**
 * 캘린더 월 변경 액션 처리
 */
class CalendarAction : WidgetActionCallback {

    companion object {
        const val TAG = "CalendarAction"
        const val PARAM_ACTION = "action"  // "prev" or "next"
        const val PARAM_YEAR = "year"
        const val PARAM_MONTH = "month"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        Log.d(TAG, "Calendar month navigation action")
        
        // 파라미터에서 widgetId, year, month, action 추출
        val widgetId = parameters[WidgetActionParameters.Key<Int>("widgetId")]
        val year = parameters[WidgetActionParameters.Key<Int>(PARAM_YEAR)]
        val month = parameters[WidgetActionParameters.Key<Int>(PARAM_MONTH)]
        val action = parameters[WidgetActionParameters.Key<String>(PARAM_ACTION)]
        
        if (widgetId == null) {
            Log.e(TAG, "WidgetId parameter not found")
            return
        }
        
        if (year == null || month == null) {
            Log.e(TAG, "Year or month parameter not found")
            return
        }
        
        if (action == null) {
            Log.e(TAG, "Action parameter not found")
            return
        }
        
        Log.d(TAG, "Action: $action for widget $widgetId, current month: $year-$month")
        
        try {
            // 파라미터에서 받은 year/month로 YearMonth 생성
            val currentYearMonth = CalendarDateUtils.YearMonth(year, month)
            
            // 새로운 년/월 계산
            val newYearMonth = when (action) {
                "prev" -> currentYearMonth.previousMonth()
                "next" -> currentYearMonth.nextMonth()
                else -> {
                    Log.e(TAG, "Unknown action: $action")
                    return
                }
            }
            
            Log.d(TAG, "Changing month from ${currentYearMonth.year}-${currentYearMonth.month} to ${newYearMonth.year}-${newYearMonth.month} for widget $widgetId")
            
            // 새로운 데이터 저장 (widget id별)
            val newData = CalendarData.fromYearMonth(newYearMonth.year, newYearMonth.month)
            CalendarDataStore.saveData(context, widgetId, newData)
            
            // 해당 위젯만 업데이트
            CalendarUpdateManager.updateWidgetById(context, widgetId, newData)
            Log.d(TAG, "Calendar widget $widgetId updated successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error changing calendar month for widget $widgetId", e)
        }
    }
}

