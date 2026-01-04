package com.widgetkit.core.component.reminder.calendar

import com.widgetkit.core.component.reminder.calendar.CalendarDateUtils.YearMonth

/**
 * Calendar 위젯 데이터 모델
 */
data class CalendarData(
    /**
     * 현재 표시 중인 년/월
     */
    val yearMonth: YearMonth,
    /**
     * 국가 코드 (공휴일 조회용, 기본값: "KR")
     */
    val countryCode: String = "KR"
) {
    companion object {
        /**
         * 빈 데이터 생성 (현재 년/월)
         */
        fun empty(): CalendarData {
            return CalendarData(
                yearMonth = CalendarDateUtils.getCurrentYearMonth(),
                countryCode = "KR"
            )
        }
        
        /**
         * 년/월로부터 데이터 생성
         */
        fun fromYearMonth(year: Int, month: Int, countryCode: String = "KR"): CalendarData {
            return CalendarData(
                yearMonth = YearMonth(year, month),
                countryCode = countryCode
            )
        }
    }
}

