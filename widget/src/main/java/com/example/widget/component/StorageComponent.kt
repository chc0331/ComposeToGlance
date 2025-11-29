package com.example.widget.component

import android.R.attr.text
import android.R.attr.value
import android.graphics.Color.argb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Progress
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextContent
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.WidgetCategory


class StorageComponent : WidgetComponent() {

    override fun getName(): String {
        return "Storage"
    }

    override fun getDescription(): String {
        return "Storage"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.DEVICE_INFO
    }

    override fun getSizeType(): SizeType {
        return SizeType.MEDIUM
    }

    override fun getWidgetTag(): String {
        return "Storage"
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
            val size = getLocal(DslLocalSize) as? DpSize
            val progressWidth = size?.let { it.width.value * 0.8f } ?: 100f

            Column({
                horizontalAlignment = H_ALIGN_CENTER
                verticalAlignment = V_ALIGN_CENTER
            }) {
                Text({
                    Text{
                        text = "Storage"
                    }
                    fontSize = 16f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = Color.Black.toArgb()
                        }
                    }
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                })

                Progress({
                    ViewProperty {
                        Width { Dp { value = progressWidth } }
                        Height { Dp { value = 20f } }
                    }
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = 65f
                    maxValue = 100f
                    ProgressColor {
                        Color {
                            argb = Color.Blue.toArgb()
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color.LightGray.toArgb()
                        }
                    }
                })

                Text({
                    Text {
                        text = "65%"
                    }
                    fontSize = 14f
                    fontWeight = FontWeight.FONT_WEIGHT_NORMAL
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

