package com.example.widget.provider

import android.R.attr.value
import android.content.Context
import android.graphics.Color.argb
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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import com.example.dsl.WidgetLayout
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.provider.DslLocalContext
import com.example.dsl.provider.DslLocalGlanceId
import com.example.dsl.provider.DslLocalProvider
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.provider.DslLocalState

abstract class DslAppWidget : GlanceAppWidget() {

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val size = LocalSize.current
            androidx.glance.layout.Box(
                modifier = GlanceModifier.fillMaxSize().background(Color.White),
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
        val renderer = remember { GlanceRenderer(context) }
        renderer.render(
            WidgetLayout {
                DslLocalProvider(
                    DslLocalSize provides dpSize,
                    DslLocalContext provides context,
                    DslLocalState provides state,
                    DslLocalGlanceId provides glanceId
                ) {
                    Box({
                        ViewProperty {
                            Width { Dp { value = dpSize.width.value } }
                            Height { Dp { value = dpSize.height.value } }
                            BackgroundColor { Color { argb = Color.LightGray.toArgb() } }
                        }
                    }) {
                        DslContent()
                    }
                }
            }
        )
    }

    abstract fun WidgetScope.DslContent()
}