package com.example.composetoglance.editor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.widget.repository.WidgetLayoutRepository

class WidgetEditorViewModelFactory(
    private val repository: WidgetLayoutRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WidgetEditorViewModel(repository) as T
    }
}