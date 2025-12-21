package com.example.widget.provider

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.LocalSize
import androidx.glance.LocalState
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.example.dsl.WidgetLayout
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.widgetlocalprovider.WidgetLocalBackgroundRadius
import com.example.dsl.widgetlocalprovider.WidgetLocalContentRadius
import com.example.dsl.widgetlocalprovider.WidgetLocalContext
import com.example.dsl.widgetlocalprovider.WidgetLocalGlanceId
import com.example.dsl.widgetlocalprovider.WidgetLocalProvider
import com.example.dsl.widgetlocalprovider.WidgetLocalSize
import com.example.dsl.widgetlocalprovider.WidgetLocalState
import com.example.dsl.dsl.modifier.WidgetModifier
import com.example.dsl.dsl.modifier.backgroundColor
import com.example.dsl.dsl.modifier.cornerRadius
import com.example.dsl.dsl.modifier.height
import com.example.dsl.dsl.modifier.width
import com.example.dsl.proto.WidgetLayoutDocument
import com.example.dsl.widget.WidgetRenderer
import com.example.widget.R
import com.example.widget.util.getSystemBackgroundRadius
import com.example.widget.util.getSystemContentRadius

abstract class DslAppWidget : GlanceAppWidget() {

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            androidx.glance.layout.Box(
                modifier = GlanceModifier.fillMaxSize().background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                val remoteviews = remember {
                    RemoteViews(
                        context.packageName,
                        R.layout.root_layout
                    )
                }
                AndroidRemoteViews(
                    remoteViews = remoteviews,
                    containerViewId = R.id.widgetRoot
                ) {
                    RenderDsl()
                }
            }
        }
    }

    @Composable
    private fun RenderDsl() {
        val state = LocalState.current as Preferences
        val context = LocalContext.current
        val dpSize = LocalSize.current
        val glanceId = LocalGlanceId.current
        val renderer = remember { WidgetRenderer(context) }
        val backgroundRadius = remember { context.getSystemBackgroundRadius() }
        val contentRadius = remember { context.getSystemContentRadius() }
        var renderContent by remember { mutableStateOf<WidgetLayoutDocument?>(null) }

        LaunchedEffect(state) {
            renderContent = WidgetLayout {
                WidgetLocalProvider(
                    WidgetLocalSize provides dpSize,
                    WidgetLocalContext provides context,
                    WidgetLocalState provides state,
                    WidgetLocalGlanceId provides glanceId,
                    WidgetLocalBackgroundRadius provides backgroundRadius,
                    WidgetLocalContentRadius provides contentRadius
                ) {
                    Box(
                        modifier = WidgetModifier
                            .width(dpSize.width.value)
                            .height(dpSize.height.value)
                            .backgroundColor(Color.Transparent.toArgb())
                            .cornerRadius(backgroundRadius.value)
                    ) {
                        DslContent()
                    }
                }
            }
        }
        if (renderContent == null) {
            LoadingContent()
        } else {
            renderer.render(renderContent!!)
        }
    }

    @Composable
    private fun LoadingContent() {
        androidx.glance.layout.Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
    }

    abstract fun WidgetScope.DslContent()
}
