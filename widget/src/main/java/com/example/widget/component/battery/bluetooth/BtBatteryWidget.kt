package com.example.widget.component.battery.bluetooth

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
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.provider.DslLocalSize
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.component.battery.BatteryComponent
import com.example.widget.component.battery.DeviceType
import com.example.widget.component.battery.getDeviceIcon

class BtBatteryWidget : BatteryComponent() {
    override fun getSizeType(): SizeType {
        return SizeType.SMALL
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
                    LeftContent()
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
                    RightContent()
                }
            }
        }
    }

    private fun WidgetScope.LeftContent() {
        Column({
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            // Circular Progress와 BatteryIcon을 겹쳐서 배치하는 Box
            Box({
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
                BatteryProgress()
                BatteryIcon()
            }
            // 프로그레스 밑에 배터리 용량 텍스트
            BatteryText()
        }
    }

    private fun WidgetScope.RightContent() {
        Column({
            horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
            verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
        }) {
            Box({
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
                CircularProgress(getBatteryValue())
                BatteryIcon()
            }
            BatteryText()
        }
    }

    private fun WidgetScope.CircularProgress(batteryLevel: Float) {
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
            progressValue = batteryLevel
            maxValue = 100f
            ProgressColor {
                Color {
                    resId =
                        R.color.battery_gauge_sufficient_color
                }
            }
            BackgroundColor {
                Color {
                    argb = Color.Companion.LightGray.toArgb()
                }
            }
        })
    }

    private fun WidgetScope.BluetoothDeviceIcon(deviceType: DeviceType) {
        fun WidgetScope.getIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.22f
        }

        Image({
            ViewProperty {
                Width { Dp { value = getIconSize() } }
                Height { Dp { value = getIconSize() } }
            }
            Provider {
                drawableResId = getDeviceIcon(deviceType)
            }
            animation = false
            infiniteLoop = false
        })
    }

    private fun WidgetScope.BluetoothBatteryText(batteryLevel: Float) {
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
            Text({
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
                fontSize = textSize * 0.65f
                FontColor {
                    Color {
                        argb = Color.Companion.Black.toArgb()
                    }
                }
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
            })
        }
    }
}