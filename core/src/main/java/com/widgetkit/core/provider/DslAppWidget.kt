package com.widgetkit.core.provider

import android.content.Context
import android.util.Log
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.widgetkit.dsl.WidgetLayout
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalBackgroundRadius
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContentRadius
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.WidgetLayoutDocument
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.core.R
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.core.util.getSystemContentRadius
import com.widgetkit.dsl.frontend.layout.Box

abstract class DslAppWidget : GlanceAppWidget() {

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            androidx.glance.layout.Box(
                modifier = GlanceModifier.fillMaxSize().background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                RenderDsl()
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
