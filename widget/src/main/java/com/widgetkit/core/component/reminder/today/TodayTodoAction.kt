package com.widgetkit.core.component.reminder.today

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import com.widgetkit.core.database.TodoDatabase
import com.widgetkit.dsl.widget.action.WidgetActionCallback
import com.widgetkit.dsl.widget.action.WidgetActionParameters

/**
 * Todo 체크박스 클릭 액션 처리
 */
class TodayTodoAction : WidgetActionCallback {

    companion object {
        const val TAG = "TodayTodoAction"
        const val PARAM_TODO_ID = "todoId"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        Log.d(TAG, "Todo checkbox clicked")
        
        // 파라미터에서 Todo ID 추출
        val todoId = parameters[WidgetActionParameters.Key<Long>(PARAM_TODO_ID)]
        if (todoId == null) {
            Log.e(TAG, "Todo ID not found in parameters")
            return
        }
        
        Log.d(TAG, "Toggling Todo status for ID: $todoId")
        
        try {
            // TodoRepository를 통해 상태 토글
            val repository = TodoRepository(context)
            val todo = repository.getTodoById(todoId)
            
            if (todo != null) {
                // 현재 상태의 반대로 토글
                val newStatus = when (todo.status) {
                    TodoStatus.COMPLETED -> TodoStatus.INCOMPLETE
                    TodoStatus.INCOMPLETE -> TodoStatus.COMPLETED
                }
                
                // 상태 업데이트
                repository.toggleTodoStatus(todoId, newStatus)
                Log.d(TAG, "Todo status updated: $todoId -> $newStatus")
                
                // 위젯 업데이트
                val updateManager = TodayTodoUpdateManager
                updateManager.syncComponentState(context)
                Log.d(TAG, "Widget updated successfully")
                
            } else {
                Log.e(TAG, "Todo not found with ID: $todoId")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling Todo status", e)
        }
    }
}
