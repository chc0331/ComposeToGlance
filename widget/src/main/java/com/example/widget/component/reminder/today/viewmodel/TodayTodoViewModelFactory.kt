package com.example.widget.component.reminder.today.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.widget.component.reminder.today.TodoRepository

/**
 * TodayTodoViewModel을 생성하는 Factory
 */
class TodayTodoViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayTodoViewModel::class.java)) {
            val repository = TodoRepository(context)
            return TodayTodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

