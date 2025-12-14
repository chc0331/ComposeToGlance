package com.example.composetoglance.editor.canvas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.composetoglance.editor.draganddrop.DragTargetInfo
import com.example.composetoglance.editor.util.LayoutBounds
import com.example.composetoglance.editor.widget.PositionedWidget
import kotlin.math.max

/**
 * 삭제 영역을 시각적으로 표시하는 컴포저블
 * PositionedWidget을 레이아웃이나 캔버스 밖으로 드래그할 때 삭제 영역을 표시합니다.
 */
@Composable
fun BoxScope.DeleteZoneIndicator(
    layoutBounds: LayoutBounds?,
    canvasPosition: Offset,
    canvasBounds: Rect?,
    dragInfo: DragTargetInfo,
    density: Density
) {
    // PositionedWidget만 삭제 대상이므로 확인
    val isPositionedWidget = dragInfo.dataToDrop is PositionedWidget
    
    // 드래그 중이고 PositionedWidget인 경우에만 표시
    if (!dragInfo.isDragging || !isPositionedWidget) {
        return
    }
    
    val dropPositionInWindow = dragInfo.dragPosition + dragInfo.dragOffset
    
    // 부모 Box의 크기를 얻기 위한 상태
    var parentSize by remember { mutableStateOf<Size?>(null) }
    
    // 드래그 중이면 항상 삭제 영역 표시 (드래그 위치에 따라 해당 방향만 표시됨)
    val showDeleteZone = true
    
    // 애니메이션을 위한 alpha 값
    val targetAlpha = if (showDeleteZone) 1f else 0f
    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "delete_zone_alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                val size = layoutCoordinates.size
                parentSize = Size(size.width.toFloat(), size.height.toFloat())
            }
    ) {
        if (showDeleteZone && animatedAlpha > 0f) {
            // 레이아웃이 있으면 레이아웃 기준으로 삭제 영역 표시
            if (layoutBounds != null) {
                LayoutDeleteZone(
                    layoutBounds = layoutBounds,
                    canvasPosition = canvasPosition,
                    dropPositionInWindow = dropPositionInWindow,
                    parentSize = parentSize,
                    density = density,
                    alpha = animatedAlpha
                )
            }
            // 레이아웃이 없으면 캔버스 기준으로 삭제 영역 표시
            else if (canvasBounds != null) {
                CanvasDeleteZone(
                    canvasBounds = canvasBounds,
                    dropPositionInWindow = dropPositionInWindow,
                    parentSize = parentSize,
                    density = density,
                    alpha = animatedAlpha
                )
            }
        }
    }
}

/**
 * 레이아웃 밖 삭제 영역 표시
 */
@Composable
private fun LayoutDeleteZone(
    layoutBounds: LayoutBounds,
    canvasPosition: Offset,
    dropPositionInWindow: Offset,
    parentSize: Size?,
    density: Density,
    alpha: Float
) {
    val layoutLeft = layoutBounds.position.x
    val layoutTop = layoutBounds.position.y
    val layoutRight = layoutBounds.position.x + layoutBounds.size.width
    val layoutBottom = layoutBounds.position.y + layoutBounds.size.height
    
    val relativeLeft = layoutLeft - canvasPosition.x
    val relativeTop = layoutTop - canvasPosition.y
    val relativeRight = layoutRight - canvasPosition.x
    val relativeBottom = layoutBottom - canvasPosition.y
    
    // 드래그 위치가 레이아웃 위쪽에 있는지 확인
    val isAbove = dropPositionInWindow.y < layoutTop
    // 드래그 위치가 레이아웃 아래쪽에 있는지 확인
    val isBelow = dropPositionInWindow.y > layoutBottom
    // 드래그 위치가 레이아웃 왼쪽에 있는지 확인
    val isLeft = dropPositionInWindow.x < layoutLeft
    // 드래그 위치가 레이아웃 오른쪽에 있는지 확인
    val isRight = dropPositionInWindow.x > layoutRight
    
    // 위쪽 영역 (드래그 위치가 위쪽에 있을 때만)
    if (isAbove && relativeTop > 0f) {
        DeleteZoneArea(
            offsetX = 0f,
            offsetY = 0f,
            width = Float.MAX_VALUE,
            height = relativeTop,
            density = density,
            alpha = alpha
        )
    }
    
    // 아래쪽 영역 (드래그 위치가 아래쪽에 있을 때만)
    if (isBelow) {
        val bottomHeight = if (parentSize != null && relativeBottom < parentSize.height) {
            parentSize.height - relativeBottom
        } else {
            Float.MAX_VALUE
        }
        DeleteZoneArea(
            offsetX = 0f,
            offsetY = relativeBottom,
            width = Float.MAX_VALUE,
            height = bottomHeight,
            density = density,
            alpha = alpha
        )
    }
    
    // 왼쪽 영역 (드래그 위치가 왼쪽에 있고, 위/아래가 아닐 때만)
    if (isLeft && !isAbove && !isBelow && relativeLeft > 0f) {
        DeleteZoneArea(
            offsetX = 0f,
            offsetY = max(0f, relativeTop),
            width = relativeLeft,
            height = layoutBounds.size.height.toFloat(),
            density = density,
            alpha = alpha
        )
    }
    
    // 오른쪽 영역 (드래그 위치가 오른쪽에 있고, 위/아래가 아닐 때만)
    if (isRight && !isAbove && !isBelow) {
        DeleteZoneArea(
            offsetX = relativeRight,
            offsetY = max(0f, relativeTop),
            width = Float.MAX_VALUE,
            height = layoutBounds.size.height.toFloat(),
            density = density,
            alpha = alpha
        )
    }
}

/**
 * 캔버스 밖 삭제 영역 표시
 */
@Composable
private fun CanvasDeleteZone(
    canvasBounds: Rect,
    dropPositionInWindow: Offset,
    parentSize: Size?,
    density: Density,
    alpha: Float
) {
    val canvasLeft = canvasBounds.left
    val canvasTop = canvasBounds.top
    val canvasRight = canvasBounds.right
    val canvasBottom = canvasBounds.bottom
    
    // 드래그 위치가 캔버스 위쪽에 있는지 확인
    val isAbove = dropPositionInWindow.y < canvasTop
    // 드래그 위치가 캔버스 아래쪽에 있는지 확인
    val isBelow = dropPositionInWindow.y > canvasBottom
    // 드래그 위치가 캔버스 왼쪽에 있는지 확인
    val isLeft = dropPositionInWindow.x < canvasLeft
    // 드래그 위치가 캔버스 오른쪽에 있는지 확인
    val isRight = dropPositionInWindow.x > canvasRight
    
    // 위쪽 영역 (드래그 위치가 위쪽에 있을 때만)
    if (isAbove && canvasTop > 0f) {
        DeleteZoneArea(
            offsetX = 0f,
            offsetY = 0f,
            width = Float.MAX_VALUE,
            height = canvasTop,
            density = density,
            alpha = alpha
        )
    }
    
    // 아래쪽 영역 (드래그 위치가 아래쪽에 있을 때만)
    if (isBelow) {
        val bottomHeight = if (parentSize != null && canvasBottom < parentSize.height) {
            parentSize.height - canvasBottom
        } else {
            Float.MAX_VALUE
        }
        DeleteZoneArea(
            offsetX = 0f,
            offsetY = canvasBottom,
            width = Float.MAX_VALUE,
            height = bottomHeight,
            density = density,
            alpha = alpha
        )
    }
    
    // 왼쪽 영역 (드래그 위치가 왼쪽에 있고, 위/아래가 아닐 때만)
    if (isLeft && !isAbove && !isBelow && canvasLeft > 0f) {
        DeleteZoneArea(
            offsetX = 0f,
            offsetY = max(0f, canvasTop),
            width = canvasLeft,
            height = canvasBounds.height,
            density = density,
            alpha = alpha
        )
    }
    
    // 오른쪽 영역 (드래그 위치가 오른쪽에 있고, 위/아래가 아닐 때만)
    if (isRight && !isAbove && !isBelow) {
        DeleteZoneArea(
            offsetX = canvasRight,
            offsetY = max(0f, canvasTop),
            width = Float.MAX_VALUE,
            height = canvasBounds.height,
            density = density,
            alpha = alpha
        )
    }
}

/**
 * 삭제 영역의 개별 영역을 표시하는 컴포저블
 */
@Composable
private fun DeleteZoneArea(
    offsetX: Float,
    offsetY: Float,
    width: Float,
    height: Float,
    density: Density,
    alpha: Float
) {
    // 유효하지 않은 크기는 표시하지 않음
    if (width <= 0f || height <= 0f) {
        return
    }
    
    val widthDp = with(density) {
        if (width == Float.MAX_VALUE) {
            null
        } else {
            width.toDp()
        }
    }
    
    val heightDp = with(density) {
        if (height == Float.MAX_VALUE) {
            null
        } else {
            height.toDp()
        }
    }
    
    val offsetXDp = with(density) { offsetX.toDp() }
    val offsetYDp = with(density) { offsetY.toDp() }
    
    Box(
        modifier = Modifier
            .offset(offsetXDp, offsetYDp)
            .then(
                when {
                    // 둘 다 명시적으로 지정된 경우
                    widthDp != null && heightDp != null -> Modifier.size(widthDp, heightDp)
                    // 너비만 MAX_VALUE인 경우 (위/아래 영역)
                    widthDp == null && heightDp != null -> Modifier.fillMaxWidth().height(heightDp)
                    // 높이만 MAX_VALUE인 경우 (왼쪽/오른쪽 영역)
                    widthDp != null && heightDp == null -> Modifier.width(widthDp).fillMaxHeight()
                    // 둘 다 MAX_VALUE인 경우
                    else -> Modifier.fillMaxSize()
                }
            )
            .background(
                MaterialTheme.colorScheme.error.copy(
                    alpha = WidgetCanvasConstants.DELETE_ZONE_BACKGROUND_ALPHA * alpha
                )
            )
            .border(
                width = WidgetCanvasConstants.DELETE_ZONE_BORDER_WIDTH_DP,
                color = MaterialTheme.colorScheme.error.copy(alpha = alpha)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "삭제",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = alpha)
        )
    }
}

