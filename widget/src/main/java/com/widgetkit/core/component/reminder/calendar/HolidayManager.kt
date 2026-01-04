package com.widgetkit.core.component.reminder.calendar

import android.content.Context
import android.util.Log
import com.widgetkit.core.component.reminder.calendar.api.HolidayApiClient
import com.widgetkit.core.component.reminder.calendar.api.HolidayResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 공휴일 데이터 관리자
 * 여러 데이터 소스를 순차적으로 시도하는 fallback 메커니즘을 제공합니다.
 * 
 * 우선순위:
 * 1. nager.date API
 * 2. CalendarProvider (시스템 캘린더)
 * 3. Assets JSON 파일 (로컬 데이터)
 */
object HolidayManager {
    
    private const val TAG = "HolidayManager"
    
    /**
     * 공휴일 데이터 로드 (Fallback 메커니즘 포함)
     * 여러 데이터 소스를 순차적으로 시도합니다:
     * 1. 캐시된 데이터 (ignoreCache가 false인 경우)
     * 2. nager.date API
     * 3. CalendarProvider (시스템 캘린더)
     * 4. Assets JSON 파일
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드 (기본값: "KR")
     * @param ignoreCache 캐시를 무시하고 강제로 새로고침할지 여부 (기본값: false)
     * @return 공휴일 목록
     */
    suspend fun loadHolidays(
        context: Context,
        year: Int,
        countryCode: String = "KR",
        ignoreCache: Boolean = false
    ): List<HolidayResponse> {
        return withContext(Dispatchers.IO) {
            // 1. 캐시에서 로드 시도 (ignoreCache가 false인 경우만)
            if (!ignoreCache) {
                val cachedHolidays = HolidayDataStore.loadHolidays(context, year, countryCode)
                if (cachedHolidays != null && cachedHolidays.isNotEmpty()) {
                    Log.d(TAG, "Using cached holidays for $year-$countryCode")
                    return@withContext cachedHolidays
                }
            } else {
                Log.d(TAG, "Ignoring cache for $year-$countryCode, fetching fresh data")
            }
            
            // 2. nager.date API 시도
            val apiHolidays = loadHolidaysFromApi(context, year, countryCode)
            if (apiHolidays.isNotEmpty()) {
                return@withContext apiHolidays
            }
            
            // 3. CalendarProvider 시도
            val calendarHolidays = CalendarHolidayProvider.loadHolidays(context, year, countryCode)
            if (calendarHolidays.isNotEmpty()) {
                Log.d(TAG, "Using holidays from CalendarProvider for $year-$countryCode")
                
                // 한국 공휴일인 경우 설날 데이터 보완 시도
                val supplementedCalendarHolidays = if (countryCode == "KR") {
                    supplementSeollalFromAssets(context, calendarHolidays, year, countryCode)
                } else {
                    calendarHolidays
                }
                
                // CalendarProvider에서 가져온 데이터도 캐시에 저장
                HolidayDataStore.saveHolidays(context, year, countryCode, supplementedCalendarHolidays)
                return@withContext supplementedCalendarHolidays
            }
            
            // 4. Assets JSON 파일 시도
            val localHolidays = LocalHolidayProvider.loadHolidays(context, year, countryCode)
            if (localHolidays.isNotEmpty()) {
                Log.d(TAG, "Using holidays from assets for $year-$countryCode")
                // Assets에서 가져온 데이터도 캐시에 저장
                HolidayDataStore.saveHolidays(context, year, countryCode, localHolidays)
                return@withContext localHolidays
            }
            
            // 모든 소스 실패
            Log.w(TAG, "No holidays found from any source for $year-$countryCode")
            emptyList()
        }
    }
    
    /**
     * nager.date API에서 공휴일 로드
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드
     * @return 공휴일 목록 (실패 시 빈 리스트)
     */
    private suspend fun loadHolidaysFromApi(
        context: Context,
        year: Int,
        countryCode: String
    ): List<HolidayResponse> {
        return try {
            Log.d(TAG, "Fetching holidays from API for $year-$countryCode")
            val response = HolidayApiClient.service.getPublicHolidays(year, countryCode)
            
            if (response.isSuccessful && response.body() != null) {
                val holidays = response.body()!!
                
                // 설날 관련 공휴일 확인 및 로깅
                val seollalHolidays = holidays.filter { 
                    it.localName.contains("설날", ignoreCase = true) || 
                    it.name.contains("Seollal", ignoreCase = true)
                }
                
                if (seollalHolidays.isNotEmpty()) {
                    Log.d(TAG, "Found ${seollalHolidays.size} Seollal holidays in API response: ${seollalHolidays.map { "${it.date} (${it.localName})" }}")
                } else {
                    Log.w(TAG, "No Seollal holidays found in API response for $year-$countryCode. Total holidays: ${holidays.size}")
                    // 공휴일 목록 로깅 (디버깅용)
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Holidays from API: ${holidays.map { "${it.date} - ${it.localName}" }}")
                    }
                    
                    // 한국 공휴일인 경우 설날 데이터 보완 시도
                    if (countryCode == "KR") {
                        val supplementedHolidays = supplementSeollalFromAssets(context, holidays, year, countryCode)
                        if (supplementedHolidays.size > holidays.size) {
                            Log.d(TAG, "Supplemented ${supplementedHolidays.size - holidays.size} Seollal holidays from assets")
                            // 보완된 데이터 캐시에 저장
                            HolidayDataStore.saveHolidays(context, year, countryCode, supplementedHolidays)
                            Log.d(TAG, "Fetched and cached ${supplementedHolidays.size} holidays (with Seollal supplement) for $year-$countryCode")
                            return supplementedHolidays
                        }
                    }
                }
                
                // 캐시에 저장
                HolidayDataStore.saveHolidays(context, year, countryCode, holidays)
                Log.d(TAG, "Fetched and cached ${holidays.size} holidays from API for $year-$countryCode")
                holidays
            } else {
                // 404는 해당 연도 데이터가 아직 없는 경우일 수 있음 (정상)
                if (response.code() == 404) {
                    Log.d(TAG, "Holidays not available from API for $year-$countryCode (404 - data may not be available yet)")
                } else {
                    Log.w(TAG, "API call failed for $year-$countryCode: ${response.code()}")
                }
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading holidays from API for $year-$countryCode", e)
            emptyList()
        }
    }
    
    /**
     * 공휴일 데이터 강제 새로고침
     * 캐시를 무시하고 모든 소스에서 최신 데이터를 시도합니다.
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드 (기본값: "KR")
     * @return 공휴일 목록
     */
    suspend fun refreshHolidays(
        context: Context,
        year: Int,
        countryCode: String = "KR"
    ): List<HolidayResponse> {
        return withContext(Dispatchers.IO) {
            // 1. API에서 새로고침 시도
            val apiHolidays = loadHolidaysFromApi(context, year, countryCode)
            if (apiHolidays.isNotEmpty()) {
                return@withContext apiHolidays
            }
            
            // 2. CalendarProvider 시도
            val calendarHolidays = CalendarHolidayProvider.loadHolidays(context, year, countryCode)
            if (calendarHolidays.isNotEmpty()) {
                HolidayDataStore.saveHolidays(context, year, countryCode, calendarHolidays)
                return@withContext calendarHolidays
            }
            
            // 3. Assets JSON 파일 시도
            val localHolidays = LocalHolidayProvider.loadHolidays(context, year, countryCode)
            if (localHolidays.isNotEmpty()) {
                HolidayDataStore.saveHolidays(context, year, countryCode, localHolidays)
                return@withContext localHolidays
            }
            
            // 4. 모든 소스 실패 시 기존 캐시 반환
            HolidayDataStore.loadHolidays(context, year, countryCode) ?: emptyList()
        }
    }
    
    /**
     * 특정 날짜의 공휴일 조회
     * 
     * @param holidays 공휴일 목록
     * @param dateString 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 공휴일 정보, 없으면 null
     */
    fun getHolidayForDate(
        holidays: List<HolidayResponse>,
        dateString: String
    ): HolidayResponse? {
        return holidays.firstOrNull { it.date == dateString }
    }
    
    /**
     * 공휴일 목록을 날짜별 맵으로 변환
     * 
     * @param holidays 공휴일 목록
     * @return 날짜를 키로 하는 맵
     */
    fun holidaysToMap(holidays: List<HolidayResponse>): Map<String, HolidayResponse> {
        return holidays.associateBy { it.date }
    }
    
    /**
     * 여러 연도의 공휴일을 한 번에 로드
     * 
     * @param context Context
     * @param years 연도 목록
     * @param countryCode 국가 코드 (기본값: "KR")
     * @return 연도별 공휴일 맵
     */
    suspend fun loadHolidaysForYears(
        context: Context,
        years: List<Int>,
        countryCode: String = "KR"
    ): Map<Int, List<HolidayResponse>> {
        return withContext(Dispatchers.IO) {
            years.associateWith { year ->
                loadHolidays(context, year, countryCode)
            }
        }
    }
    
    /**
     * Assets에서 설날 데이터를 가져와 기존 공휴일 목록에 보완
     * API 응답에 설날이 없을 경우 Assets 파일에서 설날 데이터를 추가합니다.
     * 
     * @param context Context
     * @param existingHolidays 기존 공휴일 목록
     * @param year 연도
     * @param countryCode 국가 코드
     * @return 보완된 공휴일 목록
     */
    private suspend fun supplementSeollalFromAssets(
        context: Context,
        existingHolidays: List<HolidayResponse>,
        year: Int,
        countryCode: String
    ): List<HolidayResponse> {
        return try {
            // Assets에서 공휴일 로드
            val assetsHolidays = LocalHolidayProvider.loadHolidays(context, year, countryCode)
            
            // Assets에서 설날 관련 공휴일만 필터링
            val seollalFromAssets = assetsHolidays.filter { 
                it.localName.contains("설날", ignoreCase = true) || 
                it.name.contains("Seollal", ignoreCase = true)
            }
            
            if (seollalFromAssets.isEmpty()) {
                Log.d(TAG, "No Seollal holidays found in assets for $year-$countryCode")
                return existingHolidays
            }
            
            // 기존 공휴일 목록에 설날 추가 (중복 제거)
            val existingDates = existingHolidays.map { it.date }.toSet()
            val newSeollalHolidays = seollalFromAssets.filter { it.date !in existingDates }
            
            if (newSeollalHolidays.isNotEmpty()) {
                Log.d(TAG, "Adding ${newSeollalHolidays.size} Seollal holidays from assets: ${newSeollalHolidays.map { "${it.date} (${it.localName})" }}")
                existingHolidays + newSeollalHolidays
            } else {
                existingHolidays
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error supplementing Seollal from assets for $year-$countryCode", e)
            existingHolidays
        }
    }
}

