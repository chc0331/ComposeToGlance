package com.example.widget.provider

import android.R.attr.text
import android.R.attr.top
import android.R.attr.value
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color.argb
import android.util.Log
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
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.provider.DslLocalCellHeight
import com.example.dsl.provider.DslLocalCellWidth
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
                DpSize(300.dp, 150.dp),
                DpSize(250.dp, 120.dp)
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
        val widgetSize = getLocal(DslLocalSize) ?: DpSize(0.dp, 0.dp)
        val cellWidth = getLocal(DslLocalCellWidth)
        val cellHeight = getLocal(DslLocalCellHeight)
        val gridIndex = widget.gridIndex

        val topMargin = cellHeight?.times((gridIndex - 1) / 4) ?: 0.dp
        val leftMargin = cellWidth?.times((gridIndex - 1) % 4) ?: 0.dp

        Log.i("heec.choi", "GridItem / $topMargin $leftMargin $gridIndex")

        Box({
            ViewProperty {
                Width { matchParent = true }
                Height { matchParent = true }
                Padding {
                    start = leftMargin.value
                    top = topMargin.value
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
                    BackgroundColor {
                        Color {
                            argb = Color.Black.toArgb()
                        }
                    }
                }
            })
            {
                Text {
                    TextContent {
                        text = widget.widgetTag
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
