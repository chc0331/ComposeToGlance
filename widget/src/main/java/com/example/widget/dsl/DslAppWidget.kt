package com.example.widget.dsl

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.example.dsl.DslLocalProvider
import com.example.dsl.DslLocalSize
import com.example.dsl.WidgetLayout
import com.example.dsl.WidgetScope
import com.example.dsl.glance.GlanceRenderer

abstract class DslAppWidget : GlanceAppWidget() {

    final override val sizeMode: SizeMode
        get() = SizeMode.Responsive(
            setOf(
                DpSize(150.dp, 100.dp),
                DpSize(250.dp, 150.dp)
            )
        )

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            RenderDsl()
        }
    }

    @Composable
    private fun RenderDsl() {
        val context = LocalContext.current
        val dpSize = androidx.glance.LocalSize.current
        val renderer = remember { GlanceRenderer(context) }
        renderer.render(WidgetLayout {
            DslLocalProvider(DslLocalSize provides dpSize) {
                DslContent()
            }
        })
    }

    abstract fun WidgetScope.DslContent()
}