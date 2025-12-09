package com.example.widget.component

import android.R.attr.value
import android.graphics.Color.argb
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
import com.example.widget.component.update.ComponentUpdateManager

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
            val clockSize =
                size?.let { kotlin.math.min(it.width.value, it.height.value) * 0.8f } ?: 80f

            // 시계 원형 배경 (Progress를 원형으로 사용)
            Progress({
                ViewProperty {
                    Width { Dp { value = clockSize } }
                    Height { Dp { value = clockSize } }
                }
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
            })

            // 시계 중심점 (간단한 원)
            val centerSize = clockSize * 0.1f
            Progress({
                ViewProperty {
                    Width { Dp { value = centerSize } }
                    Height { Dp { value = centerSize } }
                }
                progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                progressValue = 100f
                ProgressColor {
                    Color {
                        argb = Color.White.toArgb()
                    }
                }
            })
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*>? {
        return null
    }
}
