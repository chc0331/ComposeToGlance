package com.widgetworld.widgetcomponent.component.reminder.upcoming

import com.widgetworld.widgetcomponent.database.TodoEntity

/**
 * Upcoming Tasks 위젯 필터 타입
 */
enum class UpcomingFilterType {
    TODAY,      // 오늘
    TOMORROW,   // 내일
    THIS_WEEK,  // 이번 주
    ALL         // 전체
}

/**
 * Upcoming Tasks 위젯 데이터 모델
 */
data class UpcomingTasksData(
    val filterType: UpcomingFilterType,
    val todos: List<TodoEntity>
) {
    companion object {
        /**
         * 빈 데이터 생성
         */
        fun empty(filterType: UpcomingFilterType = UpcomingFilterType.TODAY): UpcomingTasksData {
            return UpcomingTasksData(
                filterType = filterType,
                todos = emptyList()
            )
        }
    }
}

