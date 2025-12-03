package com.example.dsl.provider

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId

val DslLocalPreview = WidgetLocal.of(false)
val DslLocalSize = WidgetLocal.of<DpSize>()
val DslLocalContext = WidgetLocal.of<Context>()
val DslLocalState = WidgetLocal.of<Preferences>()
val DslLocalGlanceId = WidgetLocal.of<GlanceId>()
val DslLocalCellWidth = WidgetLocal.of<Dp>()
val DslLocalCellHeight = WidgetLocal.of<Dp>()
val DslLocalGridIndex = WidgetLocal.of<Int>(0)
val DslLocalBackgroundRadius = WidgetLocal.of<Dp>()
val DslLocalContentRadius = WidgetLocal.of<Dp>()