package com.widgetkit.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.widgetkit.core.component.reminder.today.TodoStatus

/**
 * Todo를 저장하는 Room Entity
 */
@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Todo 제목
     */
    val title: String,

    /**
     * Todo 설명 (선택사항)
     */
    val description: String? = null,

    /**
     * 날짜 (yyyy-MM-dd 형식)
     */
    val date: String,

    /**
     * 날짜와 시간 (밀리초 timestamp, 선택사항)
     * null이면 날짜만 저장된 것으로 간주
     */
    val dateTime: Long? = null,

    /**
     * Todo 상태
     */
    val status: TodoStatus,

    /**
     * 생성 시간 (밀리초)
     */
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * 수정 시간 (밀리초)
     */
    val updatedAt: Long = System.currentTimeMillis()
)
