package com.example.widget.component.battery.bluetooth

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.stringPreferencesKey
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
import com.example.widget.component.ViewIdType
import com.example.widget.component.BluetoothBatteryViewIdType
import com.example.widget.component.battery.BatteryComponent
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.battery.getDeviceIcon

class BluetoothBatteryWidget : BatteryComponent() {

    override fun getSizeType(): SizeType {
        return SizeType.SMALL
    }

    override fun getWidgetTag(): String {
        return "BluetoothBattery"
    }
    
    // ViewIdProvider 구현 - BatteryComponent의 기본 구현을 override
    override fun getViewIdTypes(): List<ViewIdType> {
        return BluetoothBatteryViewIdType.all()
    }
    
    // View ID Helper 메서드들
    fun getEarBudsTextId(gridIndex: Int): Int {
        return generateViewId(BluetoothBatteryViewIdType.EarBudsText, gridIndex)
    }
    
    fun getEarBudsProgressId(gridIndex: Int): Int {
        return generateViewId(BluetoothBatteryViewIdType.EarBudsProgress, gridIndex)
    }
    
    fun getWatchTextId(gridIndex: Int): Int {
        return generateViewId(BluetoothBatteryViewIdType.WatchText, gridIndex)
    }
    
    fun getWatchProgressId(gridIndex: Int): Int {
        return generateViewId(BluetoothBatteryViewIdType.WatchProgress, gridIndex)
    }

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
                    EarBudsContent()
                }
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
                    WatchContent()
                }
            }
        }
    }

    private fun WidgetScope.EarBudsContent() {
        val state = getLocal(DslLocalState)
        val btEarBudsConnected =
            state?.get(BluetoothBatteryPreferenceKey.BtEarbudsConnected) as Boolean? ?: false
        val btEarBudsBatteryValue =
            state?.get(BluetoothBatteryPreferenceKey.BtEarbudsLevel) as Float? ?: 0f
        Log.i("heec.choi", "EarbudsContent / $btEarBudsConnected $btEarBudsBatteryValue")
        if (btEarBudsConnected) {
            ConnectedDeviceContent(btEarBudsBatteryValue, DeviceType.BLUETOOTH_EARBUDS)
        } else SkeletonContent()
    }

    private fun WidgetScope.WatchContent() {
        val state = getLocal(DslLocalState)
        val btWatchConnected =
            state?.get(BluetoothBatteryPreferenceKey.BtWatchConnected) as Boolean? ?: false
        val btWatchBatteryValue =
            state?.get(BluetoothBatteryPreferenceKey.BtWatchLevel) as Float? ?: 0f
        Log.i("heec.choi", "WatchContent / $btWatchConnected $btWatchBatteryValue")
        if (btWatchConnected) {
            ConnectedDeviceContent(btWatchBatteryValue, DeviceType.BLUETOOTH_WATCH)
        } else SkeletonContent()
    }

    /**
     * 연결된 BT 디바이스의 배터리 정보 표시
     */
    private fun WidgetScope.ConnectedDeviceContent(level: Float, deviceType: DeviceType) {
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val textViewId = when (deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> getEarBudsTextId(gridIndex)
            DeviceType.BLUETOOTH_WATCH -> getWatchTextId(gridIndex)
            else -> 0
        }
        val progressViewId = when (deviceType) {
            DeviceType.BLUETOOTH_EARBUDS -> getEarBudsProgressId(gridIndex)
            DeviceType.BLUETOOTH_WATCH -> getWatchProgressId(gridIndex)
            else -> 0
        }

        Column({
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // Circular Progress와 Device Icon을 겹쳐서 배치하는 Box
            Box({
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
                CircularProgress(level, progressViewId)
                BatteryIcon(deviceType)
            }
            // 프로그레스 밑에 배터리 용량 텍스트
            DeviceBatteryText(level, textViewId)
        }
    }

    /**
     * 연결 안된 상태의 스켈레톤 UI
     */
    private fun WidgetScope.SkeletonContent() {
        Column({
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // 스켈레톤 프로그레스와 아이콘
            Box({
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
                SkeletonProgress()
                SkeletonIcon()
            }
            // 스켈레톤 텍스트
            SkeletonText()
        }
    }

    /**
     * 스켈레톤 프로그레스 (회색 원형 프로그레스)
     */
    private fun WidgetScope.SkeletonProgress() {
        fun WidgetScope.getProgressSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.58f
        }

        Progress({
            ViewProperty {
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
            progressValue = 0f
            maxValue = 100f
            ProgressColor {
                Color {
                    argb = Color.Companion.LightGray.toArgb()
                }
            }
            BackgroundColor {
                Color {
                    argb = Color.Companion.LightGray.toArgb()
                }
            }
        })
    }

    /**
     * 스켈레톤 아이콘 (비활성화된 블루투스 아이콘)
     */
    private fun WidgetScope.SkeletonIcon() {
        fun WidgetScope.getIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.22f
        }

        Image {
            ViewProperty {
                Width { Dp { value = getIconSize() } }
                Height { Dp { value = getIconSize() } }
            }
            Provider {
                drawableResId = R.drawable.ic_bluetooth_device
            }
            // 반투명 처리를 위한 tint 설정
            TintColor {
                argb = Color.LightGray.toArgb()
            }
        }
    }

    /**
     * 스켈레톤 텍스트 (회색 바 형태)
     */
    private fun WidgetScope.SkeletonText() {
        val size = getLocal(DslLocalSize) as DpSize
        val textSize = size.height.value * 0.18f

        Box({
            ViewProperty {
                Width { Dp { value = textSize * 2f } }
                Height { Dp { value = textSize } }
                BackgroundColor {
                    Color {
                        argb = Color.Companion.LightGray.toArgb()
                    }
                }
                CornerRadius {
                    radius = 4f
                }
            }
        }) {}
    }

    /**
     * 디바이스별 배터리 정보를 표시하는 텍스트
     */
    private fun WidgetScope.DeviceBatteryText(batteryLevel: Float, viewId: Int) {
        val gridIndex = getLocal(DslLocalGridIndex) as Int
        val batteryValueText = "${batteryLevel.toInt()}"
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
                    this.viewId = viewId
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

    /**
     * 원형 프로그레스 표시
     */
    private fun WidgetScope.CircularProgress(batteryLevel: Float, viewId: Int) {
        fun WidgetScope.getProgressSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.58f
        }

        Progress({
            ViewProperty {
                this.viewId = viewId
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
            progressValue = batteryLevel
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
}