package com.example.widget.component.battery.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Row
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER
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
            Column({
                horizontalAlignment = H_ALIGN_CENTER
                verticalAlignment = V_ALIGN_CENTER
            }) {
                // Circular Progress와 BatteryIcon을 겹쳐서 배치하는 Box
                Box({
                    ViewProperty {
                    }
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }) {
                    BatteryProgress()
                    BatteryIcon()
                }
                // 프로그레스 밑에 배터리 용량 텍스트
                Row({
                    ViewProperty {
                        Width { matchParent = true }
                        Height { wrapContent = true }
                    }
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }) {
                    ChargingIcon()
                    BatteryText()
                }
            }
        }
    }
}
