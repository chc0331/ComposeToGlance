package com.example.widget.component.reminder.today.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.widget.component.reminder.today.TodoDateUtils
import com.example.widget.component.reminder.today.TodoRepository
import com.example.widget.component.reminder.today.TodoStatus
import com.example.widget.database.TodoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * TodayTodo 화면의 UI 상태
 */
data class TodayTodoUiState(
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val todos: List<TodoEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingTodo: TodoEntity? = null,
    val showCalendarPicker: Boolean = false,
    val inlineTitle: String = ""
) {
    /**
     * 선택된 날짜 문자열 (yyyy-MM-dd)
     */
    val selectedDateString: String
        get() = TodoDateUtils.formatDateString(Date(selectedDateMillis))

    /**
     * 헤더용 날짜 텍스트 (예: 12월 17일 (수))
     */
    val headerDateText: String
        get() = TodoDateUtils.formatHeaderDate(Date(selectedDateMillis))

    /**
     * 오늘 날짜인지 여부
     */
    val isToday: Boolean
        get() = selectedDateString == TodoDateUtils.getTodayDateString()
}

/**
 * TodayTodo 화면의 ViewModel
 */
class TodayTodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    private val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _editingTodo = MutableStateFlow<TodoEntity?>(null)
    val editingTodo: StateFlow<TodoEntity?> = _editingTodo.asStateFlow()

    private val _showCalendarPicker = MutableStateFlow(false)
    val showCalendarPicker: StateFlow<Boolean> = _showCalendarPicker.asStateFlow()

    private val _inlineTitle = MutableStateFlow("")
    val inlineTitle: StateFlow<String> = _inlineTitle.asStateFlow()

    // 선택된 날짜에 따른 Todo 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    private val todosFlow = selectedDateMillis.flatMapLatest { dateMillis ->
        val dateString = TodoDateUtils.formatDateString(Date(dateMillis))
        repository.getTodosByDate(dateString)
    }

    // UI 상태를 combine하여 단일 StateFlow로 노출
    // combine은 최대 5개까지만 지원하므로 두 단계로 나눔
    private val baseUiState = combine(
        selectedDateMillis,
        todosFlow,
        showAddDialog,
        editingTodo,
        showCalendarPicker
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val dateMillis = values[0] as Long
        @Suppress("UNCHECKED_CAST")
        val todos = values[1] as List<TodoEntity>
        val addDialog = values[2] as Boolean
        @Suppress("UNCHECKED_CAST")
        val editTodo = values[3] as TodoEntity?
        val calendarPicker = values[4] as Boolean
        
        TodayTodoUiState(
            selectedDateMillis = dateMillis,
            todos = todos,
            showAddDialog = addDialog,
            editingTodo = editTodo,
            showCalendarPicker = calendarPicker,
            inlineTitle = _inlineTitle.value
        )
    }
    
    val uiState: StateFlow<TodayTodoUiState> = combine(
        baseUiState,
        inlineTitle
    ) { base, inlineTitleValue ->
        base.copy(inlineTitle = inlineTitleValue)
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
     * 캘린더 피커 표시
     */
    fun showCalendarPicker() {
        _showCalendarPicker.value = true
    }

    /**
     * 캘린더 피커 숨김
     */
    fun hideCalendarPicker() {
        _showCalendarPicker.value = false
    }

    /**
     * 인라인 제목 업데이트
     */
    fun updateInlineTitle(title: String) {
        _inlineTitle.value = title
    }

    /**
     * 인라인으로 Todo 추가
     */
    fun addTodoInline() {
        val title = _inlineTitle.value.trim()
        if (title.isEmpty()) return

        val currentState = uiState.value
        viewModelScope.launch {
            val newTodo = TodoEntity(
                title = title,
                description = null,
                date = currentState.selectedDateString,
                dateTime = null,
                status = TodoStatus.INCOMPLETE
            )
            repository.insertTodo(newTodo)
            _inlineTitle.value = ""
        }
    }

    /**
     * Todo 추가 (상세 다이얼로그에서)
     */
    fun addTodo(title: String, description: String, dateTime: Long?) {
        viewModelScope.launch {
            val currentState = uiState.value
            // dateTime이 있으면 해당 날짜를 사용, 없으면 선택된 날짜 사용
            val date = if (dateTime != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(Date(dateTime))
            } else {
                currentState.selectedDateString
            }
            val newTodo = TodoEntity(
                title = title,
                description = description.ifEmpty { null },
                date = date,
                dateTime = dateTime,
                status = TodoStatus.INCOMPLETE
            )
            repository.insertTodo(newTodo)
            _showAddDialog.value = false
        }
    }

    /**
     * Todo 수정
     */
    fun updateTodo(todo: TodoEntity, title: String, description: String, dateTime: Long?) {
        viewModelScope.launch {
            // dateTime이 있으면 해당 날짜를 사용, 없으면 기존 날짜 유지
            val date = if (dateTime != null) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(Date(dateTime))
            } else {
                todo.date
            }
            val updatedTodo = todo.copy(
                title = title,
                description = description.ifEmpty { null },
                date = date,
                dateTime = dateTime,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTodo(updatedTodo)
            _editingTodo.value = null
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
                TodoStatus.PENDING -> TodoStatus.COMPLETED
            }
            repository.toggleTodoStatus(todo.id, newStatus)
        }
    }

    /**
     * Todo 삭제
     */
    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }
}

