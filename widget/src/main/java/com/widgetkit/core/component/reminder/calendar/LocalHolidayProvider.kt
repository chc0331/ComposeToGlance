package com.widgetkit.core.component.reminder.calendar

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.widgetkit.core.component.reminder.calendar.api.HolidayResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Assets에 포함된 JSON 파일에서 공휴일 로드
 * 오프라인에서도 최소한의 공휴일을 제공합니다.
 */
object LocalHolidayProvider {
    
    private const val TAG = "LocalHolidayProvider"
    private val gson = Gson()
    
    /**
     * Assets에서 공휴일 데이터 로드
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드 (기본값: "KR")
     * @return 공휴일 목록
     */
    suspend fun loadHolidays(
        context: Context,
        year: Int,
        countryCode: String = "KR"
    ): List<HolidayResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "holidays/holidays_${countryCode.lowercase()}_$year.json"
                
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    val type = object : TypeToken<List<HolidayResponse>>() {}.type
                    val holidays = gson.fromJson<List<HolidayResponse>>(json, type)
                    
                    Log.d(TAG, "Loaded ${holidays.size} holidays from assets for $year-$countryCode")
                    holidays
                }
            } catch (e: IOException) {
                // 파일이 없으면 정상 (해당 연도 데이터가 assets에 없을 수 있음)
                Log.d(TAG, "Holidays file not found in assets for $year-$countryCode: ${e.message}")
                emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading holidays from assets for $year-$countryCode", e)
                emptyList()
            }
        }
    }
    
    /**
     * Assets에 해당 연도 파일이 있는지 확인
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드
     * @return 파일 존재 여부
     */
    fun hasHolidayFile(
        context: Context,
        year: Int,
        countryCode: String = "KR"
    ): Boolean {
        return try {
            val fileName = "holidays/holidays_${countryCode.lowercase()}_$year.json"
            context.assets.open(fileName).use { true }
        } catch (e: Exception) {
            false
        }
    }
}

