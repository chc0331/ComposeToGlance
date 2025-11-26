package com.example.widget.component

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
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "Storage"
    }

    override fun WidgetScope.Content() {
        Box({
            viewProperty {
                width { matchParent = true }
                height { matchParent = true }
                backgroundColor {
                    color {
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
                    text = "Storage"
                    fontSize = 16f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    fontColor {
                        color {
                            argb = Color.Black.toArgb()
                        }
                    }
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                })

                Progress({
                    viewProperty {
                        width { dp { value = progressWidth } }
                        height { dp { value = 20f } }
                    }
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = 65f
                    maxValue = 100f
                    progressColor {
                        color {
                            argb = Color.Blue.toArgb()
                        }
                    }
                    backgroundColor {
                        color {
                            argb = Color.LightGray.toArgb()
                        }
                    }
                })

                Text({
                    text = "65%"
                    fontSize = 14f
                    fontWeight = FontWeight.FONT_WEIGHT_NORMAL
                    fontColor {
                        color {
                            argb = Color.Black.toArgb()
                        }
                    }
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                })
            }
        }
    }
}

