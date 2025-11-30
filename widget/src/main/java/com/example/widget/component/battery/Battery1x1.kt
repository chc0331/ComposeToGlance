package com.example.widget.component.battery

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType

class Battery1x1 : BatteryComponent() {
    override fun getSizeType(): SizeType {
        return SizeType.TINY
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
            val size = getLocal(DslLocalSize) as DpSize
            val textSize = size.height.value * 0.18f
            val batteryLevel = 50f // TODO: Get actual battery level

            Column({
                horizontalAlignment = H_ALIGN_CENTER
                verticalAlignment = V_ALIGN_CENTER
            }) {
                BatteryIcon(state = BatteryState.LEVEL_75)

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