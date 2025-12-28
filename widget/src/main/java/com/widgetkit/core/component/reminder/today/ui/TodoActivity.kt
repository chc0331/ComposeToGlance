package com.widgetkit.core.component.reminder.today.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.widgetkit.core.component.reminder.today.viewmodel.TodayTodoViewModel
import com.widgetkit.core.component.reminder.today.viewmodel.TodayTodoViewModelFactory
import com.widgetkit.core.theme.AppTheme

/**
 * Todo 관리 Activity (Dialog 스타일)
 */
class TodoActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 바깥쪽 터치 시 액티비티 종료
        setFinishOnTouchOutside(true)
        
        val viewModelFactory = TodayTodoViewModelFactory(this)
        val viewModel = ViewModelProvider(this, viewModelFactory)[TodayTodoViewModel::class.java]
        
        setContent {
            AppTheme {
                TodoContent(
                    viewModel = viewModel,
                    onDismiss = { finish() }
                )
            }
        }
    }
}

