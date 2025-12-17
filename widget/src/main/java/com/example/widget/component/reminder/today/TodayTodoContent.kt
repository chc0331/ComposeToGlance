package com.example.widget.component.reminder.today

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.widget.component.reminder.today.formatDateString
import com.example.widget.component.reminder.today.formatHeaderDate
import com.example.widget.component.reminder.today.getTodayDateString
import com.example.widget.database.TodoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodayTodoContent(
    context: ComponentActivity,
    onDismiss: () -> Unit
) {
    val repository = remember { TodoRepository(context) }
    val todayDateString = remember { getTodayDateString() }
    val calendar = remember { Calendar.getInstance() }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val selectedDateString = remember(selectedDateMillis) {
        formatDateString(Date(selectedDateMillis))
    }
    val headerDateText = remember(selectedDateMillis) {
        formatHeaderDate(Date(selectedDateMillis))
    }
    val isToday = remember(selectedDateString, todayDateString) { selectedDateString == todayDateString }

    val todos by repository.getTodosByDate(selectedDateString).collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoEntity?>(null) }
    var showCalendarPicker by remember { mutableStateOf(false) }
    var inlineTitle by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            // iOS-like header (nav-bar feel)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(onClick = {
                            calendar.timeInMillis = selectedDateMillis
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            selectedDateMillis = calendar.timeInMillis
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "이전 날짜"
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showCalendarPicker = true },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = headerDateText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isToday) "Today" else selectedDateString,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = {
                            calendar.timeInMillis = selectedDateMillis
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                            selectedDateMillis = calendar.timeInMillis
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "다음 날짜"
                            )
                        }
                        IconButton(onClick = { showCalendarPicker = true }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "날짜 선택"
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기"
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    InlineAddTodoRow(
                        title = inlineTitle,
                        onTitleChange = { inlineTitle = it },
                        onAdd = {
                            val title = inlineTitle.trim()
                            if (title.isNotEmpty()) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    val newTodo = TodoEntity(
                                        title = title,
                                        description = null,
                                        date = selectedDateString,
                                        dateTime = null,
                                        status = TodoStatus.INCOMPLETE
                                    )
                                    repository.insertTodo(newTodo)
                                }
                                inlineTitle = ""
                                focusManager.clearFocus()
                            }
                        },
                        onMore = { showAddDialog = true }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (todos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Todo가 없습니다",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = todos,
                    key = { _, todo -> todo.id }
                ) { index, todo ->
                    val isFirst = index == 0
                    val isLast = index == todos.lastIndex
                    val cellShape = when {
                        isFirst && isLast -> RoundedCornerShape(14.dp)
                        isFirst -> RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                        isLast -> RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
                        else -> RoundedCornerShape(0.dp)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        shape = cellShape,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Column {
                            TodoItem(
                                todo = todo,
                                onToggleStatus = { todoEntity ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val newStatus = when (todoEntity.status) {
                                            TodoStatus.COMPLETED -> TodoStatus.INCOMPLETE
                                            TodoStatus.INCOMPLETE -> TodoStatus.COMPLETED
                                            TodoStatus.PENDING -> TodoStatus.COMPLETED
                                        }
                                        repository.toggleTodoStatus(todoEntity.id, newStatus)
                                    }
                                },
                                onEdit = { editingTodo = it },
                                onDelete = { todoEntity ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        repository.deleteTodo(todoEntity)
                                    }
                                }
                            )
                            if (!isLast) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(start = 44.dp)
                                )
                            }
                        }
                    }

                    if (!isLast) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
    
    // Add Dialog
    if (showAddDialog) {
        TodoEditDialog(
            todo = null,
            onDismiss = { showAddDialog = false },
            onSave = { title, description, dateTime ->
                coroutineScope.launch(Dispatchers.IO) {
                    // dateTime이 있으면 해당 날짜를 사용, 없으면 오늘 날짜 사용
                    val date = if (dateTime != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateFormat.format(Date(dateTime))
                    } else {
                        selectedDateString
                    }
                    val newTodo = TodoEntity(
                        title = title,
                        description = description.ifEmpty { null },
                        date = date,
                        dateTime = dateTime,
                        status = TodoStatus.INCOMPLETE
                    )
                    repository.insertTodo(newTodo)
                }
                showAddDialog = false
            }
        )
    }
    
    // Edit Dialog
    editingTodo?.let { todo ->
        TodoEditDialog(
            todo = todo,
            onDismiss = { editingTodo = null },
            onSave = { title, description, dateTime ->
                coroutineScope.launch(Dispatchers.IO) {
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
                }
                editingTodo = null
            }
        )
    }

    // Calendar picker
    if (showCalendarPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            initialDisplayMode = DisplayMode.Picker
        )
        DatePickerDialog(
            onDismissRequest = { showCalendarPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                        showCalendarPicker = false
                    },
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendarPicker = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Composable
private fun InlineAddTodoRow(
    title: String,
    onTitleChange: (String) -> Unit,
    onAdd: () -> Unit,
    onMore: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("New reminder") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() })
            )
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "추가"
                )
            }
            IconButton(onClick = onMore) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "상세 추가"
                )
            }
        }
    }
}

