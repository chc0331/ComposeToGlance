package com.widgetkit.core.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.component.Box
import com.widgetkit.dsl.component.Progress
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.ProgressType
import com.widgetkit.dsl.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.update.ComponentUpdateManager

class AnalogClockComponent : WidgetComponent() {

    override fun getName(): String {
        return "AnalogClock"
    }

    override fun getDescription(): String {
        return "AnalogClock"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.CLOCK
    }

    override fun getSizeType(): SizeType {
        return SizeType.MEDIUM
    }

    override fun getWidgetTag(): String {
        return "AnalogClock"
    }

    override fun WidgetScope.Content() {
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(Color.Black.toArgb()),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            val size = getLocal(WidgetLocalSize) as? DpSize
            val clockSize =
                size?.let { kotlin.math.min(it.width.value, it.height.value) * 0.8f } ?: 80f

            // 시계 원형 배경 (Progress를 원형으로 사용)
            Progress(
                modifier = WidgetModifier
                    .width(clockSize)
                    .height(clockSize),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                    progressValue = 100f
                    ProgressColor {
                        Color {
                            argb = Color.White.toArgb()
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color.DarkGray.toArgb()
                        }
                    }
                }
            )

            // 시계 중심점 (간단한 원)
            val centerSize = clockSize * 0.1f
            Progress(
                modifier = WidgetModifier
                    .width(centerSize)
                    .height(centerSize),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                    progressValue = 100f
                    ProgressColor {
                        Color {
                            argb = Color.White.toArgb()
                        }
                    }
                }
            )
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*>? {
        return null
    }
}
