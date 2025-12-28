package com.widgetkit.core.component.reminder.today.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.widgetkit.core.component.reminder.today.TodayTodoDataStore
import com.widgetkit.core.component.reminder.today.TodayTodoUpdateManager
import com.widgetkit.core.component.reminder.today.TodoDateUtils
import com.widgetkit.core.component.reminder.today.viewmodel.TodayTodoViewModel
import com.widgetkit.core.component.reminder.today.viewmodel.TodayTodoViewModelFactory
import com.widgetkit.core.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Todo 관리 Activity (Dialog 스타일)
 */
class TodoActivity : ComponentActivity() {
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 바깥쪽 터치 시 액티비티 종료
        setFinishOnTouchOutside(true)
        
        val viewModelFactory = TodayTodoViewModelFactory(this)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TodayTodoViewModel::class.java]
        
        // Intent에서 SHOW_DATE_PICKER extra 확인
        val shouldShowDatePicker = intent.getBooleanExtra("SHOW_DATE_PICKER", false)
        
        if (shouldShowDatePicker) {
            // 캘린더 다이얼로그만 표시
            // DataStore에서 선택된 날짜를 먼저 동기적으로 로드
            var initialDateMillis = System.currentTimeMillis()
            
            lifecycleScope.launch {
                initialDateMillis = try {
                    val data = TodayTodoDataStore.loadData(this@TodoActivity)
                    // UTC 기준 밀리초로 변환 (DatePicker는 UTC 기준)
                    val parsedMillis = TodoDateUtils.parseDateToUtcMillis(data.selectedDate)
                    android.util.Log.d("TodoActivity", "Loaded date from DataStore: ${data.selectedDate}, UTC millis: $parsedMillis")
                    parsedMillis ?: System.currentTimeMillis()
                } catch (e: Exception) {
                    android.util.Log.e("TodoActivity", "Error loading date from DataStore", e)
                    System.currentTimeMillis()
                }
                
                // 날짜 로드 후 UI 설정
                setContent {
                    AppTheme {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = initialDateMillis,
                            initialDisplayMode = DisplayMode.Picker
                        )
                        
                        DatePickerDialog(
                            onDismissRequest = { finish() },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                                            // 선택된 날짜를 DataStore에 저장하고 위젯 업데이트
                                            lifecycleScope.launch {
                                                try {
                                                    // UTC 밀리초를 날짜 문자열로 변환
                                                    val selectedDate = TodoDateUtils.formatMillisToDateString(selectedMillis)
                                                    android.util.Log.d("TodoActivity", "Selected date: $selectedDate (UTC millis: $selectedMillis)")
                                                    
                                                    val data = TodayTodoUpdateManager.loadTodosForDate(
                                                        this@TodoActivity,
                                                        selectedDate
                                                    )
                                                    android.util.Log.d("TodoActivity", "Loaded data: ${data.totalCount} todos")
                                                    
                                                    TodayTodoDataStore.saveData(this@TodoActivity, data)
                                                    android.util.Log.d("TodoActivity", "Saved to DataStore")
                                                    
                                                    TodayTodoUpdateManager.updateComponent(
                                                        this@TodoActivity,
                                                        data
                                                    )
                                                    android.util.Log.d("TodoActivity", "Updated component")
                                                    
                                                    // 위젯 업데이트가 완료될 때까지 잠시 대기
                                                    kotlinx.coroutines.delay(500)
                                                } catch (e: Exception) {
                                                    android.util.Log.e("TodoActivity", "Error updating widget", e)
                                                }
                                                finish()
                                            }
                                        } ?: finish()
                                    }
                                ) {
                                    Text("확인")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { finish() }) {
                                    Text("취소")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }
        } else {
            // 일반 Todo 관리 화면 표시
            setContent {
                AppTheme {
                    TodoContent(
                        viewModel = viewModel,
                        onDismiss = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}

