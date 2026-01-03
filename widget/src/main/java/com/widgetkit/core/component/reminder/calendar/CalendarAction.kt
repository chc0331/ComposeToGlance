package com.widgetkit.core.component.reminder.calendar

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import com.widgetkit.dsl.widget.action.WidgetActionCallback
import com.widgetkit.dsl.widget.action.WidgetActionParameters

/**
 * 캘린더 월 변경 액션 처리
 */
class CalendarAction : WidgetActionCallback {

    companion object {
        const val TAG = "CalendarAction"
        const val PARAM_ACTION = "action"  // "prev" or "next"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        Log.d(TAG, "Calendar month navigation action")
        
        // 파라미터에서 액션 타입 추출
        val action = parameters[WidgetActionParameters.Key<String>(PARAM_ACTION)]
        if (action == null) {
            Log.e(TAG, "Action parameter not found")
            return
        }
        
        Log.d(TAG, "Action: $action")
        
        try {
            // 현재 데이터 로드
            val currentData = CalendarDataStore.loadData(context)
            val currentYearMonth = currentData.yearMonth
            
            // 새로운 년/월 계산
            val newYearMonth = when (action) {
                "prev" -> currentYearMonth.previousMonth()
                "next" -> currentYearMonth.nextMonth()
                else -> {
                    Log.e(TAG, "Unknown action: $action")
                    return
                }
            }
            
            Log.d(TAG, "Changing month from ${currentYearMonth.year}-${currentYearMonth.month} to ${newYearMonth.year}-${newYearMonth.month}")
            
            // 새로운 데이터 저장
            val newData = CalendarData.fromYearMonth(newYearMonth.year, newYearMonth.month)
            CalendarDataStore.saveData(context, newData)
            
            // 위젯 업데이트
            CalendarUpdateManager.updateByState(context, newData)
            Log.d(TAG, "Calendar widget updated successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error changing calendar month", e)
        }
    }
}

