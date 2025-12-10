package com.example.widget.component.devicecare

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Spacer
import com.example.dsl.component.Text
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.backgroundColor
import com.example.dsl.modifier.cornerRadius
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
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType

class DeviceCareWidget : WidgetComponent() {

    override fun getName() = "DeviceCare"

    override fun getDescription() = "DeviceCare"

    override fun getWidgetCategory() = WidgetCategory.DEVICE_INFO

    override fun getSizeType() = SizeType.SMALL

    override fun getWidgetTag() = "DeviceCare"

    override fun WidgetScope.Content() {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(Color(0xFFF5F5F5).toArgb())
                .cornerRadius(16f)
                .padding(top = 16f, start = 16f, end = 16f, bottom = 16f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            // Main content
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_TOP
                }
            ) {
                // Memory Progress Bar with Icon
                ProgressBarWithIcon(
                    iconRes = R.drawable.ic_memory,
                    label = "5.8GB / 8GB",
                    progress = 0.725f, // 5.8 / 8
                    progressColor = Color(0xFF2196F3).toArgb()
                )
                
                Spacer(height = 12f)
                
                // Storage Progress Bar with Icon
                ProgressBarWithIcon(
                    iconRes = R.drawable.ic_storage,
                    label = "120GB / 256GB",
                    progress = 0.47f, // 120 / 256
                    progressColor = Color(0xFF2196F3).toArgb()
                )
                
                Spacer(height = 16f)
                
                // Info Row 1: Temperature and Battery
                InfoRow(
                    icon1Res = R.drawable.ic_temperature,
                    text1 = "38°C · 24시간 가능",
                    icon2Res = null,
                    text2 = ""
                )
                
                Spacer(height = 8f)
                
                // Info Row 2: Data Usage
                InfoRow(
                    icon1Res = R.drawable.ic_data_usage,
                    text1 = "오늘 데이터 사용량: 1200MB",
                    icon2Res = null,
                    text2 = ""
                )
            }
            
            // Warning section overlay (top-right)
//            Box(
//                modifier = WidgetModifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(),
//                contentProperty = {
//                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_END
//                }
//            ) {
//                Column(
//                    modifier = WidgetModifier
//                        .wrapContentWidth()
//                        .wrapContentHeight(),
//                    contentProperty = {
//                        horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
//                        verticalAlignment = VerticalAlignment.V_ALIGN_TOP
//                    }
//                ) {
//                    Image(
//                        modifier = WidgetModifier
//                            .width(24f)
//                            .height(24f),
//                        contentProperty = {
//                            Provider {
//                                drawableResId = R.drawable.ic_warning
//                            }
//                        }
//                    )
//                    Spacer(height = 4f)
//                    Text(
//                        contentProperty = {
//                            TextContent {
//                                text = "경고"
//                            }
//                            fontSize = 10f
//                            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
//                            FontColor {
//                                Color {
//                                    argb = Color(0xFFFF0000).toArgb()
//                                }
//                            }
//                        }
//                    )
//                }
//            }
        }
    }

    private fun WidgetScope.ProgressBarWithIcon(
        iconRes: Int,
        label: String,
        progress: Float,
        progressColor: Int
    ) {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            // Icon
            Image(
                modifier = WidgetModifier
                    .width(20f)
                    .height(20f),
                contentProperty = {
                    Provider {
                        drawableResId = iconRes
                    }
                }
            )
            
            Spacer(width = 8f)
            
            // Progress bar and label column
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                // Progress bar
                Progress(
                    modifier = WidgetModifier
                        .fillMaxWidth()
                        .height(12f)
                        .cornerRadius(6f),
                    contentProperty = {
                        progressType = ProgressType.PROGRESS_TYPE_LINEAR
                        progressValue = progress
                        maxValue = 1f
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
                
                Spacer(height = 4f)
                
                // Label
                Text(
                    contentProperty = {
                        TextContent {
                            text = label
                        }
                        fontSize = 11f
                        fontWeight = FontWeight.FONT_WEIGHT_NORMAL
                        FontColor {
                            Color {
                                argb = Color(0xFF666666).toArgb()
                            }
                        }
                    }
                )
            }
        }
    }
    
    private fun WidgetScope.InfoRow(
        icon1Res: Int,
        text1: String,
        icon2Res: Int?,
        text2: String
    ) {
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            // First icon and text
            Image(
                modifier = WidgetModifier
                    .width(16f)
                    .height(16f),
                contentProperty = {
                    Provider {
                        drawableResId = icon1Res
                    }
                }
            )
            
            Spacer(width = 4f)
            
            Text(
                contentProperty = {
                    TextContent {
                        text = text1
                    }
                    fontSize = 11f
                    fontWeight = FontWeight.FONT_WEIGHT_NORMAL
                    FontColor {
                        Color {
                            argb = Color(0xFF666666).toArgb()
                        }
                    }
                }
            )
            
            // Second icon and text (optional)
            if (icon2Res != null && text2.isNotEmpty()) {
                Spacer(width = 12f)
                
                Image(
                    modifier = WidgetModifier
                        .width(16f)
                        .height(16f),
                    contentProperty = {
                        Provider {
                            drawableResId = icon2Res
                        }
                    }
                )
                
                Spacer(width = 4f)
                
                Text(
                    contentProperty = {
                        TextContent {
                            text = text2
                        }
                        fontSize = 11f
                        fontWeight = FontWeight.FONT_WEIGHT_NORMAL
                        FontColor {
                            Color {
                                argb = Color(0xFF666666).toArgb()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun WidgetScope.Spacer(height: Float = 0f, width: Float = 0f) {
        if (height > 0f) {
            Spacer(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .height(height)
            )
        } else if (width > 0f) {
            Spacer(
                modifier = WidgetModifier
                    .width(width)
                    .fillMaxHeight()
            )
        }
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