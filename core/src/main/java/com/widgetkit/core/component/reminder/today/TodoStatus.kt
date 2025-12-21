package com.widgetkit.core.component.reminder.today

/**
 * Todo 상태를 나타내는 Enum
 */
enum class TodoStatus {
    /**
     * 완료된 상태
     */
    COMPLETED,
    
    /**
     * 미완료 상태
     */
    INCOMPLETE,
    
    /**
     * Pending 상태 - 과거 날짜의 미완료 항목 (해야 했지만 지나간 상태)
     */
    PENDING
}

