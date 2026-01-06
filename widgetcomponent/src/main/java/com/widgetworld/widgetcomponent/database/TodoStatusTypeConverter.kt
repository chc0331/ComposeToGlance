package com.widgetworld.widgetcomponent.database

import androidx.room.TypeConverter
import com.widgetworld.widgetcomponent.component.reminder.today.TodoStatus

/**
 * TodoStatus를 위한 Room TypeConverter
 */
class TodoStatusTypeConverter {
    
    @TypeConverter
    fun fromStatus(status: TodoStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toStatus(value: String): TodoStatus {
        return try {
            TodoStatus.valueOf(value)
        } catch (e: Exception) {
            TodoStatus.INCOMPLETE
        }
    }
}

