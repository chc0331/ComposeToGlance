package com.widgetkit.core.provider.common

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.glance.appwidget.SizeMode
import com.widgetkit.core.WidgetComponentRegistry
import com.widgetkit.core.proto.PlacedWidgetComponent
import com.widgetkit.core.proto.SizeType
import com.widgetkit.core.proto.WidgetLayout
import com.widgetkit.core.provider.common.DslAppWidget
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalCellHeight
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalCellWidth
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContentPadding
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContentRadius
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalRootPadding
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalState

internal abstract class CommonAppWidget : DslAppWidget() {

    companion object {
        internal val layoutKey = byteArrayPreferencesKey("layout_key")
        internal const val ROOT_PADDING = 4.0f
        internal const val CONTENT_PADDING = 2.0f
    }


    override fun WidgetScope.DslContent() {
        val widgetSize = getLocal(WidgetLocalSize) as DpSize
        val context = getLocal(WidgetLocalContext) as Context
        val currentState = getLocal(WidgetLocalState) as Preferences?
        val currentLayout = WidgetLayout.parseFrom(currentState?.get(layoutKey))

        // 추상 메서드를 통해 그리드 스펙 계산
        val (rows, columns) = getGridSpec(currentLayout.sizeType, widgetSize)

        val cellWidth = (widgetSize.width - ROOT_PADDING.dp * 2) / columns
        val cellHeight = (widgetSize.height - ROOT_PADDING.dp * 2) / rows
        WidgetLocalProvider(
            WidgetLocalRootPadding provides ROOT_PADDING.dp,
            WidgetLocalContentPadding provides CONTENT_PADDING.dp,
            WidgetLocalCellWidth provides cellWidth,
            WidgetLocalCellHeight provides cellHeight
        ) {
            Box(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
                }
            ) {
                currentLayout.placedWidgetComponentList.forEach {
                    GridItem(it, columns)
                }
            }
        }
    }

    private fun getGridSpec(sizeType: SizeType, widgetSize: DpSize): Pair<Int, Int> {
        //todo : Need to check logic.
        return when (sizeType) {
            SizeType.SIZE_TYPE_SMALL -> 1 to 2
            SizeType.SIZE_TYPE_MEDIUM -> 2 to 2
            SizeType.SIZE_TYPE_MEDIUM_PLUS -> 4 to 6
            SizeType.SIZE_TYPE_LARGE -> {
                // ExtraLarge는 항상 4 rows × 4 columns
                4 to 4
            }

            else -> 4 to 4 // 기본값 (ExtraLarge는 항상 4x4)
        }
    }

    /**
     * 그리드 아이템을 렌더링하는 공통 메서드
     */
    protected fun WidgetScope.GridItem(widget: PlacedWidgetComponent, columns: Int) {
        val rootPadding = getLocal(WidgetLocalRootPadding) as Dp
        val contentPadding = getLocal(WidgetLocalContentPadding) as Dp
        val cellWidth = getLocal(WidgetLocalCellWidth)
        val cellHeight = getLocal(WidgetLocalCellHeight)
        val gridIndex = widget.gridIndex

        val topMargin = rootPadding + (cellHeight?.times((gridIndex - 1) / columns) ?: 0.dp)
        val leftMargin = rootPadding + (cellWidth?.times((gridIndex - 1) % columns) ?: 0.dp)
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = leftMargin.value, top = topMargin.value)
                .backgroundColor(Color.Transparent.toArgb())
        ) {
            val componentWidth = cellWidth?.times(widget.colSpan) ?: 0.dp
            val componentHeight = cellHeight?.times(widget.rowSpan) ?: 0.dp
            Box(
                modifier = WidgetModifier
                    .width(componentWidth.value)
                    .height(componentHeight.value)
                    .padding(
                        start = contentPadding.value,
                        top = contentPadding.value,
                        bottom = contentPadding.value,
                        end = contentPadding.value
                    )
            ) {
                WidgetLocalProvider(
                    WidgetLocalSize provides DpSize(
                        componentWidth - contentPadding * 2,
                        componentHeight - contentPadding * 2
                    ),
                    WidgetLocalGridIndex provides gridIndex
                ) {
                    val contentRadius = getLocal(WidgetLocalContentRadius) ?: 0.dp
                    WidgetComponentRegistry.getComponent(widget.widgetTag)?.let {
                        Box(
                            modifier = WidgetModifier
                                .cornerRadius(contentRadius.value)
                        ) {
                            it.renderContent(this)
                        }
                    }
                }
            }
        }
    }
}

