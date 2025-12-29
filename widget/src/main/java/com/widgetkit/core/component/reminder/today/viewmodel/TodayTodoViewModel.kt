package com.widgetkit.core.component.reminder.today.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetkit.core.component.reminder.today.TodayTodoUpdateManager
import com.widgetkit.core.component.reminder.today.TodoDateUtils
import com.widgetkit.core.component.reminder.today.TodoRepository
import com.widgetkit.core.component.reminder.today.TodoStatus
import com.widgetkit.core.component.reminder.today.ui.SpeechRecognitionManager
import com.widgetkit.core.database.TodoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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
    val inlineTitle: String = "",
    val isListening: Boolean = false,
    val speechError: String? = null
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
    private val _isListening = MutableStateFlow(false)
    private val _speechError = MutableStateFlow<String?>(null)

    private var speechRecognitionManager: SpeechRecognitionManager? = null

    init {
        // DataStore에서 선택된 날짜 로드
        viewModelScope.launch {
            try {
                val dataStore = com.widgetkit.core.component.reminder.today.TodayTodoDataStore
                val data = dataStore.loadData(applicationContext)
                val selectedDate = data.selectedDate
                
                // 날짜 문자열을 milliseconds로 변환
                val dateMillis = TodoDateUtils.parseDate(selectedDate)?.time 
                    ?: System.currentTimeMillis()
                
                _selectedDateMillis.value = dateMillis
            } catch (e: Exception) {
                // 에러 발생 시 오늘 날짜 사용
                _selectedDateMillis.value = System.currentTimeMillis()
            }
        }

        // SpeechRecognitionManager 초기화
        initializeSpeechRecognition()
    }

    /**
     * Speech Recognition Manager 초기화
     */
    private fun initializeSpeechRecognition() {
        speechRecognitionManager = SpeechRecognitionManager(
            context = applicationContext,
            onResult = { recognizedText ->
                viewModelScope.launch {
                    val trimmedText = recognizedText.trim()
                    if (trimmedText.isNotEmpty()) {
                        _inlineTitle.value = trimmedText
                        _isListening.value = false
                        _speechError.value = null
                        
                        // 음성 인식된 텍스트를 자동으로 Todo 항목으로 추가
                        val currentState = uiState.value
                        val newTodo = TodoEntity(
                            title = trimmedText,
                            date = currentState.selectedDateString,
                            status = TodoStatus.INCOMPLETE
                        )
                        repository.insertTodo(newTodo)
                        _inlineTitle.value = "" // 추가 후 입력 필드 초기화
                    } else {
                        _isListening.value = false
                        _speechError.value = "인식된 텍스트가 없습니다"
                    }
                }
            },
            onError = { errorMessage ->
                viewModelScope.launch {
                    _isListening.value = false
                    _speechError.value = errorMessage
                }
            }
        )
    }

    // 선택된 날짜에 따른 Todo 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    private val todosFlow = _selectedDateMillis.flatMapLatest { dateMillis ->
        val dateString = TodoDateUtils.formatDateString(Date(dateMillis))
        repository.getTodosByDate(dateString)
    }

    // UI 상태를 combine (여러 단계로 나눔 - combine은 최대 5개만 지원)
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
            inlineTitle = "",
            isListening = false,
            speechError = null
        )
    }

    private val intermediateUiState = combine(
        baseUiState,
        _inlineTitle,
        _isListening
    ) { base, inlineTitle, isListening ->
        base.copy(inlineTitle = inlineTitle, isListening = isListening)
    }

    val uiState: StateFlow<TodayTodoUiState> = combine(
        intermediateUiState,
        _speechError
    ) { intermediate, speechError ->
        intermediate.copy(speechError = speechError)
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
            coroutineScope {
                // 현재 선택된 날짜를 DataStore에 저장하고 위젯 업데이트
                val currentDate = uiState.value.selectedDateString
                val data = TodayTodoUpdateManager.loadTodosForDate(applicationContext, currentDate)
                com.widgetkit.core.component.reminder.today.TodayTodoDataStore.saveData(
                    applicationContext, 
                    data
                )
                TodayTodoUpdateManager.updateByState(applicationContext, data)
                delay(1000)
            }
        }
    }

    /**
     * 음성 인식 시작
     */
    fun startSpeechRecognition() {
        if (_isListening.value) {
            return
        }

        _speechError.value = null
        _isListening.value = true
        speechRecognitionManager?.startListening()
    }

    /**
     * 음성 인식 중지
     */
    fun stopSpeechRecognition() {
        speechRecognitionManager?.stopListening()
        _isListening.value = false
    }

    /**
     * 음성 인식 에러 초기화
     */
    fun clearSpeechError() {
        _speechError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognitionManager?.destroy()
        speechRecognitionManager = null
    }
}

