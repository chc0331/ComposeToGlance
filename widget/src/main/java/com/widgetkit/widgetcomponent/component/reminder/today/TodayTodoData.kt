package com.widgetkit.widgetcomponent.component.reminder.today

import com.widgetkit.widgetcomponent.database.TodoEntity

/**
 * TodayTodo 위젯 데이터 모델
 */
data class TodayTodoData(
    val selectedDate: String,  // yyyy-MM-dd
    val todos: List<TodoEntity>,
    val totalCount: Int,
    val completedCount: Int
) {
    companion object {
        /**
         * 빈 데이터 생성
         */
        fun empty(date: String = TodoDateUtils.getTodayDateString()): TodayTodoData {
            return TodayTodoData(
                selectedDate = date,
                todos = emptyList(),
                totalCount = 0,
                completedCount = 0
            )
        }
        
        /**
         * Todo 리스트로부터 데이터 생성
         */
        fun fromTodos(date: String, todos: List<TodoEntity>): TodayTodoData {
            val completed = todos.count { it.status == TodoStatus.COMPLETED }
            return TodayTodoData(
                selectedDate = date,
                todos = todos,
                totalCount = todos.size,
                completedCount = completed
            )
        }
    }
}

