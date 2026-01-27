package com.widgetworld.app.editor

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.widgetworld.app.editor.bottompanel.BottomPanelWithTabs
import com.widgetworld.app.editor.widgetcanvas.WidgetCanvas
import com.widgetworld.app.editor.widgetcanvas.canvasBorder
import com.widgetworld.app.editor.draganddrop.DragTargetInfo
import com.widgetworld.app.editor.draganddrop.LocalDragTargetInfo
import com.widgetworld.app.editor.viewmodel.WidgetEditorViewModel
import kotlinx.coroutines.delay


@Composable
fun WidgetEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: WidgetEditorViewModel = viewModel()
) {
    val outline = MaterialTheme.colorScheme.outline
    val canvasBackgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(
        alpha = 0.05f
    )
    val currentLayout by viewModel.selectedLayoutState.collectAsStateWithLifecycle()
    val positionedWidgets by viewModel.positionedWidgetsState.collectAsStateWithLifecycle()

    WidgetEditorContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        ) {
            val density = LocalDensity.current
            val widgetToAdd = viewModel.addedWidget
            WidgetCanvas(
                modifier = Modifier
                    .weight(2.2f)
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .canvasBorder(outline, canvasBackgroundColor),
                selectedLayout = currentLayout,
                positionedWidgets = positionedWidgets,
                viewModel = viewModel,
                onWidgetAddProcessed = { canvasPosition, layoutBounds, selectedLayout ->
                    viewModel.addWidgetToCanvas(
                        density,
                        canvasPosition,
                        layoutBounds,
                        selectedLayout
                    )
                }
            )

            Spacer(modifier = Modifier.size(6.dp))

            BottomPanelWithTabs(
                widgets = viewModel.widgets,
                categories = viewModel.categories,
                onLayoutSelected = { viewModel.selectLayout(it) },
                onWidgetSelected = { widget ->
                    viewModel.addedWidget = widget
                },
                selectedLayout = viewModel.selectedLayout,
                modifier = Modifier
                    .weight(2f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        outline,
                        RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}


@Composable
fun WidgetEditorContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }

    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    val newPosition = layoutCoordinates.localToWindow(Offset.Zero)
                    // 값이 실제로 변경되었을 때만 상태 업데이트하여 불필요한 재구성 방지
                    if (newPosition != canvasPosition) {
                        canvasPosition = newPosition
                    }
                }) {
            content()
            // 드래그 중이거나 드롭 직후 페이드아웃 중일 때 오버레이 표시
            // 단, dataToDrop이 null이면 즉시 숨김 (삭제 시 잔상 방지)
            if ((state.isDragging || state.itemDropped) && state.dataToDrop != null) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                val hasValidSize = targetSize != IntSize.Zero

                // 드롭 시 페이드아웃을 위한 alpha 타겟 값
                val targetAlpha = when {
                    !hasValidSize -> 0f
                    state.itemDropped -> 0f // 드롭되면 페이드아웃
                    else -> 0.9f
                }

                // 스케일 애니메이션: 드래그 시작 시 1.0에서 1.5로 부드럽게 확대, 드롭 시 즉시 축소
                val targetScale = when {
                    !hasValidSize -> 1f
                    state.itemDropped -> 1f // 드롭되면 즉시 원래 크기로
                    else -> 1.1f
                }
                val animatedScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = tween(durationMillis = if (state.itemDropped) 100 else 200),
                    label = "scale_animation"
                )

                // alpha 애니메이션: 크기가 측정되면 부드럽게 나타남, 드롭 시 빠르게 페이드아웃
                val fadeOutDuration = if (state.itemDropped) 100 else 200
                val animatedAlpha by animateFloatAsState(
                    targetValue = targetAlpha,
                    animationSpec = tween(durationMillis = fadeOutDuration),
                    label = "alpha_animation"
                )

                // 페이드아웃 완료 후 상태 초기화
                LaunchedEffect(state.itemDropped) {
                    if (state.itemDropped) {
                        // 페이드아웃 애니메이션 시간만큼 대기
                        delay(fadeOutDuration.toLong() + 50)
                        state.itemDropped = false
                        state.isDragging = false
                        state.dragOffset = Offset.Zero
                        state.dataToDrop = null
                        state.draggableComposable = null
                    }
                }

                // 드롭되지 않은 경우 상태 초기화 (DropTarget이 데이터를 처리할 시간을 주기 위해 약간의 지연)
                LaunchedEffect(state.isDragging, state.itemDropped) {
                    if (!state.isDragging && !state.itemDropped && state.dataToDrop != null) {
                        // DropTarget이 재구성되어 데이터를 처리할 시간을 줌
                        delay(300)
                        if (!state.itemDropped && !state.isDragging) {
                            state.dragOffset = Offset.Zero
                            state.dataToDrop = null
                            state.draggableComposable = null
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            // graphicsLayer는 매 프레임마다 호출되므로 최소한의 계산만 수행
                            val currentTouchWindowPosition =
                                state.dragPosition + state.dragOffset
                            val relativeOffset = currentTouchWindowPosition - canvasPosition
                            val scaledWidth = targetSize.width * animatedScale
                            val scaledHeight = targetSize.height * animatedScale

                            scaleX = animatedScale
                            scaleY = animatedScale
                            alpha = animatedAlpha
                            translationX = relativeOffset.x - scaledWidth / 2
                            translationY = relativeOffset.y - scaledHeight / 2
                        }
                        .onGloballyPositioned {
                            targetSize = it.size
                        }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

