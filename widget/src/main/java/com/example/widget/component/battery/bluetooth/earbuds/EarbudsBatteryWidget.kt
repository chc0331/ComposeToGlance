package com.example.widget.component.battery.bluetooth.earbuds

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.emptyPreferences
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Text
import com.example.dsl.localprovider.WidgetLocalGridIndex
import com.example.dsl.localprovider.WidgetLocalPreview
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.backgroundColor
import com.example.dsl.modifier.fillMaxHeight
import com.example.dsl.modifier.fillMaxWidth
import com.example.dsl.modifier.height
import com.example.dsl.modifier.partiallyUpdate
import com.example.dsl.modifier.viewId
import com.example.dsl.modifier.width
import com.example.dsl.modifier.wrapContentHeight
import com.example.dsl.modifier.wrapContentWidth
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.VerticalAlignment
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.datastore.ComponentDataStore
import com.example.widget.component.lifecycle.ComponentLifecycle
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType

class EarbudsBatteryWidget : WidgetComponent() {

    override fun getName(): String = "EarbudsBattery"

    override fun getDescription(): String = "EarbudsBattery"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "EarbudsBattery"

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
                EarbudsIcon()
                EarbudsTitle()
                EarbudsBatteryText()
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = EarbudsBatteryUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = EarbudsBatteryDataStore

    override fun getLifecycle(): ComponentLifecycle? = null

    override fun requiresAutoLifecycle(): Boolean = false

    private fun WidgetScope.EarbudsIcon() {
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
                        argb = if (isConnected) Color.Transparent.toArgb() else Color.LightGray.toArgb()
                    }
                }
            )
        }
    }

    private fun WidgetScope.EarbudsTitle() {
        Text {
            TextContent {
                text = "EarBuds"
            }
            fontSize = 12f
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
        }
    }

    private fun WidgetScope.EarbudsBatteryText() {
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
        return EarbudsBatteryViewIdType.all()
    }

    fun getEarbudsTextId(gridIndex: Int): Int {
        return generateViewId(EarbudsBatteryViewIdType.BatteryText, gridIndex)
    }

    fun getEarbudsIconId(gridIndex: Int): Int {
        return generateViewId(EarbudsBatteryViewIdType.BatteryIcon, gridIndex)
    }
}
