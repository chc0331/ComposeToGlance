package com.widgetkit.widgetcomponent.component.battery.bluetooth.earbuds

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
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.Image
import com.widgetkit.dsl.frontend.Text
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.frontend.layout.Column
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
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme

class EarbudsBatteryWidget : WidgetComponent() {

    override fun getName(): String = "EarbudsBattery"

    override fun getDescription(): String = "EarbudsBattery"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_STATUS

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "EarbudsBattery"

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
                EarbudsIcon()
                EarbudsTitle()
                EarbudsBatteryText()
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = EarbudsBatteryUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = EarbudsBatteryDataStore

    private fun WidgetScope.EarbudsIcon() {
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
            currentState[EarbudsBatteryPreferenceKey.BatteryConnected] ?: false
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
                    .viewId(getEarbudsIconId(gridIndex))
                    .partiallyUpdate(true)
                    .width(height * 0.34f)
                    .height(height * 0.34f),
                contentProperty = {
                    Provider {
                        drawableResId = R.drawable.ic_bluetooth_earbuds
                    }
                    TintColor {
                        argb = if (isConnected) iconColor else Color.LightGray.toArgb()
                    }
                }
            )
        }
    }

    private fun WidgetScope.EarbudsTitle() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()

        Text(
            text = "EarBuds",
            fontSize = 12f,
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
            fontColor = Color(textColor)
        )
    }

    private fun WidgetScope.EarbudsBatteryText() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurface.getColor(context).toArgb()
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        val batteryLevel = if (isPreview) {
            50f
        } else {
            currentState[EarbudsBatteryPreferenceKey.BatteryLevel] ?: 0f
        }
        val isConnected = if (isPreview) {
            true
        } else {
            currentState[EarbudsBatteryPreferenceKey.BatteryConnected] ?: false
        }
        val size = getLocal(WidgetLocalSize) as DpSize
        val textSize = size.height.value * 0.12f

        Text(
            modifier = WidgetModifier
                .viewId(getEarbudsTextId(gridIndex))
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
        return EarbudsBatteryViewIdType.all()
    }

    fun getEarbudsTextId(gridIndex: Int): Int {
        return generateViewId(EarbudsBatteryViewIdType.BatteryText, gridIndex)
    }

    fun getEarbudsIconId(gridIndex: Int): Int {
        return generateViewId(EarbudsBatteryViewIdType.BatteryIcon, gridIndex)
    }
}
