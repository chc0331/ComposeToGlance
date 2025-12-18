package com.example.widget.component.reminder.today

import com.example.widget.component.viewid.ViewIdType

/**
 * TodayTodo 위젯의 ViewId 타입 정의
 * 부분 업데이트를 위한 ViewId를 관리합니다.
 */
sealed class TodayTodoViewIdType(override val typeName: String) : ViewIdType() {
    /**
     * 완료/미완료 개수를 표시하는 텍스트
     */
    object CountText : TodayTodoViewIdType("today_todo_count_text")
    
    /**
     * 헤더 날짜 텍스트
     */
    object HeaderText : TodayTodoViewIdType("today_todo_header_text")
    
    companion object {
        fun all(): List<TodayTodoViewIdType> = listOf(CountText, HeaderText)
    }
}

