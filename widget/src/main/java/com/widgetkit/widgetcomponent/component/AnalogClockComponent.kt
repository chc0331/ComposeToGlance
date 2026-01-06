package com.widgetkit.widgetcomponent.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.widgetkit.widgetcomponent.SizeType
import com.widgetkit.widgetcomponent.WidgetCategory
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.Progress
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.ProgressType
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme

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
        val theme = getLocal(WidgetLocalTheme)
        val backgroundColor = (theme?.surface as? Int) ?: Color.Black.toArgb()
        val clockBgColor = (theme?.surfaceVariant as? Int) ?: Color.DarkGray.toArgb()
        val clockColor = (theme?.onSurface as? Int) ?: Color.White.toArgb()

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor),
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
                            argb = clockColor
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = clockBgColor
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
                            argb = clockColor
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
