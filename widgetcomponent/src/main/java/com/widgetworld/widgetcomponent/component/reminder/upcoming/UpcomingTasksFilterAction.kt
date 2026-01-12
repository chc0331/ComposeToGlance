package com.widgetworld.widgetcomponent.component.reminder.upcoming

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import com.widgetworld.core.widget.action.WidgetActionCallback
import com.widgetworld.core.widget.action.WidgetActionParameters

/**
 * Upcoming Tasks 필터 변경 액션 처리
 */
class UpcomingTasksFilterAction : WidgetActionCallback {

    companion object {
        const val TAG = "UpcomingTasksFilterAction"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        Log.d(TAG, "Upcoming Tasks filter changed")
        
        // 파라미터에서 widgetId와 필터 타입 추출
        val widgetId = parameters[WidgetActionParameters.Key<Int>("widgetId")]
        val filterTypeName = parameters[WidgetActionParameters.Key<String>("filterType")]
        
        if (widgetId == null) {
            Log.e(TAG, "WidgetId not found in parameters")
            return
        }
        
        if (filterTypeName == null) {
            Log.e(TAG, "Filter type not found in parameters")
            return
        }
        
        try {
            val filterType = try {
                UpcomingFilterType.valueOf(filterTypeName)
            } catch (e: Exception) {
                Log.e(TAG, "Invalid filter type: $filterTypeName", e)
                return
            }
            
            Log.d(TAG, "Changing filter to $filterType for widget $widgetId")
            
            // 필터 타입 저장
            UpcomingTasksDataStore.saveFilterType(context, widgetId, filterType)
            
            // 필터된 Todo 로드 및 위젯 업데이트
            val updateManager = UpcomingTasksUpdateManager
            val updatedData = updateManager.loadUpcomingTodos(context, filterType)
            updateManager.updateByState(context, widgetId, updatedData)
            
            Log.d(TAG, "Widget $widgetId filter updated successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error changing filter for widget $widgetId", e)
        }
    }
}

