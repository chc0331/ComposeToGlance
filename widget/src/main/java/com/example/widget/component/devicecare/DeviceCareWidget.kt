package com.example.widget.component.devicecare

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.emptyPreferences
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
import com.example.dsl.localprovider.WidgetLocalGridIndex
import com.example.dsl.localprovider.WidgetLocalPreview
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType

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
                ScoreProgress("Memory", DeviceCareViewIdType.MemoryProgress)
                Spacer()
                ScoreProgress("Storage", DeviceCareViewIdType.StorageProgress)
                Spacer()
                ScoreProgress("CPU", DeviceCareViewIdType.CpuProgress)
                Spacer()
                ScoreProgress("Temperature", DeviceCareViewIdType.TemperatureProgress)
            }
        }
    }

    private fun WidgetScope.TitleBar() {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        
        val deviceState = if (isPreview) {
            DeviceState(
                memoryUsageRatio = 0.6f,
                storageUsageRatio = 0.5f,
                cpuLoad = 0.3f,
                temperatureCelsius = 38f
            )
        } else {
            DeviceState(
                memoryUsageRatio = currentState[DeviceCarePreferenceKey.MemoryUsageRatio] ?: 0f,
                storageUsageRatio = currentState[DeviceCarePreferenceKey.StorageUsageRatio] ?: 0f,
                cpuLoad = currentState[DeviceCarePreferenceKey.CpuLoad] ?: 0f,
                temperatureCelsius = currentState[DeviceCarePreferenceKey.TemperatureCelsius] ?: 0f
            )
        }
        
        val score = DeviceHealthCalculator.calculateScore(deviceState)
        
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
                    modifier = WidgetModifier
                        .viewId(getScoreTextId(gridIndex))
                        .partiallyUpdate(true)
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    contentProperty = {
                        TextContent {
                            text = score.toString()
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
        viewIdType: DeviceCareViewIdType,
        progressColor: Int = Color.Blue.toArgb()
    ) {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val currentState = getLocal(WidgetLocalState) ?: emptyPreferences()
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        
        val calculatedScore = when (viewIdType) {
            DeviceCareViewIdType.MemoryProgress -> {
                if (isPreview) {
                    DeviceHealthCalculator.memoryScore(0.6f).toFloat()
                } else {
                    val ratio = currentState[DeviceCarePreferenceKey.MemoryUsageRatio] ?: 0f
                    DeviceHealthCalculator.memoryScore(ratio).toFloat()
                }
            }
            DeviceCareViewIdType.StorageProgress -> {
                if (isPreview) {
                    DeviceHealthCalculator.storageScore(0.5f).toFloat()
                } else {
                    val ratio = currentState[DeviceCarePreferenceKey.StorageUsageRatio] ?: 0f
                    DeviceHealthCalculator.storageScore(ratio).toFloat()
                }
            }
            DeviceCareViewIdType.CpuProgress -> {
                if (isPreview) {
                    DeviceHealthCalculator.cpuScore(0.3f).toFloat()
                } else {
                    val load = currentState[DeviceCarePreferenceKey.CpuLoad] ?: 0f
                    DeviceHealthCalculator.cpuScore(load).toFloat()
                }
            }
            DeviceCareViewIdType.TemperatureProgress -> {
                if (isPreview) {
                    DeviceHealthCalculator.temperatureScore(38f).toFloat()
                } else {
                    val temp = currentState[DeviceCarePreferenceKey.TemperatureCelsius] ?: 0f
                    DeviceHealthCalculator.temperatureScore(temp).toFloat()
                }
            }
            else -> 0f
        }

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
                        .viewId(getProgressId(gridIndex, viewIdType))
                        .partiallyUpdate(true)
                        .width(localSize.width.value * 0.75f)
                        .height(localSize.height.value * 0.1f),
                    contentProperty = {
                        progressType = ProgressType.PROGRESS_TYPE_LINEAR
                        progressValue = calculatedScore
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

    override fun getViewIdTypes(): List<ViewIdType> {
        return DeviceCareViewIdType.all()
    }

    // View ID Helper 메서드들
    /**
     * 점수 텍스트 View ID 조회
     */
    fun getScoreTextId(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.ScoreText, gridIndex)
    }

    /**
     * 프로그레스 View ID 조회
     */
    fun getProgressId(gridIndex: Int, viewIdType: DeviceCareViewIdType): Int {
        return generateViewId(viewIdType, gridIndex)
    }

    /**
     * 메모리 프로그레스 View ID 조회
     */
    fun getMemoryProgressId(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.MemoryProgress, gridIndex)
    }

    /**
     * 저장소 프로그레스 View ID 조회
     */
    fun getStorageProgressId(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.StorageProgress, gridIndex)
    }

    /**
     * CPU 프로그레스 View ID 조회
     */
    fun getCpuProgressId(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.CpuProgress, gridIndex)
    }

    /**
     * 온도 프로그레스 View ID 조회
     */
    fun getTemperatureProgressId(gridIndex: Int): Int {
        return generateViewId(DeviceCareViewIdType.TemperatureProgress, gridIndex)
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = DeviceCareUpdateManager
    
    override fun getDataStore() = DeviceCareComponentDataStore
    
    override fun getLifecycle() = DeviceCareLifecycle
    
    override fun requiresAutoLifecycle() = true
}