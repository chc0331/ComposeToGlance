package com.widgetkit.widgetcomponent.component.reminder.today.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.widgetkit.widgetcomponent.database.TodoEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Todo 추가/수정 다이얼로그
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditDialog(
    todo: TodoEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String?, dateTime: Long?) -> Unit
) {
    val initialTitle = todo?.title ?: ""
    val initialDescription = todo?.description ?: ""
    val initialDateTime = todo?.dateTime
    
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    
    // 날짜/시간 선택
    val calendar = Calendar.getInstance()
    if (initialDateTime != null) {
        calendar.timeInMillis = initialDateTime
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateTime ?: System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Picker
    )
    
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // 날짜/시간 표시 텍스트
    val dateTimeText = remember(
        datePickerState.selectedDateMillis,
        timePickerState.hour,
        timePickerState.minute
    ) {
        if (datePickerState.selectedDateMillis != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val cal = Calendar.getInstance().apply {
                timeInMillis = datePickerState.selectedDateMillis!!
                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                set(Calendar.MINUTE, timePickerState.minute)
            }
            dateFormat.format(Date(cal.timeInMillis))
        } else {
            "날짜/시간 미설정"
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (todo == null) "할 일 추가" else "할 일 수정",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("제목") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("설명 (선택사항)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 날짜/시간 선택 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS)
                    ) {
                        Text("날짜 선택")
                    }
                    
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(TodoDesignConstants.CORNER_RADIUS)
                    ) {
                        Text("시간 선택")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 선택된 날짜/시간 표시
                Text(
                    text = "선택된 날짜/시간: $dateTimeText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val dateTime = if (datePickerState.selectedDateMillis != null) {
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = datePickerState.selectedDateMillis!!
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            cal.timeInMillis
                        } else {
                            null
                        }
                        onSave(
                            title,
                            description.ifBlank { null },
                            dateTime
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("시간 선택") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("취소")
                }
            }
        )
    }
}

