package com.widgetworld.widgetcomponent.component.reminder.today.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.widgetworld.widgetcomponent.component.reminder.today.TodayTodoDataStore
import com.widgetworld.widgetcomponent.component.reminder.today.TodayTodoUpdateManager
import com.widgetworld.widgetcomponent.component.reminder.today.TodoDateUtils
import com.widgetworld.widgetcomponent.component.reminder.today.viewmodel.TodayTodoViewModel
import com.widgetworld.widgetcomponent.component.reminder.today.viewmodel.TodayTodoViewModelFactory
import com.widgetworld.widgetcomponent.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Todo 관리 Activity (Dialog 스타일)
 */
class TodoActivity : ComponentActivity() {
    
    private var pendingSpeechRecognitionStart = false
    private var viewModel: TodayTodoViewModel? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            android.util.Log.d("TodoActivity", "RECORD_AUDIO permission granted")
            // 권한이 부여되면 대기 중인 음성 인식 시작
            if (pendingSpeechRecognitionStart) {
                viewModel?.startSpeechRecognition()
                pendingSpeechRecognitionStart = false
            }
        } else {
            android.util.Log.w("TodoActivity", "RECORD_AUDIO permission denied")
            pendingSpeechRecognitionStart = false
        }
    }

    /**
     * RECORD_AUDIO 권한 확인 및 요청
     * @param startSpeechRecognitionIfGranted 권한이 이미 있거나 부여되면 음성 인식을 시작할지 여부
     */
    fun checkAndRequestAudioPermission(startSpeechRecognitionIfGranted: Boolean = false): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    if (startSpeechRecognitionIfGranted) {
                        viewModel?.startSpeechRecognition()
                    }
                    true
                }
                else -> {
                    if (startSpeechRecognitionIfGranted) {
                        pendingSpeechRecognitionStart = true
                    }
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    false
                }
            }
        } else {
            // Android 6.0 미만에서는 권한이 자동으로 부여됨
            if (startSpeechRecognitionIfGranted) {
                viewModel?.startSpeechRecognition()
            }
            true
        }
    }

    /**
     * RECORD_AUDIO 권한이 있는지 확인
     */
    fun hasAudioPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 바깥쪽 터치 시 액티비티 종료
        setFinishOnTouchOutside(true)
        
        // Intent에서 widgetId 읽기 (없으면 -1)
        // RemoteViewsBuilder에서 Long으로 저장될 수 있으므로 Long/Int/String 모두 시도
        val widgetId = when {
            intent.hasExtra("WIDGET_ID") -> {
                when (val value = intent.extras?.get("WIDGET_ID")) {
                    is Int -> value
                    is Long -> value.toInt()
                    is String -> value.toIntOrNull() ?: -1
                    else -> -1
                }
            }
            else -> -1
        }
        android.util.Log.d("TodoActivity", "Read widgetId from Intent: $widgetId (type: ${intent.extras?.get("WIDGET_ID")?.javaClass?.name})")
        
        // Intent에서 SELECTED_DATE extra 확인
        val selectedDateString = intent.getStringExtra("SELECTED_DATE")
        if (selectedDateString != null) {
            android.util.Log.d("TodoActivity", "Selected date from Intent: $selectedDateString")
        }
        
        // ViewModelFactory에 초기 날짜 전달 (Intent로 전달된 날짜가 있으면 우선 사용)
        val viewModelFactory = TodayTodoViewModelFactory(this, widgetId, selectedDateString)
        viewModel = ViewModelProvider(this, viewModelFactory)[TodayTodoViewModel::class.java]
        
        // Intent에서 SHOW_DATE_PICKER extra 확인
        val shouldShowDatePicker = intent.getBooleanExtra("SHOW_DATE_PICKER", false)
        
        if (shouldShowDatePicker) {
            // 캘린더 다이얼로그만 표시
            // DataStore에서 선택된 날짜를 먼저 동기적으로 로드
            var initialDateMillis = System.currentTimeMillis()
            
            lifecycleScope.launch {
                initialDateMillis = try {
                    val loadWidgetId = if (widgetId != -1) widgetId else 0
                    val data = TodayTodoDataStore.loadData(this@TodoActivity, loadWidgetId)
                    // UTC 기준 밀리초로 변환 (DatePicker는 UTC 기준)
                    val parsedMillis = TodoDateUtils.parseDateToUtcMillis(data.selectedDate)
                    android.util.Log.d("TodoActivity", "Loaded date from DataStore for widget $loadWidgetId: ${data.selectedDate}, UTC millis: $parsedMillis")
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
                                                    
                                                    android.util.Log.d("TodoActivity", "Date selected, widgetId: $widgetId")
                                                    val updateWidgetId = if (widgetId != -1) widgetId else null
                                                    android.util.Log.d("TodoActivity", "updateWidgetId: $updateWidgetId")
                                                    if (updateWidgetId != null) {
                                                        // 특정 위젯에만 데이터 저장
                                                        TodayTodoDataStore.saveData(this@TodoActivity, updateWidgetId, data)
                                                    } else {
                                                        // 모든 위젯에 데이터 저장 (하위 호환성)
                                                        TodayTodoDataStore.saveData(this@TodoActivity, data)
                                                    }
                                                    android.util.Log.d("TodoActivity", "Saved to DataStore $updateWidgetId")
                                                    
                                                    TodayTodoUpdateManager.updateByState(
                                                        this@TodoActivity,
                                                        updateWidgetId,
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
                        viewModel = viewModel!! ,
                        onDismiss = {
                            finish()
                        },
                        onRequestAudioPermission = {
                            checkAndRequestAudioPermission(startSpeechRecognitionIfGranted = true)
                        },
                        hasAudioPermission = hasAudioPermission()
                    )
                }
            }
        }
    }
}

