package com.widgetkit.core.component.reminder.today

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
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

    override suspend fun syncState(context: Context, data: TodayTodoData) {
        // DataStore에서 현재 선택된 날짜 로드
        val selectedDate = data.selectedDate
        val data = loadTodayTodos(context, selectedDate)
        Log.d(
            TAG,
            "Sync widget state for date $selectedDate: ${data.totalCount} tasks, ${data.completedCount} completed"
        )
        // DataStore에 저장
        TodayTodoDataStore.saveData(context, data)
        updateByState(context, data)
    }

    override suspend fun updateByPartially(context: Context, data: TodayTodoData) {
    }

    override suspend fun updateByState(context: Context, data: TodayTodoData) {
        TodayTodoDataStore.saveData(context, data)
        val placedComponents =
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
        placedComponents.forEach { (widgetId, _) ->
            updateWidget(context, widgetId)
        }
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
                state[com.widgetkit.core.provider.DslAppWidget.WIDGET_SYNC_KEY] =
                    System.currentTimeMillis()
            }

            // LargeAppWidget을 직접 업데이트
            LargeAppWidget().update(context, glanceId)

            Log.d(TAG, "Widget updated: $widgetId")
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

