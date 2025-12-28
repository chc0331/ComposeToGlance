package com.widgetkit.core.component.reminder.today.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetkit.core.component.reminder.today.TodoDateUtils
import com.widgetkit.core.component.reminder.today.TodoRepository
import com.widgetkit.core.component.reminder.today.TodoStatus
import com.widgetkit.core.component.reminder.today.TodayTodoUpdateManager
import com.widgetkit.core.database.TodoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * TodayTodo UI 상태
 */
data class TodayTodoUiState(
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val todos: List<TodoEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingTodo: TodoEntity? = null,
    val showDatePicker: Boolean = false,
    val inlineTitle: String = ""
) {
    val selectedDateString: String
        get() = TodoDateUtils.formatDateString(Date(selectedDateMillis))
    
    val headerDateText: String
        get() = TodoDateUtils.formatHeaderDate(Date(selectedDateMillis))
    
    val isToday: Boolean
        get() = TodoDateUtils.isToday(selectedDateString)
}

/**
 * TodayTodo ViewModel
 */
class TodayTodoViewModel(
    private val repository: TodoRepository,
    private val applicationContext: Context
) : ViewModel() {
    
    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    private val _showAddDialog = MutableStateFlow(false)
    private val _editingTodo = MutableStateFlow<TodoEntity?>(null)
    private val _showDatePicker = MutableStateFlow(false)
    private val _inlineTitle = MutableStateFlow("")
    
    // 선택된 날짜에 따른 Todo 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    private val todosFlow = _selectedDateMillis.flatMapLatest { dateMillis ->
        val dateString = TodoDateUtils.formatDateString(Date(dateMillis))
        repository.getTodosByDate(dateString)
    }
    
    // UI 상태를 combine (두 단계로 나눔 - combine은 최대 5개만 지원)
    private val baseUiState = combine(
        _selectedDateMillis,
        todosFlow,
        _showAddDialog,
        _editingTodo,
        _showDatePicker
    ) { dateMillis, todos, showAdd, editing, showDate ->
        TodayTodoUiState(
            selectedDateMillis = dateMillis,
            todos = todos,
            showAddDialog = showAdd,
            editingTodo = editing,
            showDatePicker = showDate,
            inlineTitle = ""
        )
    }
    
    val uiState: StateFlow<TodayTodoUiState> = combine(
        baseUiState,
        _inlineTitle
    ) { base, inlineTitle ->
        base.copy(inlineTitle = inlineTitle)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayTodoUiState()
    )
    
    /**
     * 이전 날짜로 이동
     */
    fun selectPreviousDate() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDateMillis.value
            add(Calendar.DAY_OF_YEAR, -1)
        }
        _selectedDateMillis.value = calendar.timeInMillis
    }
    
    /**
     * 다음 날짜로 이동
     */
    fun selectNextDate() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDateMillis.value
            add(Calendar.DAY_OF_YEAR, 1)
        }
        _selectedDateMillis.value = calendar.timeInMillis
    }
    
    /**
     * 특정 날짜 선택
     */
    fun selectDate(dateMillis: Long) {
        _selectedDateMillis.value = dateMillis
    }
    
    /**
     * 추가 다이얼로그 표시
     */
    fun showAddDialog() {
        _showAddDialog.value = true
    }
    
    /**
     * 추가 다이얼로그 숨김
     */
    fun hideAddDialog() {
        _showAddDialog.value = false
    }
    
    /**
     * 편집 다이얼로그 표시
     */
    fun showEditDialog(todo: TodoEntity) {
        _editingTodo.value = todo
    }
    
    /**
     * 편집 다이얼로그 숨김
     */
    fun hideEditDialog() {
        _editingTodo.value = null
    }
    
    /**
     * 날짜 피커 표시
     */
    fun showDatePicker() {
        _showDatePicker.value = true
    }
    
    /**
     * 날짜 피커 숨김
     */
    fun hideDatePicker() {
        _showDatePicker.value = false
    }
    
    /**
     * 인라인 제목 업데이트
     */
    fun updateInlineTitle(title: String) {
        _inlineTitle.value = title
    }
    
    /**
     * 인라인으로 Todo 추가 (간단 추가)
     */
    fun addTodoInline() {
        val title = _inlineTitle.value.trim()
        if (title.isEmpty()) return
        
        viewModelScope.launch {
            val currentState = uiState.value
            val newTodo = TodoEntity(
                title = title,
                date = currentState.selectedDateString,
                status = TodoStatus.INCOMPLETE
            )
            repository.insertTodo(newTodo)
            _inlineTitle.value = ""
            
            // 위젯 업데이트는 저장 버튼 클릭 시에만 수행
        }
    }
    
    /**
     * Todo 추가 (상세)
     */
    fun addTodo(title: String, description: String?, dateTime: Long?) {
        viewModelScope.launch {
            val currentState = uiState.value
            val date = if (dateTime != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(Date(dateTime))
            } else {
                currentState.selectedDateString
            }
            
            val newTodo = TodoEntity(
                title = title,
                description = description,
                date = date,
                dateTime = dateTime,
                status = TodoStatus.INCOMPLETE
            )
            repository.insertTodo(newTodo)
            _showAddDialog.value = false
            
            // 위젯 업데이트는 저장 버튼 클릭 시에만 수행
        }
    }
    
    /**
     * Todo 수정
     */
    fun updateTodo(todo: TodoEntity, title: String, description: String?, dateTime: Long?) {
        viewModelScope.launch {
            val date = if (dateTime != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(Date(dateTime))
            } else {
                todo.date
            }
            
            val updatedTodo = todo.copy(
                title = title,
                description = description,
                date = date,
                dateTime = dateTime,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTodo(updatedTodo)
            _editingTodo.value = null
            
            // 위젯 업데이트는 저장 버튼 클릭 시에만 수행
        }
    }
    
    /**
     * Todo 상태 토글
     */
    fun toggleTodoStatus(todo: TodoEntity) {
        viewModelScope.launch {
            val newStatus = when (todo.status) {
                TodoStatus.COMPLETED -> TodoStatus.INCOMPLETE
                TodoStatus.INCOMPLETE -> TodoStatus.COMPLETED
            }
            repository.toggleTodoStatus(todo.id, newStatus)
            
            // 위젯 업데이트는 저장 버튼 클릭 시에만 수행
        }
    }
    
    /**
     * Todo 삭제
     */
    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
            
            // 위젯 업데이트는 저장 버튼 클릭 시에만 수행
        }
    }
    
    /**
     * 위젯 업데이트
     * 저장 버튼 클릭 시 최종 데이터를 위젯에 반영합니다.
     */
    fun saveAndUpdateWidget() {
        viewModelScope.launch {
            // 위젯은 오늘 날짜의 Todo만 표시하므로 항상 오늘 날짜 기준으로 업데이트
            TodayTodoUpdateManager.syncComponentState(applicationContext)
        }
    }
}

