package com.example.widget.component

/**
 * BluetoothBattery 컴포넌트가 사용하는 View ID 타입들
 */
sealed class BluetoothBatteryViewIdType(override val typeName: String) : ViewIdType() {
    /**
     * 이어버즈 배터리 텍스트 뷰
     */
    object EarBudsText : BluetoothBatteryViewIdType("bt_earbuds_text")
    
    /**
     * 이어버즈 배터리 프로그레스 뷰
     */
    object EarBudsProgress : BluetoothBatteryViewIdType("bt_earbuds_progress")
    
    /**
     * 워치 배터리 텍스트 뷰
     */
    object WatchText : BluetoothBatteryViewIdType("bt_watch_text")
    
    /**
     * 워치 배터리 프로그레스 뷰
     */
    object WatchProgress : BluetoothBatteryViewIdType("bt_watch_progress")
    
    companion object {
        /**
         * 모든 View ID 타입 반환
         */
        fun all(): List<ViewIdType> = listOf(
            EarBudsText, 
            EarBudsProgress, 
            WatchText, 
            WatchProgress
        )
    }
}
