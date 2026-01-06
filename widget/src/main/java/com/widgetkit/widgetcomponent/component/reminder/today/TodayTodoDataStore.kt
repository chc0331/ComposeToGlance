package com.widgetkit.widgetcomponent.component.reminder.today

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.widgetcomponent.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

/**
 * TodayTodo 위젯 DataStore
 * widget id별로 독립적인 저장소를 사용합니다.
 * widgetId를 키에 포함하여 같은 DataStore 내에서 위젯별로 데이터를 구분합니다.
 */
private val Context.todayTodoDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "today_todo_preferences"
)

/**
 * widget id별 PreferenceKey를 생성하는 헬퍼 함수
 */
private fun getSelectedDateKey(widgetId: Int) = stringPreferencesKey("selected_date_$widgetId")
private fun getTotalCountKey(widgetId: Int) = intPreferencesKey("total_count_$widgetId")
private fun getCompletedCountKey(widgetId: Int) = intPreferencesKey("completed_count_$widgetId")

object TodayTodoDataStore : ComponentDataStore<TodayTodoData>() {
    
    override val datastoreName: String = "today_todo_preferences"
    
    /**
     * widget id별 데이터 저장
     */
    suspend fun saveData(context: Context, widgetId: Int, data: TodayTodoData) {
        context.todayTodoDataStore.edit { preferences ->
            preferences[getSelectedDateKey(widgetId)] = data.selectedDate
            preferences[getTotalCountKey(widgetId)] = data.totalCount
            preferences[getCompletedCountKey(widgetId)] = data.completedCount
        }
    }
    
    /**
     * widget id별 데이터 로드
     */
    suspend fun loadData(context: Context, widgetId: Int): TodayTodoData {
        val preferences = context.todayTodoDataStore.data.first()
        val selectedDate = preferences[getSelectedDateKey(widgetId)] 
            ?: TodoDateUtils.getTodayDateString()
        val totalCount = preferences[getTotalCountKey(widgetId)] ?: 0
        val completedCount = preferences[getCompletedCountKey(widgetId)] ?: 0
        
        // DataStore에는 개수만 저장, 실제 Todo 리스트는 Room DB에서 조회
        return TodayTodoData(
            selectedDate = selectedDate,
            todos = emptyList(),
            totalCount = totalCount,
            completedCount = completedCount
        )
    }
    
    /**
     * 기존 메서드 (호환성을 위해 유지, widget id 0 사용)
     * @deprecated widget id를 명시적으로 지정하는 saveData(context, widgetId, data) 사용 권장
     */
    @Deprecated("Use saveData(context, widgetId, data) instead", ReplaceWith("saveData(context, 0, data)"))
    override suspend fun saveData(context: Context, data: TodayTodoData) {
        saveData(context, 0, data)
    }
    
    /**
     * 기존 메서드 (호환성을 위해 유지, widget id 0 사용)
     * @deprecated widget id를 명시적으로 지정하는 loadData(context, widgetId) 사용 권장
     */
    @Deprecated("Use loadData(context, widgetId) instead", ReplaceWith("loadData(context, 0)"))
    override suspend fun loadData(context: Context): TodayTodoData {
        return loadData(context, 0)
    }
    
    override fun getDefaultData(): TodayTodoData {
        return TodayTodoData.empty()
    }
}

