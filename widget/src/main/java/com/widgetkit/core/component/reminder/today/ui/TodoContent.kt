package com.widgetkit.core.component.reminder.today.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.widgetkit.core.component.reminder.today.viewmodel.TodayTodoViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodoContent(
    viewModel: TodayTodoViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = MaterialTheme.shapes.large
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                // iOS-like header (nav-bar feel)
                item {
                    Header(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        viewModel,
                        onPreviewClick = {
                            viewModel.selectPreviousDate()
                        },
                        onNextClick = {
                            viewModel.selectNextDate()
                        },
                        onCalendarClick = {
                            viewModel.showCalendarPicker()
                        },
                        onSave = {
                            viewModel.saveAndUpdateWidget()
                        },
                        onDismiss = onDismiss
                    )
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
private fun Header(
    modifier: Modifier = Modifier,
    viewModel: TodayTodoViewModel,
    onPreviewClick: () -> Unit,
    onNextClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onPreviewClick) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "이전 날짜",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isToday) "Today" else uiState.headerDateText,
                    style = if (uiState.isToday) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "다음 날짜",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onCalendarClick) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "날짜 선택",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onSave,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "저장",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
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
                placeholder = { Text("New item") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer)
            )
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

