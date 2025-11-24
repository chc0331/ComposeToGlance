package com.example.composetoglance.editor.widget

import android.widget.FrameLayout
import android.widget.RemoteViews
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.glance.GlanceComposable
import androidx.glance.appwidget.ExperimentalGlanceRemoteViewsApi
import androidx.glance.appwidget.GlanceRemoteViews
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.proto.WidgetLayoutDocument


@OptIn(ExperimentalGlanceRemoteViewsApi::class)
@Composable
fun AppWidgetView(
    size: DpSize,
    content: @Composable @GlanceComposable () -> Unit
) {
    val context = LocalContext.current
    val glanceRemoteViews = remember { GlanceRemoteViews() }
    
    // size를 기반으로 캐시 키 생성 (content는 이미 위에서 캐싱됨)
    val cacheKey = remember(size) {
        "${size.width.value}_${size.height.value}"
    }
    
    // RemoteViews를 캐싱하여 재렌더링 방지
    var renderedViews by remember(cacheKey) { mutableStateOf<RemoteViews?>(null) }
    var isRendering by remember(cacheKey) { mutableStateOf(false) }
    
    LaunchedEffect(cacheKey) {
        // 이미 렌더링 중이거나 완료된 경우 스킵
        if (renderedViews == null && !isRendering) {
            isRendering = true
            try {
                renderedViews = glanceRemoteViews.compose(context, size, content = content).remoteViews
            } finally {
                isRendering = false
            }
        }
    }

    // 렌더링이 완료되면 RemoteViews 표시, 아니면 빈 Box (깜박임 방지)
    if (renderedViews != null) {
        RemoteViewsContent(
            modifier = Modifier.size(size),
            remoteViews = renderedViews
        )
    } else {
        // 렌더링 중일 때는 투명한 Box로 공간 유지 (깜박임 방지)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(size)
        )
    }
}

/**
 * 최적화된 AppWidgetView - layout과 renderer를 미리 생성하여 전달
 * 깜박임을 최소화하기 위해 사용
 */
@OptIn(ExperimentalGlanceRemoteViewsApi::class)
@Composable
fun AppWidgetView(
    size: DpSize,
    layout: WidgetLayoutDocument,
    renderer: GlanceRenderer
) {
    val context = LocalContext.current
    val glanceRemoteViews = remember { GlanceRemoteViews() }
    
    // layout을 기반으로 캐시 키 생성
    val cacheKey = remember(size, layout) {
        "${size.width.value}_${size.height.value}_${layout.hashCode()}"
    }
    
    // RemoteViews를 캐싱하여 재렌더링 방지
    var renderedViews by remember(cacheKey) { mutableStateOf<RemoteViews?>(null) }
    
    LaunchedEffect(cacheKey) {
        // 이미 렌더링된 경우 스킵
        if (renderedViews == null) {
            renderedViews = glanceRemoteViews.compose(context, size) {
                renderer.render(layout)
            }.remoteViews
        }
    }

    // 렌더링이 완료되면 RemoteViews 표시, 아니면 빈 Box (깜박임 방지)
    if (renderedViews != null) {
        RemoteViewsContent(
            modifier = Modifier.size(size),
            remoteViews = renderedViews
        )
    } else {
        // 렌더링 중일 때는 투명한 Box로 공간 유지 (깜박임 방지)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(size)
        )
    }
}

@Composable
internal fun RemoteViewsContent(
    modifier: Modifier = Modifier,
    remoteViews: RemoteViews?
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FrameLayout(context)
        },
        update = { view ->
            view.removeAllViews()
            remoteViews?.let { rv ->
                val content = rv.apply(context, view)
                view.addView(content)
            }
        })
}