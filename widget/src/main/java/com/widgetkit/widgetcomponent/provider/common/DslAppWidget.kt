package com.widgetkit.widgetcomponent.provider.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.LocalState
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LocalAppWidgetOptions
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import com.widgetkit.widgetcomponent.provider.getExactWidgetSizeInDp
import com.widgetkit.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetkit.widgetcomponent.util.getSystemContentRadius
import com.widgetkit.core.WidgetLayout
import com.widgetkit.core.WidgetScope
import com.widgetkit.core.frontend.layout.Box
import com.widgetkit.core.proto.WidgetMode
import com.widgetkit.core.proto.modifier.WidgetModifier
import com.widgetkit.core.proto.modifier.backgroundColor
import com.widgetkit.core.proto.modifier.cornerRadius
import com.widgetkit.core.proto.modifier.height
import com.widgetkit.core.proto.modifier.width
import com.widgetkit.core.widget.WidgetRenderer
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalBackgroundRadius
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalContentRadius
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalTheme

internal abstract class DslAppWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    companion object {
        internal val WIDGET_SYNC_KEY = longPreferencesKey("widget_sync_key")
    }

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            androidx.glance.layout.Box(
                modifier = GlanceModifier.Companion.fillMaxSize()
                    .background(Color.Companion.Transparent),
                contentAlignment = Alignment.Companion.Center
            ) {
                RenderDsl()
            }
        }
    }

    @Composable
    private fun RenderDsl() {
        val state = LocalState.current as Preferences
        val appWidgetOptions = LocalAppWidgetOptions.current
        val context = LocalContext.current
        val glanceId = LocalGlanceId.current
        val backgroundRadius = remember { context.getSystemBackgroundRadius() }
        val contentRadius = remember { context.getSystemContentRadius() }
        val theme = GlanceTheme.colors

        val dpSize = getExactWidgetSizeInDp(context, appWidgetOptions)
        WidgetRenderer(context).render(WidgetLayout(mode = WidgetMode.WIDGET_MODE_NORMAL) {
            WidgetLocalProvider(
                WidgetLocalSize provides DpSize(dpSize.width.dp, dpSize.height.dp),
                WidgetLocalContext provides context,
                WidgetLocalState provides state,
                WidgetLocalGlanceId provides glanceId,
                WidgetLocalBackgroundRadius provides backgroundRadius,
                WidgetLocalContentRadius provides contentRadius,
                WidgetLocalTheme provides theme
            ) {
                Box(
                    modifier = WidgetModifier.Companion
                        .width(dpSize.width)
                        .height(dpSize.height)
                        .backgroundColor(Color.Companion.Transparent.toArgb())
                        .cornerRadius(backgroundRadius.value)
                ) {
                    DslContent()
                }
            }
        })
    }

    abstract fun WidgetScope.DslContent()
}