package com.widgetkit.core.component.reminder.today

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.widgetkit.core.component.update.ComponentUpdateHelper
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.database.TodoDatabase
import com.widgetkit.core.provider.LargeAppWidget
import kotlinx.coroutines.flow.first

/**
 * TodayTodo 위젯 업데이트 관리자
 */
object TodayTodoUpdateManager : ComponentUpdateManager<TodayTodoData> {
    
    private const val TAG = "TodayTodoUpdateManager"
    
    override val widget: TodayTodoWidget
        get() = TodayTodoWidget()
    
    /**
     * 위젯 상태 동기화
     */
    override suspend fun syncComponentState(context: Context) {
        val todayDate = TodoDateUtils.getTodayDateString()
        val data = loadTodayTodos(context, todayDate)
        
        Log.d(TAG, "Sync widget state: ${data.totalCount} tasks, ${data.completedCount} completed")
        
        // DataStore에 저장
        TodayTodoDataStore.saveData(context, data)
        
        // 위젯 업데이트
        updateComponent(context, data)
    }
    
    /**
     * 컴포넌트 업데이트
     */
    override suspend fun updateComponent(context: Context, data: TodayTodoData) {
        Log.d(TAG, "Update component: ${data.totalCount} tasks, ${data.completedCount} completed")
        
        // DataStore에 저장
        TodayTodoDataStore.saveData(context, data)
        
        // 배치된 위젯 찾아서 업데이트
        val placedComponents = ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
        Log.d(TAG, "Found ${placedComponents.size} placed components")
        
        placedComponents.forEach { (widgetId, _) ->
            updateWidget(context, widgetId)
        }
        
        if (placedComponents.isEmpty()) {
            Log.w(TAG, "No placed components found for tag: ${widget.getWidgetTag()}")
        }
    }
    
    /**
     * 위젯 전체 업데이트
     */
    private suspend fun updateWidget(context: Context, widgetId: Int) {
        try {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            
            // RamUpdateManager 패턴 참고: getGlanceIds로 확인 후 업데이트
            glanceAppWidgetManager.getGlanceIds(LargeAppWidget::class.java).forEach { id ->
                if (id == glanceId) {
                    LargeAppWidget().update(context, id)
                    Log.d(TAG, "Widget updated: $widgetId -> $id")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget: $widgetId", e)
        }
    }
    
    /**
     * Room DB에서 오늘 날짜의 Todo 로드
     */
    private suspend fun loadTodayTodos(context: Context, date: String): TodayTodoData {
        return try {
            val todoDao = TodoDatabase.getDatabase(context).todoDao()
            val todos = todoDao.getTodosByDate(date).first()
            TodayTodoData.fromTodos(date, todos)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading todos", e)
            TodayTodoData.empty(date)
        }
    }
    
    /**
     * 특정 날짜의 Todo 데이터 로드
     */
    suspend fun loadTodosForDate(context: Context, date: String): TodayTodoData {
        return loadTodayTodos(context, date)
    }
}

