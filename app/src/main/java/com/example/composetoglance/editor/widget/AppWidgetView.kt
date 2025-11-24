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


@OptIn(ExperimentalGlanceRemoteViewsApi::class)
@Composable
internal fun AppWidgetView(
    size: DpSize,
    content: @Composable @GlanceComposable () -> Unit
) {
    val context = LocalContext.current
    var remoteViews by remember { mutableStateOf<RemoteViews?>(null) }
    val glanceRemoteViews = remember { GlanceRemoteViews() }

    LaunchedEffect(Unit) {
        remoteViews = glanceRemoteViews.compose(context, size, content = content).remoteViews
    }

    RemoteViewsContent(
        modifier = Modifier.size(size),
        remoteViews = remoteViews
    )
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