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
 * TodayTodo 컴포넌트의 PreferenceKey
 */
internal object TodayTodoPreferenceKey {
    val Date = stringPreferencesKey("today_todo_date")
    val IncompleteCount = intPreferencesKey("today_todo_incomplete_count")
    val CompletedCount = intPreferencesKey("today_todo_completed_count")
}

/**
 * TodayTodo 컴포넌트의 DataStore
 * * ComponentDataStore를 상속하여 표준화된 데이터 저장/로드를 제공합니다.
 * * Note: Todo 리스트는 복잡한 객체이므로 DataStore에는 개수와 날짜만 저장하고,
 * 실제 Todo 리스트는 Room 데이터베이스에서 조회합니다.
 */
object TodayTodoDataStore : ComponentDataStore<TodayTodoData>() {

    override val datastoreName = "today_todo_pf"

    private val Context.todayTodoDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: TodayTodoData) {
        context.todayTodoDataStore.edit { preferences ->
            preferences[TodayTodoPreferenceKey.Date] = data.date
            preferences[TodayTodoPreferenceKey.IncompleteCount] = data.incompleteCount
            preferences[TodayTodoPreferenceKey.CompletedCount] = data.completedCount
        }
    }

    override suspend fun loadData(context: Context): TodayTodoData {
        val preferences = context.todayTodoDataStore.data.first()
        val date = preferences[TodayTodoPreferenceKey.Date] ?: TodoDateUtils.getTodayDateString()
        val incompleteCount = preferences[TodayTodoPreferenceKey.IncompleteCount] ?: 0
        val completedCount = preferences[TodayTodoPreferenceKey.CompletedCount] ?: 0

        // DataStore에는 개수만 저장되므로, 빈 리스트로 반환
        // 실제 Todo 리스트는 UpdateManager에서 Room DB에서 조회
        return TodayTodoData(
            todos = emptyList(),
            date = date,
            incompleteCount = incompleteCount,
            completedCount = completedCount
        )
    }

    override fun getDefaultData(): TodayTodoData {
        return TodayTodoData.empty()
    }
}
