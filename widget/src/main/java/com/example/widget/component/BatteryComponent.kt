package com.example.widget.component

import android.R.attr.value
import android.graphics.Color.argb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Progress
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ProgressType
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.WidgetCategory


class BatteryComponent : WidgetComponent() {
    override fun getName(): String {
        return "Battery"
    }

    override fun getDescription(): String {
        return "Battery"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.DEVICE_INFO
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "Battery"
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
            val progressSize = size?.let { it.height.value * 0.6f } ?: 60f
            Progress({
                ViewProperty {
                    Width { Dp { value = progressSize } }
                    Height { Dp { value = progressSize } }
                }
                progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
                progressValue = 50f
                ProgressColor {
                    Color {
                        argb = Color.Green.toArgb()
                    }
                }
            })
            Text({
                text = "50%"
                FontColor {
                    Color {
                        argb = Color.White.toArgb()
                    }
                }
            })
        }
    }
}