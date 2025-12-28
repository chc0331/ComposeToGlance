package com.widgetkit.core.component.reminder.today

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Todo 날짜 관련 유틸리티
 */
object TodoDateUtils {
    
    /**
     * 오늘 날짜를 yyyy-MM-dd 형식으로 반환
     */
    fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        return formatCalendarToDateString(calendar)
    }
    
    /**
     * Date를 yyyy-MM-dd 형식 문자열로 변환
     */
    fun formatDateString(date: Date): String {
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        return formatCalendarToDateString(calendar)
    }
    
    /**
     * Calendar를 yyyy-MM-dd 형식 문자열로 변환
     */
    private fun formatCalendarToDateString(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
    
    /**
     * Milliseconds를 yyyy-MM-dd 형식 문자열로 변환 (UTC 타임존 고려)
     * DatePicker에서 반환된 UTC 기준 밀리초를 로컬 날짜로 변환
     */
    fun formatMillisToDateString(millis: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = millis
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
    
    /**
     * 위젯 헤더용 날짜 포맷 (예: "Dec 28")
     */
    fun formatWidgetDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
        return dateFormat.format(date)
    }
    
    /**
     * 시간 포맷 (예: "5:00 PM")
     */
    fun formatTime(dateTime: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        return dateFormat.format(Date(dateTime))
    }
    
    /**
     * 전체 날짜/시간 포맷 (예: "2024-12-28 17:00")
     */
    fun formatDateTime(dateTime: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(Date(dateTime))
    }
    
    /**
     * 헤더용 날짜 텍스트 (예: "12월 28일")
     */
    fun formatHeaderDate(date: Date): String {
        val dateFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)
        return dateFormat.format(date)
    }
    
    /**
     * 날짜 문자열을 Date로 변환 (로컬 타임존의 자정으로 설정)
     */
    fun parseDate(dateString: String): Date? {
        return try {
            val parts = dateString.split("-")
            if (parts.size != 3) return null
            
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1 // Calendar.MONTH는 0부터 시작
            val day = parts[2].toInt()
            
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 날짜 문자열을 UTC 기준 밀리초로 변환 (DatePicker용)
     */
    fun parseDateToUtcMillis(dateString: String): Long? {
        return try {
            val parts = dateString.split("-")
            if (parts.size != 3) return null
            
            val year = parts[0].toInt()
            val month = parts[1].toInt() - 1
            val day = parts[2].toInt()
            
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 특정 날짜가 오늘인지 확인
     */
    fun isToday(dateString: String): Boolean {
        return dateString == getTodayDateString()
    }
}

