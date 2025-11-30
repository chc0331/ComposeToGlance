package com.example.widget.component.battery

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.provider.DslLocalSize
import com.example.widget.R
import com.example.widget.SizeType
import kotlin.math.min

class Battery2x1 : BatteryComponent() {
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
                        argb = Color.White.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            val batteryLevel = 50f // TODO: Get actual battery level

            Row({
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                ViewProperty {
                    Width { matchParent = true }
                    Height { matchParent = true }
                }
            }) {
                BatteryIcon(BatteryState.CHARGING)
                Row({
                    ViewProperty {
                        Width {
                            matchParent = true
                        }
                        Height {
                            matchParent = true
                        }
                    }
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_END
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }) {
                    Column({
                        ViewProperty {
                            Padding {
                                end = 16f
                            }
                        }
                        horizontalAlignment = HorizontalAlignment.H_ALIGN_END

                    }) {
                        val size = getLocal(DslLocalSize) as DpSize
                        val mainTextSize = size.height.value * 0.25f
                        Text {
                            TextContent {
                                text = "75%"
                                FontColor { Color { argb = Color.Black.toArgb() } }
                                fontSize = mainTextSize
                                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                            }
                            textAlign = TextAlign.TEXT_ALIGN_END
                        }
                        val subTextSize = size.height.value * 0.14f
                        Text {
                            TextContent {
                                text = "≈ 3h 20m"
                                FontColor { Color { argb = Color.Gray.toArgb() } }
                                fontSize = subTextSize
                                fontWeight = FontWeight.FONT_WEIGHT_NORMAL
                            }
                            textAlign = TextAlign.TEXT_ALIGN_END
                        }
                        Text {
                            TextContent {
                                text = "충전됨"
                                FontColor { Color { argb = Color.Gray.toArgb() } }
                                fontSize = subTextSize
                                fontWeight = FontWeight.FONT_WEIGHT_NORMAL
                            }
                            textAlign = TextAlign.TEXT_ALIGN_END
                        }
                    }
                }
            }
        }
    }
}