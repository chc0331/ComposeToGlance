package com.widgetkit.core.component.reminder.today

import android.content.Context
import com.widgetkit.core.database.TodoDao
import com.widgetkit.core.database.TodoDatabase
import com.widgetkit.core.database.TodoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Todo 데이터를 관리하는 Repository
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
     * ID로 Todo 조회
     */
    fun getTodoById(id: Long): Flow<TodoEntity?> {
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
    suspend fun toggleTodoStatus(id: Long, status: TodoStatus) {
        todoDao.toggleTodoStatus(id, status)
    }
}
