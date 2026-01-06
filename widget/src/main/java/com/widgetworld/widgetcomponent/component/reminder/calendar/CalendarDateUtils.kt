package com.widgetworld.widgetcomponent.component.reminder.calendar

import com.widgetworld.widgetcomponent.component.reminder.today.TodoDateUtils
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 캘린더 위젯용 날짜 유틸리티
 */
object CalendarDateUtils {
    
    /**
     * 캘린더 그리드의 날짜 정보
     */
    data class CalendarDay(
        val date: String,  // yyyy-MM-dd
        val dayOfMonth: Int,
        val isCurrentMonth: Boolean,
        val isToday: Boolean
    )
    
    /**
     * 년/월 정보
     */
    data class YearMonth(
        val year: Int,
        val month: Int  // 1-12
    ) {
        /**
         * 이전 달
         */
        fun previousMonth(): YearMonth {
            return if (month == 1) {
                YearMonth(year - 1, 12)
            } else {
                YearMonth(year, month - 1)
            }
        }
        
        /**
         * 다음 달
         */
        fun nextMonth(): YearMonth {
            return if (month == 12) {
                YearMonth(year + 1, 1)
            } else {
                YearMonth(year, month + 1)
            }
        }
        
        /**
         * 현재 달인지 확인
         */
        fun isCurrentMonth(): Boolean {
            val now = Calendar.getInstance()
            return year == now.get(Calendar.YEAR) && month == now.get(Calendar.MONTH) + 1
        }
        
        /**
         * 월의 첫 날 날짜 문자열 (yyyy-MM-dd)
         */
        fun getFirstDayString(): String {
            return String.format("%04d-%02d-01", year, month)
        }
        
        /**
         * 월의 마지막 날 날짜 문자열 (yyyy-MM-dd)
         */
        fun getLastDayString(): String {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)  // Calendar.MONTH는 0부터 시작
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            return String.format("%04d-%02d-%02d", year, month, lastDay)
        }
        
        /**
         * 월 이름 표시 (예: "December 2024")
         */
        fun getDisplayName(locale: Locale = Locale.ENGLISH): String {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
            }
            val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
            return "$monthName $year"
        }
    }
    
    /**
     * 현재 년/월 가져오기
     */
    fun getCurrentYearMonth(): YearMonth {
        val calendar = Calendar.getInstance()
        return YearMonth(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1
        )
    }
    
    /**
     * 날짜 문자열로부터 YearMonth 생성
     */
    fun parseYearMonth(dateString: String): YearMonth? {
        return try {
            val parts = dateString.split("-")
            if (parts.size >= 2) {
                YearMonth(
                    year = parts[0].toInt(),
                    month = parts[1].toInt()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 캘린더 그리드 생성 (6주 x 7일)
     * 
     * @param yearMonth 표시할 년/월
     * @return 6주 x 7일 그리드 (42개 날짜)
     */
    fun generateCalendarGrid(yearMonth: YearMonth): List<List<CalendarDay>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, yearMonth.year)
            set(Calendar.MONTH, yearMonth.month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        
        // 월의 첫 날이 무슨 요일인지 (일요일 = 1, 토요일 = 7)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val startOffset = firstDayOfWeek - 1  // 일요일부터 시작하므로 0부터 시작
        
        // 월의 마지막 날
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // 이전 달의 마지막 날들
        val prevMonth = yearMonth.previousMonth()
        val prevCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, prevMonth.year)
            set(Calendar.MONTH, prevMonth.month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val prevLastDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // 다음 달의 첫 날들
        val nextMonth = yearMonth.nextMonth()
        
        val today = TodoDateUtils.getTodayDateString()
        val todayYearMonth = parseYearMonth(today) ?: getCurrentYearMonth()
        
        val grid = mutableListOf<List<CalendarDay>>()
        
        // 6주 생성
        for (week in 0 until 6) {
            val weekDays = mutableListOf<CalendarDay>()
            
            // 7일 생성
            for (day in 0 until 7) {
                val dayIndex = week * 7 + day
                val dayOfMonth: Int
                val isCurrentMonth: Boolean
                val dateString: String
                
                if (dayIndex < startOffset) {
                    // 이전 달
                    dayOfMonth = prevLastDay - (startOffset - dayIndex - 1)
                    dateString = String.format("%04d-%02d-%02d", prevMonth.year, prevMonth.month, dayOfMonth)
                    isCurrentMonth = false
                } else if (dayIndex < startOffset + lastDay) {
                    // 현재 달
                    dayOfMonth = dayIndex - startOffset + 1
                    dateString = String.format("%04d-%02d-%02d", yearMonth.year, yearMonth.month, dayOfMonth)
                    isCurrentMonth = true
                } else {
                    // 다음 달
                    dayOfMonth = dayIndex - startOffset - lastDay + 1
                    dateString = String.format("%04d-%02d-%02d", nextMonth.year, nextMonth.month, dayOfMonth)
                    isCurrentMonth = false
                }
                
                weekDays.add(
                    CalendarDay(
                        date = dateString,
                        dayOfMonth = dayOfMonth,
                        isCurrentMonth = isCurrentMonth,
                        isToday = dateString == today
                    )
                )
            }
            
            grid.add(weekDays)
        }
        
        return grid
    }
    
    /**
     * 요일 이름 배열 (일요일부터)
     */
    fun getWeekDayNames(locale: Locale = Locale.KOREAN): List<String> {
        val calendar = Calendar.getInstance(locale)
        val dayNames = mutableListOf<String>()
        
        // 일요일부터 시작
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        for (i in 0 until 7) {
            val dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale)
            dayNames.add(dayName ?: "")
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
        
        return dayNames
    }
}

