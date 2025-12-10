package com.example.dsl.localprovider

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId

val WidgetLocalPreview = WidgetLocal.of(false)
val WidgetLocalSize = WidgetLocal.of<DpSize>()
val WidgetLocalContext = WidgetLocal.of<Context>()
val WidgetLocalState = WidgetLocal.of<Preferences>()
val WidgetLocalGlanceId = WidgetLocal.of<GlanceId>()
val WidgetLocalCellWidth = WidgetLocal.of<Dp>()
val WidgetLocalCellHeight = WidgetLocal.of<Dp>()
val WidgetLocalGridIndex = WidgetLocal.of<Int>(0)
val WidgetLocalBackgroundRadius = WidgetLocal.of<Dp>()
val WidgetLocalContentRadius = WidgetLocal.of<Dp>()
val WidgetLocalRootPadding = WidgetLocal.of<Dp>()
val WidgetLocalContentPadding = WidgetLocal.of<Dp>()