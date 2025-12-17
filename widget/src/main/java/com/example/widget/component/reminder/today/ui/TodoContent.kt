package com.example.widget.component.reminder.today.ui

import android.R.attr.bottom
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.widget.component.reminder.today.viewmodel.TodayTodoViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodoContent(
    viewModel: TodayTodoViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
                .padding(bottom = 12.dp, start = 6.dp, end = 6.dp)
                .height(400.dp),
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
                        IconButton(onClick = { viewModel.selectPreviousDate() }) {
                            Icon(
                                imageVector = Icons.Filled.ChevronLeft,
                                contentDescription = "이전 날짜",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.showCalendarPicker() },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.headerDateText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (uiState.isToday) "Today" else uiState.selectedDateString,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { viewModel.selectNextDate() }) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "다음 날짜",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        IconButton(onClick = { viewModel.showCalendarPicker() }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "날짜 선택",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = MaterialTheme.colorScheme.onSurface
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
                        title = uiState.inlineTitle,
                        onTitleChange = { viewModel.updateInlineTitle(it) },
                        onAdd = {
                            viewModel.addTodoInline()
                            focusManager.clearFocus()
                        },
                        onMore = { viewModel.showAddDialog() }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (uiState.todos.isEmpty()) {
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
                    items = uiState.todos,
                    key = { _, todo -> todo.id }
                ) { index, todo ->
                    val isFirst = index == 0
                    val isLast = index == uiState.todos.lastIndex
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

    // Calendar picker
    if (uiState.showCalendarPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDateMillis,
            initialDisplayMode = DisplayMode.Picker
        )
        DatePickerDialog(
            onDismissRequest = { viewModel.hideCalendarPicker() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.selectDate(it)
                        }
                        viewModel.hideCalendarPicker()
                    },
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideCalendarPicker() }) {
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
                    contentDescription = "추가",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onMore) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "상세 추가",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

