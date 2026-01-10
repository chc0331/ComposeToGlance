package com.widgetworld.app.editor.draganddrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

/**
 * 드래그 앤 드롭 상태를 관리하는 클래스
 * API 사용자가 직접 접근할 수 있도록 public으로 제공
 */
class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
    var itemDropped: Boolean by mutableStateOf(false)
}

/**
 * 드래그 앤 드롭 상태를 제공하는 CompositionLocal
 * API 사용자가 직접 접근할 수 있도록 public으로 제공
 */
val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

