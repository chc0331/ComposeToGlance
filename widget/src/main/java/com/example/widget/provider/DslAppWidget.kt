package com.example.widget.provider

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.dsl.WidgetLayout
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.modifier.*
import com.example.dsl.localprovider.WidgetLocalBackgroundRadius
import com.example.dsl.localprovider.WidgetLocalContentRadius
import com.example.dsl.localprovider.WidgetLocalContext
import com.example.dsl.localprovider.WidgetLocalGlanceId
import com.example.dsl.localprovider.WidgetLocalProvider
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
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
        val renderer = remember { GlanceRenderer(context) }
        val backgroundRadius = remember { context.getSystemBackgroundRadius() }
        val contentRadius = remember { context.getSystemContentRadius() }
        renderer.render(
            WidgetLayout {
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
                            .backgroundColor(Color.LightGray.toArgb())
                            .cornerRadius(backgroundRadius.value)
                    ) {
                        DslContent()
                    }
                }
            }
        )
    }

    abstract fun WidgetScope.DslContent()
}
