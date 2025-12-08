package com.example.widget.component.battery.bluetooth

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.emptyPreferences
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
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
import com.example.widget.component.battery.BatteryPreferenceKey
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.viewid.ViewIdType

class BluetoothBatteryWidget : WidgetComponent() {

    override fun getName(): String = "BluetoothBattery"

    override fun getDescription(): String = "BluetoothBattery"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType {
        return SizeType.SMALL
    }

    override fun getWidgetTag(): String = "BluetoothBattery"

    override fun WidgetScope.Content() {
        Box({
            ViewProperty {
                Width { matchParent = true }
                Height { matchParent = true }
                BackgroundColor {
                    Color {
                        argb = Color.Companion.White.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            Row({
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                ViewProperty {
                    Width { matchParent = true }
                    Height { matchParent = true }
                }
            }) {
                DeviceContent(DeviceType.BLUETOOTH_EARBUDS)
                DeviceContent(DeviceType.BLUETOOTH_WATCH)
            }
        }
    }

    private fun WidgetScope.DeviceContent(type: DeviceType) {
        val size = getLocal(DslLocalSize) as DpSize
        Box({
            ViewProperty {
                Width {
                    Dp {
                        value = size.width.value / 2
                    }
                }
                Height { matchParent = true }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            if (type == DeviceType.BLUETOOTH_EARBUDS) {
                EarBudsContent()
            } else if (type == DeviceType.BLUETOOTH_WATCH) {
                WatchContent()
            }
        }
    }

    private fun WidgetScope.EarBudsContent() {
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val currentState = getLocal(DslLocalState) ?: emptyPreferences()
        val isPreview = getLocal(DslLocalPreview) ?: false
        val batteryLevel = if (isPreview) {
            50f
        } else {
            currentState[BluetoothBatteryPreferenceKey.BtEarbudsLevel] ?: 0f
        }
        var isConnected = isPreview

        if (!isPreview) {
            isConnected = currentState[BluetoothBatteryPreferenceKey.BtEarbudsConnected] ?: false
        }

        Column({
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // Circular Progress와 Device Icon을 겹쳐서 배치하는 Box
            Box({
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
                BatteryProgress(
                    progressLevel = batteryLevel,
                    progressViewId = getEarBudsProgressId(gridIndex),
                    isConnected
                )
                BatteryIcon(
                    iconResId = R.drawable.ic_bluetooth_earbuds,
                    iconViewId = getEarBudsIconId(gridIndex),
                    isConnected
                )
            }
            BatteryText(
                batteryLevel,
                getEarBudsTextId(gridIndex),
                isConnected
            )
        }
    }

    private fun WidgetScope.WatchContent() {
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val currentState = getLocal(DslLocalState) ?: emptyPreferences()
        val isPreview = getLocal(DslLocalPreview) ?: false
        val batteryLevel = if (isPreview) {
            50f
        } else {
            currentState[BluetoothBatteryPreferenceKey.BtWatchLevel] ?: 0f
        }
        var isConnected = isPreview

        if (!isPreview) {
            isConnected = currentState[BluetoothBatteryPreferenceKey.BtWatchConnected] ?: false
        }

        Column({
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // Circular Progress와 Device Icon을 겹쳐서 배치하는 Box
            Box({
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
                BatteryProgress(
                    progressLevel = batteryLevel,
                    progressViewId = getWatchProgressId(gridIndex),
                    isConnected
                )
                BatteryIcon(
                    iconResId = R.drawable.ic_bluetooth_watch,
                    iconViewId = getWatchIconId(gridIndex),
                    isConnected
                )
            }
            BatteryText(
                batteryLevel,
                getWatchTextId(gridIndex),
                isConnected
            )
        }
    }

    private fun WidgetScope.BatteryProgress(
        progressLevel: Float,
        progressViewId: Int,
        isConnect: Boolean = false
    ) {
        fun WidgetScope.getProgressSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.58f
        }
        Progress {
            ViewProperty {
                viewId = progressViewId
                partiallyUpdate = true
                Width {
                    Dp { value = getProgressSize() }
                }
                Height {
                    Dp { value = getProgressSize() }
                }
            }
            progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
            progressValue = if (isConnect) progressLevel else 0f
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
        }
    }

    private fun WidgetScope.BatteryIcon(
        iconResId: Int,
        iconViewId: Int,
        isConnect: Boolean = false
    ) {
        fun WidgetScope.getBatteryIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.22f
        }
        Image {
            ViewProperty {
                viewId = iconViewId
                Width { Dp { value = getBatteryIconSize() } }
                Height { Dp { value = getBatteryIconSize() } }
                partiallyUpdate = true
            }
            Provider {
                drawableResId = iconResId
            }
            TintColor {
                argb = if (isConnect) Color.Transparent.toArgb() else Color.LightGray.toArgb()
            }
        }
    }

    private fun WidgetScope.BatteryText(
        progressLevel: Float,
        textViewId: Int,
        isConnect: Boolean = false
    ) {
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
            Text {
                ViewProperty {
                    viewId = textViewId
                    partiallyUpdate = true
                    Width { wrapContent = true }
                    Height { wrapContent = true }
                }
                TextContent {
                    text = if (isConnect) {
                        progressLevel.toInt().toString()
                    } else {
                        ""
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

    override fun getViewIdTypes(): List<ViewIdType> {
        return BluetoothBatteryViewIdType.all()
    }

    internal fun getEarBudsTextId(gridIndex: Int) =
        generateViewId(BluetoothBatteryViewIdType.EarBudsBatteryText, gridIndex)

    internal fun getEarBudsProgressId(gridIndex: Int) =
        generateViewId(BluetoothBatteryViewIdType.EarBudsBatteryProgress, gridIndex)

    internal fun getEarBudsIconId(gridIndex: Int) =
        generateViewId(BluetoothBatteryViewIdType.EarBudsBatteryIcon, gridIndex)

    internal fun getWatchTextId(gridIndex: Int) =
        generateViewId(BluetoothBatteryViewIdType.WatchBatteryText, gridIndex)

    internal fun getWatchProgressId(gridIndex: Int) =
        generateViewId(BluetoothBatteryViewIdType.WatchBatteryProgress, gridIndex)

    internal fun getWatchIconId(gridIndex: Int) =
        generateViewId(BluetoothBatteryViewIdType.WatchBatteryIcon, gridIndex)
}

sealed class BluetoothBatteryViewIdType(override val typeName: String) : ViewIdType() {
    object EarBudsBatteryText : BluetoothBatteryViewIdType("ear_buds_battery_text")
    object EarBudsBatteryProgress : BluetoothBatteryViewIdType("ear_buds_battery_progress")
    object EarBudsBatteryIcon : BluetoothBatteryViewIdType("ear_buds_battery_icon")
    object WatchBatteryText : BluetoothBatteryViewIdType("watch_battery_text")
    object WatchBatteryProgress : BluetoothBatteryViewIdType("watch_battery_progress")
    object WatchBatteryIcon : BluetoothBatteryViewIdType("watch_battery_icon")

    companion object {
        fun all(): List<BluetoothBatteryViewIdType> = listOf(
            EarBudsBatteryText,
            EarBudsBatteryProgress,
            EarBudsBatteryIcon,
            WatchBatteryText,
            WatchBatteryProgress,
            WatchBatteryIcon
        )
    }
}
