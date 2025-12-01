package com.example.widget.provider

import WidgetComponentRegistry
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.proto.AlignmentType
import com.example.dsl.provider.DslLocalCellHeight
import com.example.dsl.provider.DslLocalCellWidth
import com.example.dsl.provider.DslLocalContentRadius
import com.example.dsl.provider.DslLocalProvider
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.provider.DslLocalState
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.proto.WidgetLayout
import com.example.widget.repository.WidgetLayoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val layoutKey = byteArrayPreferencesKey("layout_key")

class LargeAppWidget : DslAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Responsive(
            setOf(
                DpSize(400.dp, 250.dp),
                DpSize(350.dp, 200.dp),
                DpSize(330.dp, 180.dp)
            )
        )

    override fun WidgetScope.DslContent() {
        val currentState = getLocal(DslLocalState) as Preferences?
        val currentLayout = WidgetLayout.parseFrom(currentState?.get(layoutKey))
        val widgetSize = getLocal(DslLocalSize) as DpSize

        DslLocalProvider(
            DslLocalCellWidth provides (widgetSize.width / 4),
            DslLocalCellHeight provides (widgetSize.height / 2)
        ) {
            Box({
                ViewProperty {
                    Width { matchParent = true }
                    Height { matchParent = true }
                }
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START

            }) {
                currentLayout.placedWidgetComponentList.forEach {
                    GridItem(it)
                }
            }
        }
    }

    private fun WidgetScope.GridItem(widget: PlacedWidgetComponent) {
        val cellWidth = getLocal(DslLocalCellWidth)
        val cellHeight = getLocal(DslLocalCellHeight)
        val gridIndex = widget.gridIndex

        val topMargin = cellHeight?.times((gridIndex - 1) / 4) ?: 0.dp
        val leftMargin = cellWidth?.times((gridIndex - 1) % 4) ?: 0.dp
        Box({
            ViewProperty {
                Width { matchParent = true }
                Height { matchParent = true }
                Padding {
                    start = leftMargin.value
                    top = topMargin.value
                }
                BackgroundColor {
                    Color {
                        argb = Color.Transparent.toArgb()
                    }
                }
            }
        }) {
            val componentWidth = cellWidth?.times(widget.colSpan) ?: 0.dp
            val componentHeight = cellHeight?.times(widget.rowSpan) ?: 0.dp
            Box({
                ViewProperty {
                    Width {
                        Dp {
                            value = componentWidth.value
                        }
                    }
                    Height {
                        Dp {
                            value = componentHeight.value
                        }
                    }
                }
            })
            {
                DslLocalProvider(DslLocalSize provides DpSize(componentWidth, componentHeight)) {
                    val contentRadius = getLocal(DslLocalContentRadius) ?: 0.dp
                    WidgetComponentRegistry.getComponent(widget.widgetTag)?.let {
                        Box({
                            ViewProperty {
                                CornerRadius {
                                    radius = contentRadius.value
                                }
                            }
                        }) {
                            it.renderContent(this)
                        }
                    }
                }
            }
        }
    }
}

class LargeWidgetProvider : GlanceAppWidgetReceiver() {

    companion object {
        private const val TAG = "LargeWidgetProvider"
    }

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            CoroutineScope(Dispatchers.Default).launch {
                val repository = WidgetLayoutRepository(context)
                val widgetLayoutData = repository.fetchData()
                val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                val glanceManager = GlanceAppWidgetManager(context)
                appWidgetIds?.forEach {
                    val glanceId = glanceManager.getGlanceIdBy(it)
                    updateAppWidgetState(context, glanceId) {
                        it[layoutKey] = widgetLayoutData.toByteArray()
                    }
                }
            }
        }
    }
}
