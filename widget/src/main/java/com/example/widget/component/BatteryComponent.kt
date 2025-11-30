package com.example.widget.component

import android.graphics.Color.argb
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.dsl.proto.ViewProperty
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.R


abstract class BatteryComponent : WidgetComponent() {

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
}

class TinyBattery : BatteryComponent() {
    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun WidgetScope.Content() {
        val widgetSize = getLocal(DslLocalSize)
        Box({
            ViewProperty {
                Width { matchParent = true }
                Height { matchParent = true }
                BackgroundColor {
                    Color {
                        argb = Color.White.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            val size = getLocal(DslLocalSize) as? DpSize
            val progressSize =
                size?.let { kotlin.math.min(it.width.value, it.height.value) * 0.7f } ?: 60f
            val textSize = size?.let { it.height.value * 0.2f } ?: 12f
            val batteryLevel = 50f // TODO: Get actual battery level

            Column({
                horizontalAlignment = H_ALIGN_CENTER
                verticalAlignment = V_ALIGN_CENTER
            }) {
                Image {
                    ViewProperty {
                        Width { Dp { value = progressSize } }
                        Height { Dp { value = progressSize } }
                        BackgroundColor {
                            Color {
                                argb = Color.Black.toArgb()
                            }
                        }

                    }
                    Provider {
                        drawableResId = R.layout.battery_charging_avd
                    }

                    animation = true
                    infiniteLoop = true
                }

                Text({
                    TextContent {
                        text = "${batteryLevel.toInt()}%"
                    }
                    fontSize = textSize
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = Color.Black.toArgb()
                        }
                    }
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                })
            }
        }
    }
}

class SmallBattery : BatteryComponent() {
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
                        argb = Color.Black.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            val size = getLocal(DslLocalSize) as? DpSize
            val progressSize =
                size?.let { kotlin.math.min(it.width.value, it.height.value) * 0.65f } ?: 80f
            val batteryLevel = 50f // TODO: Get actual battery level

            Column({
                horizontalAlignment = H_ALIGN_CENTER
                verticalAlignment = V_ALIGN_CENTER
            }) {
                Progress({
                    ViewProperty {
                        Width { Dp { value = progressSize } }
                        Height { Dp { value = progressSize } }
                    }
                    progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                    progressValue = batteryLevel
                    maxValue = 100f
                    ProgressColor {
                        Color {
                            argb = Color.Green.toArgb()
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color.DarkGray.toArgb()
                        }
                    }
                })

                Text({
                    TextContent {
                        text = "${batteryLevel.toInt()}%"
                    }
                    fontSize = 14f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = Color.White.toArgb()
                        }
                    }
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                })
            }
        }
    }
}
