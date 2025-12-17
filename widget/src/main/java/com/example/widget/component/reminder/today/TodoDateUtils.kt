package com.example.widget.component.reminder.today

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Todo 날짜 관련 유틸리티 함수들
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
     * yyyy-MM-dd 형식 문자열을 반환
     */
    fun formatDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * 헤더용 로컬라이즈된 날짜 텍스트(예: 12월 17일 (수))
     */
    fun formatHeaderDate(date: Date): String {
        val dateFormat = SimpleDateFormat("M월 d일 (E)", Locale.getDefault())
        return dateFormat.format(date)
    }
}

