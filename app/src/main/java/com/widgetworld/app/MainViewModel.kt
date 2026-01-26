package com.widgetworld.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetworld.app.repository.WidgetCanvasStateRepository
import com.widgetworld.widgetcomponent.SizeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


data class WidgetCanvasUiState(
    val sizeType: SizeType = SizeType.LARGE
)

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WidgetCanvasStateRepository) :
    ViewModel() {
    val widgetCanvasState = repository.dataStoreFlow.map { layout ->
        if (layout.sizeType == com.widgetworld.widgetcomponent.proto.SizeType.SIZE_TYPE_UNSPECIFIED)
            WidgetCanvasUiState(sizeType = SizeType.LARGE)
        else WidgetCanvasUiState(sizeType = SizeType.getSizeType(layout.sizeType.name))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WidgetCanvasUiState()
    )
}