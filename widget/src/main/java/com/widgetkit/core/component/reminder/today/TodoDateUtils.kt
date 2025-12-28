package com.widgetkit.core.component.reminder.today

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Todo 날짜 관련 유틸리티
 */
object TodoDateUtils {
    
    /**
     * 오늘 날짜를 yyyy-MM-dd 형식으로 반환
     */
    fun getTodayDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * Date를 yyyy-MM-dd 형식 문자열로 변환
     */
    fun formatDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
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
     * 날짜 문자열을 Date로 변환
     */
    fun parseDate(dateString: String): Date? {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.parse(dateString)
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

