package com.widgetkit.core.component.reminder.today

import com.widgetkit.core.database.TodoEntity

/**
 * TodayTodo 위젯에 표시할 데이터 모델
 * * @property todos 오늘 날짜의 Todo 리스트
 * @property date 날짜 문자열 (yyyy-MM-dd)
 * @property incompleteCount 미완료 Todo 개수
 * @property completedCount 완료된 Todo 개수
 */
data class TodayTodoData(
    val todos: List<TodoEntity>,
    val date: String, // yyyy-MM-dd
    val incompleteCount: Int,
    val completedCount: Int
) {
    companion object {
        /**
         * 빈 데이터 생성
         */
        fun empty(date: String = TodoDateUtils.getTodayDateString()): TodayTodoData {
            return TodayTodoData(
                todos = emptyList(),
                date = date,
                incompleteCount = 0,
                completedCount = 0
            )
        }

        /**
         * Todo 리스트로부터 데이터 생성
         */
        fun fromTodos(todos: List<TodoEntity>, date: String): TodayTodoData {
            val incompleteCount = todos.count { it.status != TodoStatus.COMPLETED }
            val completedCount = todos.count { it.status == TodoStatus.COMPLETED }
            return TodayTodoData(
                todos = todos,
                date = date,
                incompleteCount = incompleteCount,
                completedCount = completedCount
            )
        }
    }
}
