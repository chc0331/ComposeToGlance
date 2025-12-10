package com.example.widget.component.battery

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.modifier.*
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.localprovider.DslLocalGridIndex
import com.example.dsl.localprovider.DslLocalPreview
import com.example.dsl.localprovider.DslLocalSize
import com.example.dsl.localprovider.DslLocalState
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.dsl.modifier.viewId
import com.example.dsl.modifier.partiallyUpdate
import com.example.dsl.modifier.hide
import com.example.dsl.modifier.padding
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType

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
                // Circular Progress와 BatteryIcon을 겹쳐서 배치하는 Box
                Box(contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }) {
                    BatteryProgress()
                    BatteryIcon()
                }
                // 프로그레스 밑에 배터리 용량 텍스트
                Row(
                    modifier = WidgetModifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentProperty = {
                        horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                        verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                    }
                ) {
                    ChargingIcon()
                    BatteryText()
                }
            }
        }
    }

    private fun WidgetScope.BatteryProgress() {
        fun WidgetScope.getProgressSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.58f
        }

        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val batteryValue = getBatteryValue()
        Progress(
            modifier = WidgetModifier
                .viewId(getBatteryProgressId(gridIndex))
                .partiallyUpdate(true)
                .width(getProgressSize())
                .height(getProgressSize()),
            contentProperty = {
                progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                progressValue = batteryValue
                maxValue = 100f
                ProgressColor {
                    Color {
                        resId = R.color.battery_gauge_sufficient_color
                    }
                }
                BackgroundColor {
                    Color {
                        argb = Color.LightGray.toArgb()
                    }
                }
            }
        )
    }

    private fun WidgetScope.BatteryIcon(deviceType: DeviceType = DeviceType.PHONE) {
        fun WidgetScope.getBatteryIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.22f
        }
        Image(
            modifier = WidgetModifier
                .width(getBatteryIconSize())
                .height(getBatteryIconSize()),
            contentProperty = {
                Provider {
                    drawableResId = getDeviceIcon(deviceType)
                }
            }
        )
    }

    private fun WidgetScope.BatteryText() {
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val batteryValueText = "${getBatteryValue().toInt()}"
        val size = getLocal(DslLocalSize) as DpSize
        val textSize = size.height.value * 0.18f
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

    private fun WidgetScope.ChargingIcon() {
        fun WidgetScope.getChargingIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.2f
        }

        val iconSize = getChargingIconSize()
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        Image(
            modifier = WidgetModifier
                .viewId(getChargingIconId(gridIndex))
                .width(iconSize * 0.6f)
                .height(iconSize)
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
        val currentState = getLocal(DslLocalState)
        val isPreview = getLocal(DslLocalPreview) ?: false
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
        val currentState = getLocal(DslLocalState)
        val isPreview = getLocal(DslLocalPreview) ?: false
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

    // View ID Helper 메서드들
    /**
     * 배터리 텍스트 View ID 조회
     */
    fun getBatteryTextId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.Text, gridIndex)
    }

    /**
     * 배터리 프로그레스 View ID 조회
     */
    fun getBatteryProgressId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.Progress, gridIndex)
    }

    /**
     * 배터리 아이콘 View ID 조회
     */
    fun getBatteryIconId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.Icon, gridIndex)
    }

    /**
     * 충전 아이콘 View ID 조회
     */
    fun getChargingIconId(gridIndex: Int): Int {
        return generateViewId(BatteryViewIdType.ChargingIcon, gridIndex)
    }

    override fun getUpdateManager(): ComponentUpdateManager<*>? = BatteryUpdateManager
}
