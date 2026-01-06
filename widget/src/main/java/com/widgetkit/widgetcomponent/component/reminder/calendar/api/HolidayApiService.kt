package com.widgetkit.widgetcomponent.component.reminder.calendar.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * nager.date API 서비스 인터페이스
 */
interface HolidayApiService {
    
    /**
     * 특정 연도와 국가의 공휴일 목록 조회
     * 
     * @param year 연도 (예: 2026)
     * @param countryCode 국가 코드 (예: KR, US, JP)
     * @return 공휴일 목록
     */
    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): Response<List<HolidayResponse>>
}

