package com.widgetworld.widgetcomponent.component.reminder.today

import android.content.Context
import com.widgetworld.widgetcomponent.database.TodoDao
import com.widgetworld.widgetcomponent.database.TodoDatabase
import com.widgetworld.widgetcomponent.database.TodoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Todo 데이터 관리 Repository
 */
class TodoRepository(context: Context) {
    
    private val todoDao: TodoDao = TodoDatabase.getDatabase(context).todoDao()
    
    /**
     * 날짜별 Todo 조회
     */
    fun getTodosByDate(date: String): Flow<List<TodoEntity>> {
        return todoDao.getTodosByDate(date)
    }
    
    /**
     * 위젯 표시용 Todo 조회
     * - dateTime이 null인 Todo는 날짜에 관계없이 항상 포함 (별도 날짜/시간 선택 없이 추가한 항목)
     * - dateTime이 있는 Todo는 선택된 날짜와 일치하는 것만 포함
     */
    fun getTodosForWidget(date: String): Flow<List<TodoEntity>> {
        return todoDao.getTodosForWidget(date)
    }
    
    /**
     * 모든 Todo 조회
     */
    fun getAllTodos(): Flow<List<TodoEntity>> {
        return todoDao.getAllTodos()
    }
    
    /**
     * ID로 Todo 조회
     */
    suspend fun getTodoById(id: Long): TodoEntity? {
        return todoDao.getTodoById(id)
    }
    
    /**
     * Todo 추가
     */
    suspend fun insertTodo(todo: TodoEntity): Long {
        return todoDao.insertTodo(todo)
    }
    
    /**
     * Todo 수정
     */
    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo)
    }
    
    /**
     * Todo 삭제
     */
    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo)
    }
    
    /**
     * ID로 Todo 삭제
     */
    suspend fun deleteTodoById(id: Long) {
        todoDao.deleteTodoById(id)
    }
    
    /**
     * Todo 상태 토글
     */
    suspend fun toggleTodoStatus(id: Long, newStatus: TodoStatus) {
        todoDao.updateTodoStatus(id, newStatus)
    }
    
    /**
     * 날짜별 완료된 Todo 개수
     */
    suspend fun getCompletedCountByDate(date: String): Int {
        return todoDao.getCompletedCountByDate(date)
    }
    
    /**
     * 날짜별 전체 Todo 개수
     */
    suspend fun getTotalCountByDate(date: String): Int {
        return todoDao.getTotalCountByDate(date)
    }
    
    /**
     * 날짜 범위별 Todo 조회
     */
    fun getTodosByDateRange(startDate: String, endDate: String): Flow<List<TodoEntity>> {
        return todoDao.getTodosByDateRange(startDate, endDate)
    }
    
    /**
     * dateTime이 있는 미래 Todo 조회 (현재 시간 이후, 미완료 상태만)
     */
    suspend fun getUpcomingTodos(currentTime: Long = System.currentTimeMillis()): List<TodoEntity> {
        return todoDao.getUpcomingTodos(currentTime)
    }
    
    /**
     * 날짜 범위 기반 Upcoming Todos 조회 (dateTime null 포함)
     * - dateTime이 있는 경우: dateTime >= currentTime
     * - dateTime이 null인 경우: date가 startDate와 endDate 사이
     */
    suspend fun getUpcomingTodosByDateRange(
        currentTime: Long = System.currentTimeMillis(),
        startDate: String,
        endDate: String
    ): List<TodoEntity> {
        return todoDao.getUpcomingTodosByDateRange(currentTime, startDate, endDate)
    }
    
    /**
     * dateTime이 null인 모든 미완료 Todo 조회 (항상 표시되는 Todo)
     */
    suspend fun getTodosWithoutDateTime(): List<TodoEntity> {
        return todoDao.getTodosWithoutDateTime()
    }
    
    /**
     * 특정 날짜의 미완료 Todo 조회 (suspend 함수)
     */
    suspend fun getTodosByDateSync(date: String): List<TodoEntity> {
        return todoDao.getTodosByDateSync(date)
    }
}

