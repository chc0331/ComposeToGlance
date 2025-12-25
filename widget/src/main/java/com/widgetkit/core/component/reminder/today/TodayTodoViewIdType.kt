package com.widgetkit.core.component.reminder.today

import com.widgetkit.core.component.viewid.ViewIdType

/**
 * TodayTodo 위젯의 ViewId 타입 정의
 * 부분 업데이트를 위한 ViewId를 관리합니다.
 */
sealed class TodayTodoViewIdType(override val typeName: String) : ViewIdType() {

    object TitleDate : TodayTodoViewIdType("todo_title_date")
    object SelectedDate : TodayTodoViewIdType("todo_selected_date")
    object AllTodoNumber : TodayTodoViewIdType("all_todo_number")
    object CompletedTodoNumber : TodayTodoViewIdType("completed_todo_number")

    companion object {
        fun all(): List<TodayTodoViewIdType> = listOf(
            TitleDate, SelectedDate, AllTodoNumber, CompletedTodoNumber
        )
    }
}

