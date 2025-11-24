package com.example.widget.dsl

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.LocalState
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.example.dsl.provider.DslLocalProvider
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.WidgetLayout
import com.example.dsl.WidgetScope
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.provider.DslLocalContext
import com.example.dsl.provider.DslLocalGlanceId
import com.example.dsl.provider.DslLocalState

abstract class DslAppWidget : GlanceAppWidget() {

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            RenderDsl()
        }
    }

    @Composable
    private fun RenderDsl() {
        val state = LocalState.current as Preferences
        val context = LocalContext.current
        val dpSize = androidx.glance.LocalSize.current
        val glanceId = LocalGlanceId.current
        val renderer = remember { GlanceRenderer(context) }
        renderer.render(WidgetLayout {
            DslLocalProvider(
                DslLocalSize provides dpSize,
                DslLocalContext provides context,
                DslLocalState provides state,
                DslLocalGlanceId provides glanceId
            ) {
                DslContent()
            }
        })
    }

    abstract fun WidgetScope.DslContent()
}