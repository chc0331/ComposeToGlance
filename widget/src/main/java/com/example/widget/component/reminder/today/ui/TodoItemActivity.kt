package com.example.widget.component.reminder.today.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.widget.component.reminder.today.viewmodel.TodayTodoViewModel
import com.example.widget.component.reminder.today.viewmodel.TodayTodoViewModelFactory
import com.example.widget.theme.AppTheme

/**
 * Today Todo를 표시하는 Dialog 스타일 Activity
 */
class TodayTodoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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

