package com.widgetkit.core.component.reminder.today.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.widgetkit.core.component.reminder.today.TodoRepository

/**
 * TodayTodoViewModel Factory
 */
class TodayTodoViewModelFactory(
    private val context: Context,
    private val widgetId: Int = -1,
    private val initialDateString: String? = null
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayTodoViewModel::class.java)) {
            val repository = TodoRepository(context)
            return TodayTodoViewModel(repository, context.applicationContext, widgetId, initialDateString) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

