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
import com.example.dsl.modifier.*
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.localprovider.WidgetLocalGridIndex
import com.example.dsl.localprovider.WidgetLocalPreview
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType

class BluetoothBatteryWidget : WidgetComponent() {

    override fun getName(): String = "BluetoothBattery"

    override fun getDescription(): String = "BluetoothBattery"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType = SizeType.SMALL

    override fun getWidgetTag(): String = "BluetoothBattery"

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
            Row(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                DeviceContent(DeviceType.BLUETOOTH_EARBUDS)
                DeviceContent(DeviceType.BLUETOOTH_WATCH)
            }
        }
    }

    private fun WidgetScope.DeviceContent(type: DeviceType) {
        val size = getLocal(WidgetLocalSize) as DpSize
        Box(
            modifier = WidgetModifier
                .width(size.width.value / 2)
                .fillMaxHeight(),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            if (type == DeviceType.BLUETOOTH_EARBUDS) {
                EarBudsContent()
            } else if (type == DeviceType.BLUETOOTH_WATCH) {
                WatchContent()
            }
        }
    }

    private fun WidgetScope.EarBudsContent() {
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        val batteryLevel = if (isPreview) {
            50f
        } else {
            currentState[BluetoothBatteryPreferenceKey.BtEarbudsLevel] ?: 0f
        }
        var isConnected = isPreview

        if (!isPreview) {
            isConnected = currentState[BluetoothBatteryPreferenceKey.BtEarbudsConnected] ?: false
        }

        Column(contentProperty = {
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // Circular Progress와 Device Icon을 겹쳐서 배치하는 Box
            Box(contentProperty = {
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
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        val batteryLevel = if (isPreview) {
            50f
        } else {
            currentState[BluetoothBatteryPreferenceKey.BtWatchLevel] ?: 0f
        }
        var isConnected = isPreview

        if (!isPreview) {
            isConnected = currentState[BluetoothBatteryPreferenceKey.BtWatchConnected] ?: false
        }

        Column(contentProperty = {
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // Circular Progress와 Device Icon을 겹쳐서 배치하는 Box
            Box(contentProperty = {
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
            val size = getLocal(WidgetLocalSize) as DpSize
            return size.height.value * 0.58f
        }
        Progress(
            modifier = WidgetModifier
                .viewId(progressViewId)
                .partiallyUpdate(true)
                .width(getProgressSize())
                .height(getProgressSize()),
            contentProperty = {
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
                        argb = Color.LightGray.toArgb()
                    }
                }
            }
        )
    }

    private fun WidgetScope.BatteryIcon(
        iconResId: Int,
        iconViewId: Int,
        isConnect: Boolean = false
    ) {
        fun WidgetScope.getBatteryIconSize(): Float {
            val size = getLocal(WidgetLocalSize) as DpSize
            return size.height.value * 0.22f
        }
        Image(
            modifier = WidgetModifier
                .viewId(iconViewId)
                .partiallyUpdate(true)
                .width(getBatteryIconSize())
                .height(getBatteryIconSize()),
            contentProperty = {
                Provider {
                    drawableResId = iconResId
                }
                TintColor {
                    argb = if (isConnect) Color.Transparent.toArgb() else Color.LightGray.toArgb()
                }
            }
        )
    }

    private fun WidgetScope.BatteryText(
        progressLevel: Float,
        textViewId: Int,
        isConnect: Boolean = false
    ) {
        val size = getLocal(WidgetLocalSize) as DpSize
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
                    .viewId(textViewId)
                    .partiallyUpdate(true)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                contentProperty = {
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

    override fun getUpdateManager(): ComponentUpdateManager<*> = BluetoothBatteryUpdateManager
}
