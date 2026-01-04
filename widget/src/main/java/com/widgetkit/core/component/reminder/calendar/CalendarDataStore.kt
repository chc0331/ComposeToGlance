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
 */
private val Context.calendarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "calendar_preferences"
)

private object CalendarPreferenceKeys {
    val YEAR = intPreferencesKey("year")
    val MONTH = intPreferencesKey("month")
    val COUNTRY_CODE = stringPreferencesKey("country_code")
}

object CalendarDataStore : ComponentDataStore<CalendarData>() {
    
    override val datastoreName: String = "calendar_preferences"
    
    override suspend fun saveData(context: Context, data: CalendarData) {
        context.calendarDataStore.edit { preferences ->
            preferences[CalendarPreferenceKeys.YEAR] = data.yearMonth.year
            preferences[CalendarPreferenceKeys.MONTH] = data.yearMonth.month
            preferences[CalendarPreferenceKeys.COUNTRY_CODE] = data.countryCode
        }
    }
    
    override suspend fun loadData(context: Context): CalendarData {
        val preferences = context.calendarDataStore.data.first()
        val year = preferences[CalendarPreferenceKeys.YEAR]
        val month = preferences[CalendarPreferenceKeys.MONTH]
        val countryCode = preferences[CalendarPreferenceKeys.COUNTRY_CODE] ?: "KR"
        
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
    
    override fun getDefaultData(): CalendarData {
        return CalendarData.empty()
    }
}

