package com.widgetworld.widgetcomponent.component.reminder.today.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.widgetworld.widgetcomponent.component.reminder.today.viewmodel.TodayTodoViewModel

/**
 * Todo 메인 컨텐츠
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoContent(
    viewModel: TodayTodoViewModel,
    onDismiss: () -> Unit,
    onRequestAudioPermission: () -> Unit = {},
    hasAudioPermission: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // 음성 인식 에러 처리
    LaunchedEffect(Unit) {
        snapshotFlow { uiState.speechError }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { error ->
                // 에러는 ViewModel에서 관리하므로 여기서는 로그만 남김
                android.util.Log.w("TodoContent", "Speech recognition error: $error")
            }
    }
    
    val backgroundInteractionSource = remember { MutableInteractionSource() }
    val surfaceInteractionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .clickable(
                indication = null, // 리플 효과 제거
                interactionSource = backgroundInteractionSource,
                onClick = onDismiss
            ), // 바깥쪽 터치 시 종료
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .clickable(
                    indication = null, // 리플 효과 제거
                    interactionSource = surfaceInteractionSource,
                    onClick = {}
                ), // Surface 내부 클릭은 이벤트 소비 (전파 방지)
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp
            )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                // 헤더
                item {
                    Header(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        viewModel = viewModel,
                        onPreviousClick = { viewModel.selectPreviousDate() },
                        onNextClick = { viewModel.selectNextDate() },
                        onCalendarClick = { viewModel.showDatePicker() },
                        onSave = { 
                            viewModel.saveAndUpdateWidget()
                        },
                        onDismiss = onDismiss
                    )
                }
                
                // 인라인 추가 입력
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        InlineAddTodoRow(
                            title = uiState.inlineTitle,
                            onTitleChange = { viewModel.updateInlineTitle(it) },
                            onAdd = {
                                viewModel.addTodoInline()
                                focusManager.clearFocus()
                            },
                            onMore = { viewModel.showAddDialog() },
                            onMicClick = {
                                if (hasAudioPermission) {
                                    if (uiState.isListening) {
                                        viewModel.stopSpeechRecognition()
                                    } else {
                                        viewModel.startSpeechRecognition()
                                    }
                                } else {
                                    onRequestAudioPermission()
                                }
                            },
                            isListening = uiState.isListening,
                            hasAudioPermission = hasAudioPermission
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                
                // Todo 리스트
                if (uiState.todos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "할 일이 없습니다",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(
                        items = uiState.todos,
                        key = { _, todo -> todo.id }
                    ) { index, todo ->
                        val isFirst = index == 0
                        val isLast = index == uiState.todos.lastIndex
                        val cellShape = when {
                            isFirst && isLast -> RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS)
                            isFirst -> RoundedCornerShape(
                                topStart = TodoDesignConstants.CORNER_RADIUS,
                                topEnd = TodoDesignConstants.CORNER_RADIUS
                            )
                            isLast -> RoundedCornerShape(
                                bottomStart = TodoDesignConstants.CORNER_RADIUS,
                                bottomEnd = TodoDesignConstants.CORNER_RADIUS
                            )
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
                                    onToggleStatus = { viewModel.toggleTodoStatus(it) },
                                    onEdit = { viewModel.showEditDialog(it) },
                                    onDelete = { viewModel.deleteTodo(it) }
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
    }
    
    // Add Dialog
    if (uiState.showAddDialog) {
        TodoEditDialog(
            todo = null,
            onDismiss = { viewModel.hideAddDialog() },
            onSave = { title, description, dateTime ->
                viewModel.addTodo(title, description, dateTime)
            }
        )
    }
    
    // Edit Dialog
    uiState.editingTodo?.let { todo ->
        TodoEditDialog(
            todo = todo,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { title, description, dateTime ->
                viewModel.updateTodo(todo, title, description, dateTime)
            }
        )
    }
    
    // Date Picker
    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDateMillis,
            initialDisplayMode = DisplayMode.Picker
        )
        DatePickerDialog(
            onDismissRequest = { viewModel.hideDatePicker() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.selectDate(it)
                        }
                        viewModel.hideDatePicker()
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDatePicker() }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * 헤더 (날짜 네비게이션, 저장/닫기 버튼)
 */
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    viewModel: TodayTodoViewModel,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "이전 날짜"
                )
            }
            
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isToday) "Today" else uiState.headerDateText,
                    style = if (uiState.isToday) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.titleSmall
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
            
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "다음 날짜"
                )
            }
            
            IconButton(onClick = onCalendarClick) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "날짜 선택"
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "저장",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기"
                )
            }
        }
    }
}

/**
 * 인라인 Todo 추가 Row
 */
@Composable
private fun InlineAddTodoRow(
    title: String,
    onTitleChange: (String) -> Unit,
    onAdd: () -> Unit,
    onMore: () -> Unit,
    onMicClick: () -> Unit,
    isListening: Boolean = false,
    hasAudioPermission: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS),
        color = MaterialTheme.colorScheme.primaryContainer
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
                placeholder = { Text("새 항목 추가") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            
            IconButton(onClick = onMicClick) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = if (isListening) "음성 인식 중지" else "음성 입력",
                    tint = if (isListening) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
            
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "추가",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            IconButton(onClick = onMore) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "상세 추가",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

