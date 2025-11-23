package com.example.composetoglance.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.composetoglance.ui.layout.Layout
import com.example.composetoglance.ui.widget.Category
import com.example.composetoglance.ui.widget.Widget

class WidgetEditorViewModel : ViewModel() {
    
    // 카테고리 목록
    val categories: List<Category> = listOf(
        Category("cat1", "카테고리 1"),
        Category("cat2", "카테고리 2"),
        Category("cat3", "카테고리 3")
    )
    
    // 위젯 목록
    val widgets = mutableStateListOf(
        Widget("위젯 1", "설명 1", "1x1", "cat1"),
        Widget("위젯 2", "설명 2", "2x1", "cat1"),
        Widget("위젯 3", "설명 3", "2x2", "cat2"),
        Widget("위젯 4", "설명 4", "1x1", "cat2"),
        Widget("위젯 5", "설명 5", "1x1", "cat3"),
        Widget("위젯 6", "설명 6", "2x1", "cat3")
    )
    
    // 선택된 레이아웃
    var selectedLayout by mutableStateOf<Layout?>(null)
        private set
    
    /**
     * 레이아웃 선택
     */
    fun selectLayout(layout: Layout?) {
        selectedLayout = layout
    }
    
    /**
     * 위젯 추가
     */
    fun addWidget(widget: Widget) {
        widgets.add(widget)
    }
    
    /**
     * 위젯 제거
     */
    fun removeWidget(widget: Widget) {
        widgets.remove(widget)
    }
    
    /**
     * 위젯 업데이트
     */
    fun updateWidget(oldWidget: Widget, newWidget: Widget) {
        val index = widgets.indexOf(oldWidget)
        if (index != -1) {
            widgets[index] = newWidget
        }
    }
    
    /**
     * 저장 기능 (나중에 구현)
     */
    fun save() {
        // TODO: 저장 기능 구현
    }
}

