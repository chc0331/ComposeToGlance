package com.widgetkit.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.widgetkit.core.component.reminder.today.TodoStatus
import kotlinx.coroutines.flow.Flow

/**
 * TodoEntity에 대한 데이터 접근 객체
 */
@Dao
interface TodoDao {

    /**
     * 날짜별 Todo 조회
     */
    @Query("SELECT * FROM todos WHERE date = :date ORDER BY createdAt ASC")
    fun getTodosByDate(date: String): Flow<List<TodoEntity>>

    /**
     * ID로 Todo 조회
     */
    @Query("SELECT * FROM todos WHERE id = :id")
    fun getTodoById(id: Long): Flow<TodoEntity?>

    /**
     * Todo 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    /**
     * Todo 수정
     */
    @Update
    suspend fun updateTodo(todo: TodoEntity)

    /**
     * Todo 삭제
     */
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    /**
     * ID로 Todo 삭제
     */
    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long)

    /**
     * Todo 상태 토글
     */
    @Query("UPDATE todos SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun toggleTodoStatus(
        id: Long,
        status: TodoStatus,
        updatedAt: Long = System.currentTimeMillis()
    )
}
