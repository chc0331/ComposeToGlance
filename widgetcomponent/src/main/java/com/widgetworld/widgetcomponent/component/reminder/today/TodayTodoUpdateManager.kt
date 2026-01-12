package com.widgetworld.widgetcomponent.component.reminder.today

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.database.TodoDatabase
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidget
import kotlinx.coroutines.flow.first

/**
 * TodayTodo 위젯 업데이트 관리자
 */
object TodayTodoUpdateManager : ComponentUpdateManager<TodayTodoData> {

    private const val TAG = "TodayTodoUpdateManager"

    override val widget: TodayTodoWidget
        get() = TodayTodoWidget()

    override suspend fun syncState(context: Context, data: TodayTodoData) {
        updateByState(context, null, data)
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: TodayTodoData) {
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: TodayTodoData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        if (widgetId != null) {
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            TodayTodoDataStore.saveData(context, widgetId, data)
            updateTodoWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, _, glanceAppWidget) ->
                    val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                    TodayTodoDataStore.saveData(context, id, data)
                    updateTodoWidgetState(context, id, data)
                    glanceAppWidget.update(context, glanceId)
                }
        }
    }

    private suspend fun updateTodoWidgetState(
        context: Context,
        widgetId: Int,
        data: TodayTodoData
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[ComponentContainerWidget.WIDGET_SYNC_KEY] =
                System.currentTimeMillis()
        }
    }

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

    suspend fun loadTodosForDate(context: Context, date: String): TodayTodoData {
        return loadTodayTodos(context, date)
    }
}

