package com.example.widget.component.devicecare

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Column
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Spacer
import com.example.dsl.component.Text
import com.example.dsl.modifier.*
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.localprovider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.update.ComponentUpdateManager

class DeviceCareWidget : WidgetComponent() {

    override fun getName() = "DeviceCare"

    override fun getDescription() = "DeviceCare"

    override fun getWidgetCategory() = WidgetCategory.DEVICE_INFO

    override fun getSizeType() = SizeType.SMALL

    override fun getWidgetTag() = "DeviceCare"

    override fun WidgetScope.Content() {
        Column(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(Color.White.toArgb())
                .padding(top = 6f, start = 8f, end = 8f, bottom = 6f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_TOP
            }
        ) {
            TitleBar()
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    verticalAlignment = VerticalAlignment.V_ALIGN_BOTTOM
                }
            ) {
                ScoreProgress("Memory", progressViewId = 0)
                Spacer()
                ScoreProgress("Storage", progressViewId = 0)
                Spacer()
                ScoreProgress("CPU", progressViewId = 0)
                Spacer()
                ScoreProgress("Temperature", progressViewId = 0)
            }
        }
    }

    private fun WidgetScope.TitleBar() {
        val localSize = getLocal(DslLocalSize) as DpSize
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .height(localSize.height.value * 0.28f)
                .backgroundColor(Color.White.toArgb())
        ) {
            Row(
                modifier = WidgetModifier
                    .wrapContentWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_BOTTOM
                }
            ) {
                Text(
                    contentProperty = {
                        TextContent {
                            text = "Device Score"
                        }
                        fontSize = localSize.height.value * 0.16f
                        fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
                    }
                )
            }

            Row(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_END
                }
            ) {
                Text(
                    contentProperty = {
                        TextContent {
                            text = "89"
                        }
                        fontSize = localSize.height.value * 0.24f
                        fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    }
                )
            }
        }
    }

    private fun WidgetScope.ScoreProgress(
        category: String,
        progressViewId: Int,
        progressColor: Int = Color.Blue.toArgb()
    ) {
        val localSize = getLocal(DslLocalSize) as DpSize

        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row {
                Text(
                    contentProperty = {
                        TextContent {
                            text = category
                        }
                        fontSize = 8f
                    }
                )
                Progress(
                    modifier = WidgetModifier
                        .width(localSize.width.value * 0.75f)
                        .height(localSize.height.value * 0.1f),
                    contentProperty = {
                        progressType = ProgressType.PROGRESS_TYPE_LINEAR
                        progressValue = 65f
                        maxValue = 100f
                        ProgressColor {
                            Color {
                                argb = progressColor
                            }
                        }
                        BackgroundColor {
                            Color {
                                argb = Color.LightGray.toArgb()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun WidgetScope.Spacer(height: Float = 2f) {
        Spacer(
            modifier = WidgetModifier
                .fillMaxWidth()
                .height(height)
        )
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = DeviceCareUpdateManager
}