package com.widgetkit.widgetcomponent.component.reminder.calendar

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.widgetkit.widgetcomponent.component.reminder.calendar.api.HolidayResponse
import kotlinx.coroutines.flow.first
import java.util.Calendar

/**
 * 공휴일 데이터 저장소
 * DataStore를 사용하여 공휴일 데이터를 캐싱합니다.
 */
private val Context.holidayDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "holiday_preferences"
)

object HolidayDataStore {
    
    private const val TAG = "HolidayDataStore"
    private val gson = Gson()
    
    /**
     * 공휴일 데이터 저장
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드
     * @param holidays 공휴일 목록
     */
    suspend fun saveHolidays(
        context: Context,
        year: Int,
        countryCode: String,
        holidays: List<HolidayResponse>
    ) {
        try {
            val key = getKey(year, countryCode)
            val json = gson.toJson(holidays)
            
            context.holidayDataStore.edit { preferences ->
                preferences[key] = json
            }
            
            Log.d(TAG, "Saved holidays for $year-$countryCode: ${holidays.size} holidays")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving holidays for $year-$countryCode", e)
        }
    }
    
    /**
     * 공휴일 데이터 로드
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드
     * @return 공휴일 목록, 없으면 null
     */
    suspend fun loadHolidays(
        context: Context,
        year: Int,
        countryCode: String
    ): List<HolidayResponse>? {
        return try {
            val key = getKey(year, countryCode)
            val preferences = context.holidayDataStore.data.first()
            val json = preferences[key]
            
            if (json != null) {
                val type = object : TypeToken<List<HolidayResponse>>() {}.type
                val holidays = gson.fromJson<List<HolidayResponse>>(json, type)
                Log.d(TAG, "Loaded holidays for $year-$countryCode: ${holidays.size} holidays")
                holidays
            } else {
                Log.d(TAG, "No cached holidays for $year-$countryCode")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading holidays for $year-$countryCode", e)
            null
        }
    }
    
    /**
     * 오래된 공휴일 데이터 정리
     * 현재 연도 기준으로 2년 이전 데이터 삭제
     * 
     * @param context Context
     */
    suspend fun clearOldHolidays(context: Context) {
        try {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val cutoffYear = currentYear - 2
            
            context.holidayDataStore.edit { preferences ->
                val keysToRemove = mutableListOf<Preferences.Key<*>>()
                
                preferences.asMap().forEach { (key, _) ->
                    if (key is Preferences.Key<*>) {
                        val keyName = key.name
                        // 키 형식: "holiday_{year}_{countryCode}"
                        if (keyName.startsWith("holiday_")) {
                            val parts = keyName.split("_")
                            if (parts.size >= 2) {
                                val year = parts[1].toIntOrNull()
                                if (year != null && year < cutoffYear) {
                                    keysToRemove.add(key)
                                }
                            }
                        }
                    }
                }
                
                keysToRemove.forEach { key ->
                    preferences.remove(key as Preferences.Key<String>)
                }
            }
            
            Log.d(TAG, "Cleared old holidays before $cutoffYear")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing old holidays", e)
        }
    }
    
    /**
     * 특정 연도/국가의 공휴일 데이터 삭제
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드
     */
    suspend fun clearHolidays(
        context: Context,
        year: Int,
        countryCode: String
    ) {
        try {
            val key = getKey(year, countryCode)
            context.holidayDataStore.edit { preferences ->
                preferences.remove(key)
            }
            Log.d(TAG, "Cleared holidays for $year-$countryCode")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing holidays for $year-$countryCode", e)
        }
    }
    
    /**
     * DataStore 키 생성
     * 
     * @param year 연도
     * @param countryCode 국가 코드
     * @return Preferences.Key
     */
    private fun getKey(year: Int, countryCode: String): Preferences.Key<String> {
        return stringPreferencesKey("holiday_${year}_${countryCode}")
    }
}

