package com.widgetkit.widgetcomponent.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.widgetkit.widgetcomponent.component.reminder.today.TodoStatus
import kotlinx.coroutines.flow.Flow

/**
 * Todo 데이터 접근 객체
 */
@Dao
interface TodoDao {
    
    /**
     * 날짜별 Todo 조회 (생성 순서)
     */
    @Query("SELECT * FROM todos WHERE date = :date ORDER BY createdAt ASC")
    fun getTodosByDate(date: String): Flow<List<TodoEntity>>
    
    /**
     * ID로 Todo 조회
     */
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?
    
    /**
     * 모든 Todo 조회
     */
    @Query("SELECT * FROM todos ORDER BY date DESC, createdAt ASC")
    fun getAllTodos(): Flow<List<TodoEntity>>
    
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
     * Todo 상태 업데이트
     */
    @Query("UPDATE todos SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoStatus(
        id: Long,
        status: TodoStatus,
        updatedAt: Long = System.currentTimeMillis()
    )
    
    /**
     * 날짜별 완료된 Todo 개수
     */
    @Query("SELECT COUNT(*) FROM todos WHERE date = :date AND status = 'COMPLETED'")
    suspend fun getCompletedCountByDate(date: String): Int
    
    /**
     * 날짜별 전체 Todo 개수
     */
    @Query("SELECT COUNT(*) FROM todos WHERE date = :date")
    suspend fun getTotalCountByDate(date: String): Int
    
    /**
     * 날짜 범위별 Todo 조회 (yyyy-MM-dd 형식)
     */
    @Query("SELECT * FROM todos WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, createdAt ASC")
    fun getTodosByDateRange(startDate: String, endDate: String): Flow<List<TodoEntity>>
    
    /**
     * dateTime이 있는 미래 Todo 조회 (현재 시간 이후, 미완료 상태만)
     * dateTime 기준 오름차순 정렬
     */
    @Query("SELECT * FROM todos WHERE dateTime IS NOT NULL AND dateTime >= :currentTime AND status != 'COMPLETED' ORDER BY dateTime ASC")
    suspend fun getUpcomingTodos(currentTime: Long): List<TodoEntity>
    
    /**
     * 날짜 범위 기반 Upcoming Todos 조회 (dateTime null 포함)
     * - dateTime이 있는 경우: dateTime >= currentTime AND status != 'COMPLETED'
     * - dateTime이 null인 경우: date >= startDate AND date <= endDate AND status != 'COMPLETED'
     * 정렬은 애플리케이션 레벨에서 수행 (dateTime 우선, 그 다음 날짜 순)
     */
    @Query("""
        SELECT * FROM todos 
        WHERE status != 'COMPLETED' 
        AND (
            (dateTime IS NOT NULL AND dateTime >= :currentTime) 
            OR 
            (dateTime IS NULL AND date >= :startDate AND date <= :endDate)
        )
        ORDER BY date ASC, createdAt ASC
    """)
    suspend fun getUpcomingTodosByDateRange(
        currentTime: Long,
        startDate: String,
        endDate: String
    ): List<TodoEntity>
}

