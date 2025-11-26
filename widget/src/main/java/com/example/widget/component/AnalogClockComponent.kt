package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Progress
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ProgressType
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.WidgetCategory


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
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "AnalogClock"
    }

    override fun WidgetScope.Content() {
        Box({
            viewProperty {
                width { matchParent = true }
                height { matchParent = true }
                backgroundColor {
                    color {
                        argb = Color.Black.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            val size = getLocal(DslLocalSize) as? DpSize
            val clockSize =
                size?.let { kotlin.math.min(it.width.value, it.height.value) * 0.8f } ?: 80f

            // 시계 원형 배경 (Progress를 원형으로 사용)
            Progress({
                viewProperty {
                    width { dp { value = clockSize } }
                    height { dp { value = clockSize } }
                }
                progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                progressValue = 100f
                progressColor {
                    color {
                        argb = Color.White.toArgb()
                    }
                }
                backgroundColor {
                    color {
                        argb = Color.DarkGray.toArgb()
                    }
                }
            })

            // 시계 중심점 (간단한 원)
            val centerSize = clockSize * 0.1f
            Progress({
                viewProperty {
                    width { dp { value = centerSize } }
                    height { dp { value = centerSize } }
                }
                progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                progressValue = 100f
                progressColor {
                    color {
                        argb = Color.White.toArgb()
                    }
                }
            })
        }
    }
}

