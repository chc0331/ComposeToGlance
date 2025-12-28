package com.widgetkit.core.component.reminder.today

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.core.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

/**
 * TodayTodo 위젯 DataStore
 */
private val Context.todayTodoDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "today_todo_preferences"
)

private object TodayTodoPreferenceKeys {
    val SELECTED_DATE = stringPreferencesKey("selected_date")
    val TOTAL_COUNT = intPreferencesKey("total_count")
    val COMPLETED_COUNT = intPreferencesKey("completed_count")
}

object TodayTodoDataStore : ComponentDataStore<TodayTodoData>() {
    
    override val datastoreName: String = "today_todo_preferences"
    
    override suspend fun saveData(context: Context, data: TodayTodoData) {
        context.todayTodoDataStore.edit { preferences ->
            preferences[TodayTodoPreferenceKeys.SELECTED_DATE] = data.selectedDate
            preferences[TodayTodoPreferenceKeys.TOTAL_COUNT] = data.totalCount
            preferences[TodayTodoPreferenceKeys.COMPLETED_COUNT] = data.completedCount
        }
    }
    
    override suspend fun loadData(context: Context): TodayTodoData {
        val preferences = context.todayTodoDataStore.data.first()
        val selectedDate = preferences[TodayTodoPreferenceKeys.SELECTED_DATE] 
            ?: TodoDateUtils.getTodayDateString()
        val totalCount = preferences[TodayTodoPreferenceKeys.TOTAL_COUNT] ?: 0
        val completedCount = preferences[TodayTodoPreferenceKeys.COMPLETED_COUNT] ?: 0
        
        // DataStore에는 개수만 저장, 실제 Todo 리스트는 Room DB에서 조회
        return TodayTodoData(
            selectedDate = selectedDate,
            todos = emptyList(),
            totalCount = totalCount,
            completedCount = completedCount
        )
    }
    
    override fun getDefaultData(): TodayTodoData {
        return TodayTodoData.empty()
    }
}

