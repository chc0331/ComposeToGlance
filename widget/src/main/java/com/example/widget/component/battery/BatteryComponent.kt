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
import com.example.widget.ViewKey
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.battery.datastore.BatteryData
import com.example.widget.component.battery.datastore.BatteryPreferenceKey
import com.example.widget.component.battery.datastore.DeviceType

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
                viewId = ViewKey.Battery.getBatteryProgressId(gridIndex)
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
                    viewId = ViewKey.Battery.getBatteryTextId(gridIndex)
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
                viewId = ViewKey.Battery.getChargingIconId(gridIndex)
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
            val currentValue = state[BatteryPreferenceKey.Phone.Level]
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
            val currentValue = state[BatteryPreferenceKey.Phone.Charging]
            currentValue ?: false
        } ?: false
    }

//    protected fun WidgetScope.getChargingAnimation(): Boolean {
//        val currentState = getLocal(DslLocalState) as Preferences? ?: null
//        val isPreview = getLocal(DslLocalPreview) as Boolean? ?: false
//        if (isPreview) {
//            return false
//        }
//        return currentState?.let { state ->
//            val currentValue = state[chargingAnimationKey] as Boolean?
//            currentValue ?: false
//        } ?: false
//    }

    // Bluetooth Device Helper Functions
    protected fun WidgetScope.getFirstBluetoothDevice(): BatteryData? {
        val currentState = getLocal(DslLocalState)
        val isPreview = getLocal(DslLocalPreview) ?: false
        if (isPreview) {
            // Show preview data
            return BatteryData(
                level = 75f,
                charging = false,
                deviceType = DeviceType.BLUETOOTH_EARBUDS, // 무선 이어폰 예시
                deviceName = "Galaxy Buds2 Pro",
                deviceAddress = "00:00:00:00:00:00"
            )
        }

        return currentState?.let { state ->
            val addresses = state[BatteryPreferenceKey.Bluetooth.DeviceAddresses] ?: emptySet()
            addresses.firstOrNull()?.let { address ->
                val level =
                    state[BatteryPreferenceKey.Bluetooth.levelKey(address)] ?: return@let null
                val charging = state[BatteryPreferenceKey.Bluetooth.chargingKey(address)] ?: false
                val name = state[BatteryPreferenceKey.Bluetooth.nameKey(address)] ?: "Unknown"
                val typeString = state[BatteryPreferenceKey.Bluetooth.typeKey(address)]
                    ?: DeviceType.BLUETOOTH_UNKNOWN.name
                val deviceType = try {
                    DeviceType.valueOf(typeString)
                } catch (e: IllegalArgumentException) {
                    DeviceType.BLUETOOTH_UNKNOWN
                }

                BatteryData(
                    level = level,
                    charging = charging,
                    deviceType = deviceType,
                    deviceName = name,
                    deviceAddress = address
                )
            }
        }
    }

    protected fun getDeviceIcon(deviceType: DeviceType): Int {
        return when (deviceType) {
            DeviceType.PHONE -> R.drawable.ic_mobile_device
            DeviceType.BLUETOOTH_EARBUDS -> R.drawable.ic_bluetooth_earbuds // 무선 이어폰 아이콘
            DeviceType.BLUETOOTH_HEADPHONES -> R.drawable.ic_bluetooth_headphones
            DeviceType.BLUETOOTH_HEADSET -> R.drawable.ic_bluetooth_headset
            DeviceType.BLUETOOTH_WATCH -> R.drawable.ic_bluetooth_watch
            DeviceType.BLUETOOTH_SPEAKER -> R.drawable.ic_bluetooth_speaker
            DeviceType.BLUETOOTH_HEARING_AID -> R.drawable.ic_bluetooth_headphones
            DeviceType.BLUETOOTH_UNKNOWN -> R.drawable.ic_bluetooth_device
            else -> R.drawable.ic_mobile_device
        }
    }
}