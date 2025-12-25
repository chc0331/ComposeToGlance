package com.widgetkit.core.component.reminder.today

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.core.component.update.ComponentUpdateHelper
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.database.TodoDatabase
import com.widgetkit.core.provider.LargeAppWidget
import kotlinx.coroutines.flow.first

/**
 * TodayTodo 위젯의 업데이트를 관리하는 Manager
 * * Room 데이터베이스에서 오늘 날짜의 Todo를 조회하고 위젯을 업데이트합니다.
 */
object TodayTodoUpdateManager : ComponentUpdateManager<TodayTodoData> {

    private const val TAG = "TodayTodoUpdateManager"

    override val widget: TodayTodoWidget
        get() = TodayTodoWidget()

    /**
     * 위젯 상태를 동기화합니다.
     * 위젯이 표시될 때 호출되어 최신 Todo 데이터를 로드합니다.
     */
    override suspend fun syncComponentState(context: Context) {
        val todayDate = TodoDateUtils.getTodayDateString()
        val todos = loadTodayTodos(context, todayDate)
        val data = TodayTodoData.fromTodos(todos, todayDate)

        Log.i(
            TAG,
            "Sync widget state: ${data.incompleteCount} incomplete, ${data.completedCount} completed"
        )

        // DataStore에 저장
        TodayTodoDataStore.saveData(context, data)

        // 위젯 업데이트
        updateComponent(context, data)
    }

    /**
     * 컴포넌트를 업데이트합니다.
     * Todo 데이터가 변경되었을 때 호출됩니다.
     */
    override suspend fun updateComponent(context: Context, data: TodayTodoData) {
        Log.i(
            TAG,
            "Update component: ${data.todos.size} todos, ${data.incompleteCount} incomplete, ${data.completedCount} completed"
        )

        // DataStore에 저장
        TodayTodoDataStore.saveData(context, data)

        // 배치된 위젯들을 찾아서 업데이트
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                updateWidgetState(context, widgetId, data)
                // Todo 리스트가 변경되면 전체 위젯을 다시 렌더링
                updateWidget(context, widgetId)
            }
    }

    /**
     * 위젯 상태를 업데이트합니다.
     * GlanceAppWidget state에 데이터를 저장합니다.
     */
    private suspend fun updateWidgetState(context: Context, widgetId: Int, data: TodayTodoData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)

        // Widget state에 간단한 값들만 저장 (Todo 리스트는 복잡하므로 위젯 렌더링 시 DB에서 조회)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[TodayTodoWidgetStateKey.Date] = data.date
            pref[TodayTodoWidgetStateKey.IncompleteCount] = data.incompleteCount
            pref[TodayTodoWidgetStateKey.CompletedCount] = data.completedCount
        }
    }

    /**
     * 위젯을 전체 업데이트합니다.
     */
    private suspend fun updateWidget(context: Context, widgetId: Int) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)

        // LargeAppWidget을 통해 전체 위젯 업데이트
        glanceAppWidgetManager.getGlanceIds(LargeAppWidget::class.java).forEach { id ->
            if (id == glanceId) {
                LargeAppWidget().update(context, id)
            }
        }
    }

    /**
     * Room 데이터베이스에서 오늘 날짜의 Todo를 조회합니다.
     */
    private suspend fun loadTodayTodos(
        context: Context,
        date: String
    ): List<com.widgetkit.core.database.TodoEntity> {
        return try {
            val todoDao = TodoDatabase.getDatabase(context).todoDao()
            todoDao.getTodosByDate(date).first()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading today todos", e)
            emptyList()
        }
    }
}

/**
 * Widget state에 저장할 PreferenceKey
 * DataStore의 PreferenceKey와는 별개로 위젯 state에 사용됩니다.
 */
internal object TodayTodoWidgetStateKey {
    val Date = stringPreferencesKey("today_todo_widget_date")
    val IncompleteCount = intPreferencesKey("today_todo_widget_incomplete_count")
    val CompletedCount = intPreferencesKey("today_todo_widget_completed_count")
}
