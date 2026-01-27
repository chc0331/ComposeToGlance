package com.widgetworld.app

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetworld.app.repository.WidgetCanvasStateRepository
import com.widgetworld.widgetcomponent.proto.SizeType
import com.widgetworld.widgetcomponent.provider.ExtraLargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.LargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.MediumWidgetProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WidgetCanvasStateRepository) :
    ViewModel() {

//    val widgetCanvasState = repository.dataStoreFlow.map { layout ->
//        if (layout.sizeType == com.widgetworld.widgetcomponent.proto.SizeType.SIZE_TYPE_UNSPECIFIED)
//            WidgetCanvasUiState(sizeType = SizeType.LARGE)
//        else WidgetCanvasUiState(sizeType = SizeType.getSizeType(layout.sizeType.name))
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = WidgetCanvasUiState()
//    )

    // 그리드 설정 패널 표시 상태
    var showGridSettings by mutableStateOf(false)
        private set

    fun save(context: Context) {
        viewModelScope.launch {
            val widgetCanvasState = repository.dataStoreFlow.first()
            val currentLayoutSizeType = widgetCanvasState.sizeType

            val provider = when (currentLayoutSizeType) {
                SizeType.SIZE_TYPE_MEDIUM -> MediumWidgetProvider::class.java.name
                SizeType.SIZE_TYPE_LARGE -> LargeWidgetProvider::class.java.name
                SizeType.SIZE_TYPE_EXTRA_LARGE -> ExtraLargeWidgetProvider::class.java.name
                else -> return@launch
            }
            delay(150)
            AppWidgetManager.getInstance(context).requestPinAppWidget(
                ComponentName(
                    context.packageName,
                    provider
                ), null, null
            )
        }
    }

    fun showGridSettingsPanel(show: Boolean) {
        this.showGridSettings = show
    }
}