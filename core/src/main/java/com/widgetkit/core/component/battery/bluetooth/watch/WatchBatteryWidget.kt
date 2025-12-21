package com.widgetkit.core.component.battery.bluetooth.watch

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.emptyPreferences
import com.widgetkit.core.R
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.component.datastore.ComponentDataStore
import com.widgetkit.core.component.lifecycle.ComponentLifecycle
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.component.viewid.ViewIdType
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.partiallyUpdate
import com.widgetkit.dsl.proto.modifier.viewId
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.modifier.wrapContentHeight
import com.widgetkit.dsl.proto.modifier.wrapContentWidth
import com.widgetkit.dsl.ui.Image
import com.widgetkit.dsl.ui.Text
import com.widgetkit.dsl.ui.layout.Box
import com.widgetkit.dsl.ui.layout.Column
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalState

class WatchBatteryWidget : WidgetComponent() {

    override fun getName(): String = "WatchBattery"

    override fun getDescription(): String = "WatchBattery"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "WatchBattery"

    override fun WidgetScope.Content() {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        Box(
            modifier = WidgetModifier
                .fillMaxWidth().fillMaxHeight().backgroundColor(Color.White.toArgb()),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
            Column(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }) {
                WatchIcon()
                WatchTitle()
                WatchBatteryText()
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = WatchBatteryUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = WatchBatteryDataStore

    override fun getLifecycle(): ComponentLifecycle? = null

    override fun requiresAutoLifecycle(): Boolean = false

    private fun WidgetScope.WatchIcon() {
        val size = getLocal(WidgetLocalSize) as DpSize
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        val isConnected = if (isPreview) {
            true
        } else {
            currentState[WatchBatteryPreferenceKey.BatteryConnected] ?: false
        }
        val height = size.height.value

        Box(
            modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Image(
                modifier = WidgetModifier
                    .viewId(getWatchIconId(gridIndex))
                    .partiallyUpdate(true)
                    .width(height * 0.34f)
                    .height(height * 0.34f),
                contentProperty = {
                    Provider {
                        drawableResId = R.drawable.ic_bluetooth_watch
                    }
                    TintColor {
                        argb = if (isConnected) Color.Transparent.toArgb() else Color.LightGray.toArgb()
                    }
                }
            )
        }
    }

    private fun WidgetScope.WatchTitle() {
        Text {
            TextContent {
                text = "Watch"
            }
            fontSize = 12f
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
        }
    }

    private fun WidgetScope.WatchBatteryText() {
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        val batteryLevel = if (isPreview) {
            50f
        } else {
            currentState[WatchBatteryPreferenceKey.BatteryLevel] ?: 0f
        }
        val isConnected = if (isPreview) {
            true
        } else {
            currentState[WatchBatteryPreferenceKey.BatteryConnected] ?: false
        }
        val size = getLocal(WidgetLocalSize) as DpSize
        val textSize = size.height.value * 0.12f

        Text(
            modifier = WidgetModifier
                .viewId(getWatchTextId(gridIndex))
                .partiallyUpdate(true)
                .wrapContentWidth()
                .wrapContentHeight(),
            contentProperty = {
                TextContent {
                    text = if (isConnected) {
                        "${batteryLevel.toInt()}%"
                    } else {
                        "--"
                    }
                }
                fontSize = textSize
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                FontColor {
                    Color {
                        argb = Color.Black.toArgb()
                    }
                }
            }
        )
    }

    override fun getViewIdTypes(): List<ViewIdType> {
        return WatchBatteryViewIdType.all()
    }

    fun getWatchTextId(gridIndex: Int): Int {
        return generateViewId(WatchBatteryViewIdType.BatteryText, gridIndex)
    }

    fun getWatchIconId(gridIndex: Int): Int {
        return generateViewId(WatchBatteryViewIdType.BatteryIcon, gridIndex)
    }
}
