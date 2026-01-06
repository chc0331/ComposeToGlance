package com.widgetkit.widgetcomponent.component.reminder.calendar.api

import com.google.gson.annotations.SerializedName

/**
 * nager.date API 공휴일 응답 모델
 */
data class HolidayResponse(
    /**
     * 공휴일 날짜 (yyyy-MM-dd 형식)
     */
    val date: String,
    
    /**
     * 공휴일 이름 (로컬 언어)
     */
    @SerializedName("localName")
    val localName: String,
    
    /**
     * 공휴일 이름 (영어)
     */
    val name: String,
    
    /**
     * 국가 코드 (예: KR, US, JP)
     */
    @SerializedName("countryCode")
    val countryCode: String,
    
    /**
     * 고정 공휴일 여부
     */
    val fixed: Boolean,
    
    /**
     * 글로벌 공휴일 여부
     */
    val global: Boolean,
    
    /**
     * 카운티 코드 (선택적)
     */
    val counties: List<String>? = null,
    
    /**
     * 공휴일 유형 (예: Public, Bank, School, Authorities, Optional, Observance)
     */
    val types: List<String>? = null
)

