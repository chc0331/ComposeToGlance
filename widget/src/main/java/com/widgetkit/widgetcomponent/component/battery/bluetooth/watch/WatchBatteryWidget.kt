package com.widgetkit.widgetcomponent.component.battery.bluetooth.watch

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.emptyPreferences
import androidx.glance.color.DynamicThemeColorProviders
import com.widgetkit.widgetcomponent.R
import com.widgetkit.widgetcomponent.SizeType
import com.widgetkit.widgetcomponent.WidgetCategory
import com.widgetkit.widgetcomponent.component.WidgetComponent
import com.widgetkit.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetkit.widgetcomponent.component.viewid.ViewIdType
import com.widgetkit.core.WidgetScope
import com.widgetkit.core.frontend.Image
import com.widgetkit.core.frontend.Text
import com.widgetkit.core.frontend.layout.Box
import com.widgetkit.core.frontend.layout.Column
import com.widgetkit.core.proto.AlignmentType
import com.widgetkit.core.proto.FontWeight
import com.widgetkit.core.proto.HorizontalAlignment
import com.widgetkit.core.proto.VerticalAlignment
import com.widgetkit.core.proto.modifier.WidgetModifier
import com.widgetkit.core.proto.modifier.backgroundColor
import com.widgetkit.core.proto.modifier.fillMaxHeight
import com.widgetkit.core.proto.modifier.fillMaxWidth
import com.widgetkit.core.proto.modifier.height
import com.widgetkit.core.proto.modifier.partiallyUpdate
import com.widgetkit.core.proto.modifier.viewId
import com.widgetkit.core.proto.modifier.width
import com.widgetkit.core.proto.modifier.wrapContentHeight
import com.widgetkit.core.proto.modifier.wrapContentWidth
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalTheme

class WatchBatteryWidget : WidgetComponent() {

    override fun getName(): String = "WatchBattery"

    override fun getDescription(): String = "WatchBattery"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_STATUS

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "WatchBattery"

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val backgroundColor = theme.surface.getColor(context).toArgb()
        val localSize = getLocal(WidgetLocalSize) as DpSize

        Box(
            modifier = WidgetModifier
                .fillMaxWidth().fillMaxHeight().backgroundColor(backgroundColor),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Column(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                WatchIcon()
                WatchTitle()
                WatchBatteryText()
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = WatchBatteryUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = WatchBatteryDataStore

    private fun WidgetScope.WatchIcon() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val iconColor = theme.primary.getColor(context).toArgb()
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
                        argb = if (isConnected) iconColor else Color.LightGray.toArgb()
                    }
                }
            )
        }
    }

    private fun WidgetScope.WatchTitle() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()

        Text(
            text = "Watch",
            fontSize = 12f,
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
            fontColor = Color(textColor)
        )
    }

    private fun WidgetScope.WatchBatteryText() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurface.getColor(context).toArgb()
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
            text = if (isConnected) {
                "${batteryLevel.toInt()}%"
            } else {
                "--"
            },
            fontSize = textSize,
            fontWeight = FontWeight.FONT_WEIGHT_BOLD,
            fontColor = Color(textColor)
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
