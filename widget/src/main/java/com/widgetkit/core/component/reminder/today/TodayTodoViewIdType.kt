package com.widgetkit.core.component.reminder.today

import com.widgetkit.core.component.viewid.ViewIdType

/**
 * TodayTodo 위젯 ViewId 타입
 */
sealed class TodayTodoViewIdType(override val typeName: String) : ViewIdType() {
    
    object TodoList : TodayTodoViewIdType("todo_list")
    object TaskCount : TodayTodoViewIdType("task_count")
    object DateText : TodayTodoViewIdType("date_text")
    
    companion object {
        fun all(): List<TodayTodoViewIdType> = listOf(
            TodoList,
            TaskCount,
            DateText
        )
    }
}

