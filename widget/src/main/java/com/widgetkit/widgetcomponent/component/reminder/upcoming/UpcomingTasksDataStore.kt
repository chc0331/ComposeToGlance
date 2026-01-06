package com.widgetkit.widgetcomponent.component.reminder.upcoming

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.widgetcomponent.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

/**
 * Upcoming Tasks 위젯 DataStore
 * widget id별로 독립적인 필터 설정을 저장합니다.
 */
private val Context.upcomingTasksDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "upcoming_tasks_preferences"
)

/**
 * widget id별 PreferenceKey를 생성하는 헬퍼 함수
 */
private fun getFilterTypeKey(widgetId: Int) = stringPreferencesKey("filter_type_$widgetId")

object UpcomingTasksDataStore : ComponentDataStore<UpcomingTasksData>() {
    
    override val datastoreName: String = "upcoming_tasks_preferences"
    
    /**
     * widget id별 필터 타입 저장
     */
    suspend fun saveFilterType(context: Context, widgetId: Int, filterType: UpcomingFilterType) {
        context.upcomingTasksDataStore.edit { preferences ->
            preferences[getFilterTypeKey(widgetId)] = filterType.name
        }
    }
    
    /**
     * widget id별 필터 타입 로드
     */
    suspend fun loadFilterType(context: Context, widgetId: Int): UpcomingFilterType {
        val preferences = context.upcomingTasksDataStore.data.first()
        val filterTypeName = preferences[getFilterTypeKey(widgetId)] 
            ?: UpcomingFilterType.TODAY.name
        return try {
            UpcomingFilterType.valueOf(filterTypeName)
        } catch (e: Exception) {
            UpcomingFilterType.TODAY
        }
    }
    
    /**
     * widget id별 데이터 저장
     */
    suspend fun saveData(context: Context, widgetId: Int, data: UpcomingTasksData) {
        saveFilterType(context, widgetId, data.filterType)
    }
    
    /**
     * widget id별 데이터 로드
     */
    suspend fun loadData(context: Context, widgetId: Int): UpcomingTasksData {
        val filterType = loadFilterType(context, widgetId)
        // 실제 Todo 리스트는 Room DB에서 조회
        return UpcomingTasksData(
            filterType = filterType,
            todos = emptyList()
        )
    }
    
    /**
     * 기존 메서드 (호환성을 위해 유지, widget id 0 사용)
     * @deprecated widget id를 명시적으로 지정하는 saveData(context, widgetId, data) 사용 권장
     */
    @Deprecated("Use saveData(context, widgetId, data) instead", ReplaceWith("saveData(context, 0, data)"))
    override suspend fun saveData(context: Context, data: UpcomingTasksData) {
        saveData(context, 0, data)
    }
    
    /**
     * 기존 메서드 (호환성을 위해 유지, widget id 0 사용)
     * @deprecated widget id를 명시적으로 지정하는 loadData(context, widgetId) 사용 권장
     */
    @Deprecated("Use loadData(context, widgetId) instead", ReplaceWith("loadData(context, 0)"))
    override suspend fun loadData(context: Context): UpcomingTasksData {
        return loadData(context, 0)
    }
    
    override fun getDefaultData(): UpcomingTasksData {
        return UpcomingTasksData.empty()
    }
}

