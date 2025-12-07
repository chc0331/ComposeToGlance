package com.example.widget.component

/**
 * Battery 컴포넌트가 사용하는 View ID 타입들
 */
sealed class BatteryViewIdType(override val typeName: String) : ViewIdType() {
    /**
     * 배터리 텍스트 뷰 (퍼센트 표시)
     */
    object Text : BatteryViewIdType("battery_text")
    
    /**
     * 배터리 프로그레스 뷰 (원형 게이지)
     */
    object Progress : BatteryViewIdType("battery_progress")
    
    /**
     * 배터리 아이콘 뷰
     */
    object Icon : BatteryViewIdType("battery_icon")
    
    /**
     * 충전 중 아이콘 뷰
     */
    object ChargingIcon : BatteryViewIdType("charging_icon")
    
    companion object {
        /**
         * 모든 View ID 타입 반환
         */
        fun all(): List<BatteryViewIdType> = listOf(Text, Progress, Icon, ChargingIcon)
    }
}
