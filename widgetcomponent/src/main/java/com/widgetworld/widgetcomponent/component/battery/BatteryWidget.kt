package com.widgetworld.widgetcomponent.component.battery

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.glance.color.DynamicThemeColorProviders
import com.widgetworld.widgetcomponent.R
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.component.viewid.ViewIdType
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.frontend.Image
import com.widgetworld.core.frontend.Text
import com.widgetworld.core.frontend.layout.Box
import com.widgetworld.core.frontend.layout.Column
import com.widgetworld.core.frontend.layout.Row
import com.widgetworld.core.proto.AlignmentType
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.proto.HorizontalAlignment
import com.widgetworld.core.proto.VerticalAlignment
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.modifier.backgroundColor
import com.widgetworld.core.proto.modifier.fillMaxHeight
import com.widgetworld.core.proto.modifier.fillMaxWidth
import com.widgetworld.core.proto.modifier.height
import com.widgetworld.core.proto.modifier.hide
import com.widgetworld.core.proto.modifier.padding
import com.widgetworld.core.proto.modifier.partiallyUpdate
import com.widgetworld.core.proto.modifier.viewId
import com.widgetworld.core.proto.modifier.width
import com.widgetworld.core.proto.modifier.wrapContentHeight
import com.widgetworld.core.proto.modifier.wrapContentWidth
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalTheme

class BatteryWidget : WidgetComponent() {

    override fun getName() = "Battery"

    override fun getDescription() = "Battery"

    override fun getWidgetCategory() = WidgetCategory.DEVICE_STATUS

    override fun getWidgetTag() = "Battery"

    override fun getSizeType() = SizeType.TINY

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val backgroundColor = theme.surface.getColor(context).toArgb()

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Column(contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }) {
                BatteryIcon()
                BatteryDescription()
                BatteryText()
            }
        }
    }

    private fun WidgetScope.BatteryDescription() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()

        Text(
            text = "Battery",
            fontSize = 12f,
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
            fontColor = Color(textColor)
        )
    }

    private fun WidgetScope.BatteryText() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurface.getColor(context).toArgb()
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val batteryValueText = "${getBatteryValue().toInt()}"
        val size = getLocal(WidgetLocalSize) as DpSize
        val textSize = size.height.value * 0.12f
        Row(
            modifier = WidgetModifier
                .wrapContentWidth()
                .wrapContentHeight(),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_BOTTOM
            }
        ) {
            Text(
                modifier = WidgetModifier
                    .viewId(getBatteryTextId(gridIndex))
                    .partiallyUpdate(true)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                text = batteryValueText,
                fontSize = textSize,
                fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                fontColor = Color(textColor)
            )
            Text(
                modifier = WidgetModifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 2f),
                text = "%",
                fontSize = textSize * 0.6f,
                fontColor = Color(textColor),
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
            )
        }
    }

    private fun WidgetScope.BatteryIcon() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val iconColor = theme.primary.getColor(context).toArgb()
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        Box(
            modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Image(
                modifier = WidgetModifier
                    .width(height * 0.34f)
                    .height(height * 0.34f),
                contentProperty = {
                    Provider {
                        drawableResId = R.drawable.ic_mobile_device
                    }
                    TintColor {
                        argb = iconColor
                    }
                }
            )
            ChargingIcon()
        }
    }

    private fun WidgetScope.ChargingIcon() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val iconColor = theme.primary.getColor(context).toArgb()
        fun WidgetScope.getChargingIconSize(): Float {
            val size = getLocal(WidgetLocalSize) as DpSize
            return size.height.value * 0.34f
        }

        val iconSize = getChargingIconSize()
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        Image(
            modifier = WidgetModifier
                .viewId(getChargingIconId(gridIndex))
                .height(iconSize)
                .width(iconSize)
                .hide(!getChargingState()),
            contentProperty = {
                Provider {
                    drawableResId = R.layout.battery_charging_avd
                }
                TintColor {
                    argb = iconColor
                }
                animation = true
                infiniteLoop = true
            }
        )
    }

    private fun WidgetScope.getBatteryValue(): Float {
        val currentState = getLocal(WidgetLocalState)
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        if (isPreview) {
            return 50f
        }
        val value = currentState?.let { state ->
            val currentValue = state[BatteryPreferenceKey.Level]
            currentValue ?: 0f
        } ?: 0f

        return value
    }

    private fun WidgetScope.getChargingState(): Boolean {
        val currentState = getLocal(WidgetLocalState)
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        if (isPreview) {
            return false
        }
        return currentState?.let { state ->
            val currentValue = state[BatteryPreferenceKey.Charging]
            currentValue ?: false
        } ?: false
    }

    // ViewIdProvider 구현
    override fun getViewIdTypes(): List<ViewIdType> {
        return BatteryViewIdType.all()
    }

    fun getBatteryTextId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.Text, gridIndex)
    }

    fun getBatteryIconId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.Icon, gridIndex)
    }

    fun getChargingIconId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.ChargingIcon, gridIndex)
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = BatteryUpdateManager

    override fun getDataStore() = BatteryComponentDataStore
}
