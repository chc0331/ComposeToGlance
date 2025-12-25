package com.widgetkit.core.database

import androidx.room.TypeConverter
import com.widgetkit.core.component.reminder.today.TodoStatus

/**
 * TodoStatus Enum과 String 간의 변환을 처리하는 TypeConverter
 */
class TodoStatusTypeConverter {

    @TypeConverter
    fun fromStatus(status: TodoStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(status: String): TodoStatus {
        return try {
            TodoStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            TodoStatus.INCOMPLETE // 기본값
        }
    }
}
