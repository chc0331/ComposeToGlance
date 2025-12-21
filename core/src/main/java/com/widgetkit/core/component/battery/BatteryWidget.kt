package com.widgetkit.core.component.battery

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.component.Box
import com.widgetkit.dsl.component.Column
import com.widgetkit.dsl.component.Image
import com.widgetkit.dsl.component.Row
import com.widgetkit.dsl.component.Text
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widgetlocalprovider.WidgetLocalState
import com.widgetkit.core.R
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.dsl.proto.modifier.viewId
import com.widgetkit.dsl.proto.modifier.partiallyUpdate
import com.widgetkit.dsl.proto.modifier.hide
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.modifier.wrapContentHeight
import com.widgetkit.dsl.proto.modifier.wrapContentWidth
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.component.viewid.ViewIdType

class BatteryWidget : WidgetComponent() {

    override fun getName() = "Battery"

    override fun getDescription() = "Battery"

    override fun getWidgetCategory() = WidgetCategory.DEVICE_INFO

    override fun getWidgetTag() = "Battery"

    override fun getSizeType() = SizeType.TINY

    override fun WidgetScope.Content() {
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(Color.White.toArgb()),
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
        Text {
            TextContent {
                text = "Battery"
            }
            fontSize = 12f
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
        }
    }

    private fun WidgetScope.BatteryText() {
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
                contentProperty = {
                    TextContent {
                        text = batteryValueText
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
            Text(
                modifier = WidgetModifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(bottom = 2f),
                contentProperty = {
                    TextContent {
                        text = "%"
                    }
                    fontSize = textSize * 0.6f
                    FontColor {
                        Color {
                            argb = Color.Black.toArgb()
                        }
                    }
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                }
            )
        }
    }

    private fun WidgetScope.BatteryIcon() {
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        Box(modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(), contentProperty = {
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
                }
            )
            ChargingIcon()
        }

    }

    private fun WidgetScope.ChargingIcon() {
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

    // BroadcastReceiver는 WidgetForegroundService에서 관리하므로 Lifecycle 불필요
    override fun getLifecycle() = null

    override fun requiresAutoLifecycle() = false
}
