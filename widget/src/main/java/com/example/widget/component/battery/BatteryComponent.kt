package com.example.widget.component.battery

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import com.example.dsl.WidgetScope
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.provider.DslLocalGridIndex
import com.example.dsl.provider.DslLocalPreview
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.provider.DslLocalState
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.viewid.ViewIdType

abstract class BatteryComponent : WidgetComponent() {

    companion object {
        internal val batteryValueKey = floatPreferencesKey("battery_value")
        internal val chargingStateKey = booleanPreferencesKey("charging_state")
    }

    fun getCellType(): String {
        return if (getSizeType() == SizeType.TINY) "1x1" else "2x1"
    }

    override fun getName(): String {
        return "${getSizeType()}-Battery"
    }

    override fun getDescription(): String {
        return "${getSizeType()}-Battery"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.DEVICE_INFO
    }

    override fun getWidgetTag(): String {
        return "${getSizeType()}-Battery"
    }

    protected fun WidgetScope.BatteryProgress() {
        fun WidgetScope.getProgressSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.58f
        }

        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val batteryValue = getBatteryValue()
        Progress({
            ViewProperty {
                viewId = getBatteryProgressId(gridIndex)
                partiallyUpdate = true
                Width {
                    Dp {
                        value = getProgressSize()
                    }
                }
                Height {
                    Dp {
                        value = getProgressSize()
                    }
                }
            }
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
                    argb = Color.Companion.LightGray.toArgb()
                }
            }
        })
    }

    protected fun WidgetScope.BatteryIcon(deviceType: DeviceType = DeviceType.PHONE) {
        fun WidgetScope.getBatteryIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.22f
        }
        Image {
            ViewProperty {
                Width { Dp { value = getBatteryIconSize() } }
                Height { Dp { value = getBatteryIconSize() } }
            }
            Provider {
                drawableResId = getDeviceIcon(deviceType)
            }
        }
    }

    protected fun WidgetScope.BatteryText() {
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val batteryValueText = "${getBatteryValue().toInt()}"
        val size = getLocal(DslLocalSize) as DpSize
        val textSize = size.height.value * 0.18f
        Row({
            ViewProperty {
                Width { wrapContent = true }
                Height { wrapContent = true }
            }
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_BOTTOM
        }) {
            Text({
                ViewProperty {
                    viewId = getBatteryTextId(gridIndex)
                    partiallyUpdate = true
                    Width { wrapContent = true }
                    Height { wrapContent = true }
                }
                TextContent {
                    text = batteryValueText
                }
                fontSize = textSize
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                FontColor {
                    Color {
                        argb = Color.Companion.Black.toArgb()
                    }
                }
            })
            Text {
                ViewProperty {
                    Width { wrapContent = true }
                    Height { wrapContent = true }
                    Padding {
                        bottom = 2f
                    }
                }
                TextContent {
                    text = "%"
                }
                fontSize = textSize * 0.6f
                FontColor {
                    Color {
                        argb = Color.Companion.Black.toArgb()
                    }
                }
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
            }
        }
    }

    protected fun WidgetScope.ChargingIcon() {
        fun WidgetScope.getChargingIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.2f
        }

        val iconSize = getChargingIconSize()
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        Image {
            ViewProperty {
                viewId = getChargingIconId(gridIndex)
                Width { Dp { value = iconSize * 0.6f } }
                Height { Dp { value = iconSize } }
                hide = !getChargingState()
            }
            Provider {
                drawableResId = R.layout.battery_charging_avd
            }
            animation = true
            infiniteLoop = true
        }
    }

    protected fun WidgetScope.getBatteryValue(): Float {
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

    protected fun WidgetScope.getChargingState(): Boolean {
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

}

/**
 * Battery 컴포넌트가 사용하는 View ID 타입들
 */
sealed class BatteryViewIdType(override val typeName: String) : ViewIdType() {
    /**
     * 배터리 텍스트 뷰 (퍼센트 표시)
     */
    object Text : BatteryViewIdType("battery_text")

    /**
     * 배터리 프로그레스 뷰 (원형 게이지)
     */
    object Progress : BatteryViewIdType("battery_progress")

    /**
     * 배터리 아이콘 뷰
     */
    object Icon : BatteryViewIdType("battery_icon")

    /**
     * 충전 중 아이콘 뷰
     */
    object ChargingIcon : BatteryViewIdType("charging_icon")

    companion object {
        /**
         * 모든 View ID 타입 반환
         */
        fun all(): List<BatteryViewIdType> = listOf(Text, Progress, Icon, ChargingIcon)
    }
}