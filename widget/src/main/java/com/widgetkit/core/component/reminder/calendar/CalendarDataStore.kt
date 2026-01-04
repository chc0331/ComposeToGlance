package com.widgetkit.core.component.reminder.calendar

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
 * Calendar 위젯 DataStore
 * widget id별로 독립적인 저장소를 사용합니다.
 * widgetId를 키에 포함하여 같은 DataStore 내에서 위젯별로 데이터를 구분합니다.
 */
private val Context.calendarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "calendar_preferences"
)

/**
 * widget id별 PreferenceKey를 생성하는 헬퍼 함수
 */
private fun getYearKey(widgetId: Int) = intPreferencesKey("year_$widgetId")
private fun getMonthKey(widgetId: Int) = intPreferencesKey("month_$widgetId")
private fun getCountryCodeKey(widgetId: Int) = stringPreferencesKey("country_code_$widgetId")

object CalendarDataStore : ComponentDataStore<CalendarData>() {
    
    override val datastoreName: String = "calendar_preferences"
    
    /**
     * widget id별 데이터 저장
     */
    suspend fun saveData(context: Context, widgetId: Int, data: CalendarData) {
        context.calendarDataStore.edit { preferences ->
            preferences[getYearKey(widgetId)] = data.yearMonth.year
            preferences[getMonthKey(widgetId)] = data.yearMonth.month
            preferences[getCountryCodeKey(widgetId)] = data.countryCode
        }
    }
    
    /**
     * widget id별 데이터 로드
     */
    suspend fun loadData(context: Context, widgetId: Int): CalendarData {
        val preferences = context.calendarDataStore.data.first()
        val year = preferences[getYearKey(widgetId)]
        val month = preferences[getMonthKey(widgetId)]
        val countryCode = preferences[getCountryCodeKey(widgetId)] ?: "KR"
        
        return if (year != null && month != null) {
            CalendarData(
                yearMonth = CalendarDateUtils.YearMonth(year, month),
                countryCode = countryCode
            )
        } else {
            // 저장된 데이터가 없으면 현재 년/월 반환
            CalendarData.empty()
        }
    }
    
    /**
     * 기존 메서드 (호환성을 위해 유지, widget id 0 사용)
     * @deprecated widget id를 명시적으로 지정하는 saveData(context, widgetId, data) 사용 권장
     */
    @Deprecated("Use saveData(context, widgetId, data) instead", ReplaceWith("saveData(context, 0, data)"))
    override suspend fun saveData(context: Context, data: CalendarData) {
        saveData(context, 0, data)
    }
    
    /**
     * 기존 메서드 (호환성을 위해 유지, widget id 0 사용)
     * @deprecated widget id를 명시적으로 지정하는 loadData(context, widgetId) 사용 권장
     */
    @Deprecated("Use loadData(context, widgetId) instead", ReplaceWith("loadData(context, 0)"))
    override suspend fun loadData(context: Context): CalendarData {
        return loadData(context, 0)
    }
    
    override fun getDefaultData(): CalendarData {
        return CalendarData.empty()
    }
}

