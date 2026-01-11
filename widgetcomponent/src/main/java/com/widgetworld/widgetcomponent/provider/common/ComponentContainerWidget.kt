package com.widgetworld.widgetcomponent.provider.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
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
import com.widgetworld.widgetcomponent.provider.getExactWidgetSizeInDp
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetworld.widgetcomponent.util.getSystemContentRadius
import com.widgetworld.core.WidgetLayout
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.frontend.layout.Box
import com.widgetworld.core.proto.AlignmentType
import com.widgetworld.core.proto.WidgetMode
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.modifier.backgroundColor
import com.widgetworld.core.proto.modifier.cornerRadius
import com.widgetworld.core.proto.modifier.fillMaxHeight
import com.widgetworld.core.proto.modifier.fillMaxWidth
import com.widgetworld.core.proto.modifier.height
import com.widgetworld.core.proto.modifier.padding
import com.widgetworld.core.proto.modifier.width
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalBackgroundRadius
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalCellHeight
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalCellWidth
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContentPadding
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContentRadius
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalRootPadding
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import com.widgetworld.widgetcomponent.proto.SizeType
import com.widgetworld.widgetcomponent.proto.WidgetLayout

internal abstract class ComponentContainerWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    companion object {
        internal val WIDGET_SYNC_KEY = longPreferencesKey("widget_sync_key")
        internal val layoutKey = byteArrayPreferencesKey("layout_key")
        internal const val ROOT_PADDING = 4.0f
        internal const val CONTENT_PADDING = 2.0f
    }

    final override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            androidx.glance.layout.Box(
                modifier = GlanceModifier.fillMaxSize()
                    .background(Color.Companion.Transparent),
                contentAlignment = Alignment.Companion.Center
            ) {
                WidgetComponentContainer()
            }
        }
    }

    @Composable
    private fun WidgetComponentContainer() {
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
                    WidgetComponentGrid()
                }
            }
        })
    }

    private fun WidgetScope.WidgetComponentGrid() {
        val context = getLocal(WidgetLocalContext) as Context
        val currentState = getLocal(WidgetLocalState)
        val currentLayout = WidgetLayout.parseFrom(currentState?.get(layoutKey))
        val widgetSize = getLocal(WidgetLocalSize) as DpSize
        val (column, row) = getGridSpec(currentLayout.sizeType)
        val cellWidth = (widgetSize.width - ROOT_PADDING.dp * 2) / column
        val cellHeight = (widgetSize.height - ROOT_PADDING.dp * 2) / row
        WidgetLocalProvider(
            WidgetLocalRootPadding provides ROOT_PADDING.dp,
            WidgetLocalContentPadding provides CONTENT_PADDING.dp,
            WidgetLocalCellWidth provides cellWidth,
            WidgetLocalCellHeight provides cellHeight
        ) {
            Box(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
                }) {
                val widgetComponentList = currentLayout.placedWidgetComponentList
                widgetComponentList.forEach {
                    WidgetComponentGridItem(it, column)
                }
            }
        }
    }

    private fun WidgetScope.WidgetComponentGridItem(widget: PlacedWidgetComponent, column: Int) {
        val rootPadding = getLocal(WidgetLocalRootPadding) as Dp
        val contentPadding = getLocal(WidgetLocalContentPadding) as Dp
        val cellWidth = getLocal(WidgetLocalCellWidth)
        val cellHeight = getLocal(WidgetLocalCellHeight)
        val gridIndex = widget.gridIndex

        val topMargin = rootPadding + (cellHeight?.times((gridIndex - 1) / column) ?: 0.dp)
        val leftMargin = rootPadding + (cellWidth?.times((gridIndex - 1) % column) ?: 0.dp)

        Box(
            modifier = WidgetModifier.fillMaxWidth().fillMaxHeight()
                .padding(start = leftMargin.value, top = topMargin.value)
                .backgroundColor(Color.Transparent.toArgb())
        ) {
            val cellWidth = cellWidth?.times(widget.colSpan) ?: 0.dp
            val cellHeight = cellHeight?.times(widget.rowSpan) ?: 0.dp
            Box(
                modifier = WidgetModifier
                    .width(cellWidth.value)
                    .height(cellHeight.value)
                    .padding(
                        start = contentPadding.value,
                        top = contentPadding.value,
                        bottom = contentPadding.value,
                        end = contentPadding.value
                    )
            ) {
                val componentWidth = cellWidth - contentPadding * 2
                val componentHeight = cellHeight - contentPadding * 2
                WidgetLocalProvider(
                    WidgetLocalSize provides DpSize(
                        componentWidth, componentHeight
                    ),
                    WidgetLocalGridIndex provides gridIndex
                ) {
                    val widgetComponent = WidgetComponentRegistry.getComponent(widget.widgetTag)
                    if (widgetComponent != null) {
                        WidgetComponentContent(
                            modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                            widget = widgetComponent
                        )
                    }
                }
            }
        }
    }

    private fun WidgetScope.WidgetComponentContent(
        modifier: WidgetModifier,
        widget: WidgetComponent
    ) {
        val contentRadius = getLocal(WidgetLocalContentRadius) ?: 0.dp
        Box(modifier = modifier.cornerRadius(contentRadius.value)) {
            widget.renderContent(this)
        }
    }


    private fun getGridSpec(sizeType: SizeType): Pair<Int, Int> {
        return when (sizeType) {
            SizeType.SIZE_TYPE_SMALL -> 2 to 1
            SizeType.SIZE_TYPE_MEDIUM -> 2 to 2
            SizeType.SIZE_TYPE_MEDIUM_PLUS -> 3 to 2
            SizeType.SIZE_TYPE_LARGE -> {
                // ExtraLarge는 항상 4 rows × 4 columns
                4 to 2
            }

            else -> 4 to 4 // 기본값 (ExtraLarge는 항상 4x4)
        }
    }
}

