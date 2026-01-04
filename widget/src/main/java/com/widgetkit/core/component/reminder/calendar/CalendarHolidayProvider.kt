package com.widgetkit.core.component.reminder.calendar

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.util.Log
import androidx.core.content.ContextCompat
import com.widgetkit.core.component.reminder.calendar.api.HolidayResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Android CalendarProvider를 통한 공휴일 조회
 * 시스템 캘린더에 저장된 공휴일 이벤트를 조회합니다.
 */
object CalendarHolidayProvider {
    
    private const val TAG = "CalendarHolidayProvider"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    /**
     * CalendarProvider에서 공휴일 조회
     * 
     * @param context Context
     * @param year 연도
     * @param countryCode 국가 코드 (현재는 KR만 지원)
     * @return 공휴일 목록
     */
    fun loadHolidays(
        context: Context,
        year: Int,
        countryCode: String = "KR"
    ): List<HolidayResponse> {
        // 권한 체크
        if (!hasReadCalendarPermission(context)) {
            Log.d(TAG, "READ_CALENDAR permission not granted")
            return emptyList()
        }
        
        return try {
            val holidays = mutableListOf<HolidayResponse>()
            
            // 연도의 시작과 끝 시간 계산
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startMillis = calendar.timeInMillis
            
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
            calendar.set(Calendar.DAY_OF_MONTH, 31)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endMillis = calendar.timeInMillis
            
            // CalendarProvider 쿼리
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_TIMEZONE
            )
            
            val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
            val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())
            
            context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)) ?: ""
                    val dtStart = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                    val allDay = cursor.getInt(cursor.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1
                    
                    // 공휴일로 보이는 이벤트만 필터링
                    if (isHolidayEvent(title, description, countryCode)) {
                        val dateString = formatDate(dtStart)
                        if (dateString != null) {
                            holidays.add(
                                HolidayResponse(
                                    date = dateString,
                                    localName = title,
                                    name = title, // 영어 이름이 없으면 로컬 이름 사용
                                    countryCode = countryCode,
                                    fixed = true, // CalendarProvider에서는 고정 여부를 알 수 없으므로 true로 설정
                                    global = false
                                )
                            )
                        }
                    }
                }
            }
            
            Log.d(TAG, "Loaded ${holidays.size} holidays from CalendarProvider for $year-$countryCode")
            holidays
        } catch (e: Exception) {
            Log.e(TAG, "Error loading holidays from CalendarProvider for $year-$countryCode", e)
            emptyList()
        }
    }
    
    /**
     * READ_CALENDAR 권한이 있는지 확인
     */
    private fun hasReadCalendarPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 이벤트가 공휴일인지 판단
     * 제목이나 설명에 공휴일 관련 키워드가 포함되어 있는지 확인
     */
    private fun isHolidayEvent(title: String, description: String, countryCode: String): Boolean {
        val titleLower = title.lowercase(Locale.getDefault())
        val descLower = description.lowercase(Locale.getDefault())
        
        // 한국 공휴일 키워드
        if (countryCode == "KR") {
            val koreanHolidayKeywords = listOf(
                "신정", "설날", "삼일절", "어린이날", "부처님오신날",
                "현충일", "광복절", "추석", "개천절", "한글날", "크리스마스",
                "new year", "seollal", "independence", "children", "buddha",
                "memorial", "liberation", "chuseok", "foundation", "hangul", "christmas",
                "공휴일", "휴일", "holiday"
            )
            
            return koreanHolidayKeywords.any { keyword ->
                titleLower.contains(keyword) || descLower.contains(keyword)
            }
        }
        
        // 다른 국가는 기본 키워드로 판단
        val commonHolidayKeywords = listOf(
            "holiday", "public holiday", "national holiday", "festival"
        )
        
        return commonHolidayKeywords.any { keyword ->
            titleLower.contains(keyword) || descLower.contains(keyword)
        }
    }
    
    /**
     * 밀리초 타임스탬프를 yyyy-MM-dd 형식으로 변환
     */
    private fun formatDate(millis: Long): String? {
        return try {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = millis
            }
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date: $millis", e)
            null
        }
    }
}

