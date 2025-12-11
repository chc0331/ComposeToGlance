package com.example.widget.component.devicecare

import android.R.attr.label
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.Preferences
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Spacer
import com.example.dsl.component.Text
import com.example.dsl.localprovider.WidgetLocalContext
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.backgroundColor
import com.example.dsl.modifier.cornerRadius
import com.example.dsl.modifier.expandWidth
import com.example.dsl.modifier.fillMaxHeight
import com.example.dsl.modifier.fillMaxWidth
import com.example.dsl.modifier.height
import com.example.dsl.modifier.padding
import com.example.dsl.modifier.width
import com.example.dsl.modifier.wrapContentHeight
import com.example.dsl.modifier.wrapContentWidth
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.battery.BatteryViewIdType
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType
import com.example.widget.util.getSystemBackgroundRadius


class DeviceCareWidget : WidgetComponent() {

    override fun getName() = "DeviceCare"

    override fun getDescription() = "DeviceCare"

    override fun getWidgetCategory() = WidgetCategory.DEVICE_INFO

    override fun getSizeType() = SizeType.SMALL

    override fun getWidgetTag() = "DeviceCare"

    override fun WidgetScope.Content() {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val verticalPadding = localSize.height.value * 0.03f

        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(Color(0xFFF5F5F5).toArgb())
                .cornerRadius(16f)
                .padding(top = verticalPadding, start = 12f, end = 12f, bottom = verticalPadding),
            contentProperty = {
                verticalAlignment = VerticalAlignment.V_ALIGN_TOP
            }
        ) {
            DeviceStateContent(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight()
            )
        }
    }

    private fun WidgetScope.DeviceStateContent(modifier: WidgetModifier) {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        Column(
            modifier = modifier,
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            val itemHeight = localSize.height.value * 0.42f
            // Memory Progress Bar with Icon
            ProgressBarWithIcon(
                modifier = WidgetModifier.fillMaxWidth().height(itemHeight),
                iconRes = R.drawable.ic_memory,
                progressColor = Color(0xFF2196F3).toArgb()
            )

            // Storage Progress Bar with Icon
            ProgressBarWithIcon(
                modifier = WidgetModifier.fillMaxWidth().height(itemHeight),
                iconRes = R.drawable.ic_storage,
                progressColor = Color(0xFF2196F3).toArgb(),
                memory = false
            )
        }
    }

    private fun WidgetScope.ProgressBarWithIcon(
        modifier: WidgetModifier = WidgetModifier,
        iconRes: Int,
        progressColor: Int,
        memory: Boolean = true
    ) {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        Row(
            modifier = modifier.padding(vertical = 2f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            val iconSize = localSize.height * 0.36f
            // Icon
            Image(
                modifier = WidgetModifier
                    .width(iconSize.value)
                    .height(iconSize.value).backgroundColor(Color.LightGray.toArgb())
                    .padding(all = 4f)
                    .cornerRadius(12f),
                contentProperty = {
                    Provider {
                        drawableResId = iconRes
                    }
                }
            )

            Spacer(modifier = WidgetModifier.width(4f).wrapContentHeight())

            val progressHeight = localSize.height.value * 0.28f
            if (memory) {
                RamUsageProcess(
                    modifier = WidgetModifier.expandWidth().height(progressHeight),
                    progressColor = progressColor
                )
            } else {
                StorageUsageProcess(
                    modifier = WidgetModifier.expandWidth().height(progressHeight),
                    progressColor = progressColor
                )
            }
        }
    }

    private fun WidgetScope.RamUsageProcess(
        modifier: WidgetModifier = WidgetModifier,
        progressColor: Int
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val state = getLocal(WidgetLocalState) as Preferences
        val currentMemoryUsage = state[DeviceCarePreferenceKey.MemoryUsage] ?: 0f
        val totalMemory = state[DeviceCarePreferenceKey.TotalMemory] ?: 100f
        val progress = (currentMemoryUsage * 100f) / totalMemory
        Box(
            modifier = modifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            // Progress bar
            Progress(
                modifier = WidgetModifier
                    .fillMaxWidth().fillMaxHeight()
                    .cornerRadius(context.getSystemBackgroundRadius().value),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = progress
                    maxValue = 100f
                    ProgressColor {
                        Color {
                            argb = progressColor
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color(0xFFE0E0E0).toArgb()
                        }
                    }
                }
            )

            Spacer(modifier = WidgetModifier.height(4f).wrapContentWidth())

            // Label
            Text(
                contentProperty = {
                    TextContent {
                        text = "${currentMemoryUsage}GB / ${totalMemory}GB"
                    }
                    fontSize = 11f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = Color(0xFF000000).toArgb()
                        }
                    }
                }
            )
        }
    }

    private fun WidgetScope.StorageUsageProcess(
        modifier: WidgetModifier = WidgetModifier,
        progressColor: Int
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val state = getLocal(WidgetLocalState) as Preferences
        val currentStorageUsage = state[DeviceCarePreferenceKey.StorageUsage] ?: 0f
        val totalStorage = state[DeviceCarePreferenceKey.TotalStorage] ?: 100f
        val progress = (currentStorageUsage * 100f) / totalStorage
        Box(
            modifier = modifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            // Progress bar
            Progress(
                modifier = WidgetModifier
                    .fillMaxWidth().fillMaxHeight()
                    .cornerRadius(context.getSystemBackgroundRadius().value),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = progress
                    maxValue = 100f
                    ProgressColor {
                        Color {
                            argb = progressColor
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color(0xFFE0E0E0).toArgb()
                        }
                    }
                }
            )

            Spacer(modifier = WidgetModifier.height(4f).wrapContentWidth())

            // Label
            Text(
                contentProperty = {
                    TextContent {
                        text = "${currentStorageUsage}GB / ${totalStorage}GB"
                    }
                    fontSize = 11f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = Color(0xFF000000).toArgb()
                        }
                    }
                }
            )
        }
    }

    override fun getViewIdTypes(): List<ViewIdType> {
        return DeviceCareViewIdType.all()
    }

    fun getRamUsageText(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.RamUsageText, gridIndex)
    }

    fun getRamUsageProgress(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.RamUsageProgress, gridIndex)
    }

    fun getStorageUsageText(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.StorageUsageText, gridIndex)
    }

    fun getStorageUsageProgress(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.StorageUsageProgress, gridIndex)
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = DeviceCareUpdateManager

    override fun getDataStore() = DeviceCareComponentDataStore

    override fun getLifecycle() = DeviceCareLifecycle

    override fun requiresAutoLifecycle() = true
}