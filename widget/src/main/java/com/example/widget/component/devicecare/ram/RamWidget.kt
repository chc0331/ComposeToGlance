package com.example.widget.component.devicecare.ram

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.localprovider.WidgetLocalContext
import com.example.dsl.localprovider.WidgetLocalGlanceId
import com.example.dsl.localprovider.WidgetLocalGridIndex
import com.example.dsl.localprovider.WidgetLocalPreview
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.backgroundColor
import com.example.dsl.modifier.cornerRadius
import com.example.dsl.modifier.fillMaxHeight
import com.example.dsl.modifier.fillMaxWidth
import com.example.dsl.modifier.height
import com.example.dsl.modifier.hide
import com.example.dsl.modifier.padding
import com.example.dsl.modifier.partiallyUpdate
import com.example.dsl.modifier.viewId
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
import com.example.widget.action.runCallbackAction
import com.example.widget.component.WidgetComponent
import com.example.widget.component.datastore.ComponentDataStore
import com.example.widget.component.lifecycle.ComponentLifecycle
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType
import com.example.widget.util.getSystemBackgroundRadius

class RamWidget : WidgetComponent() {

    override fun getName(): String = "RAM"

    override fun getDescription(): String = "RAM"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "RAM"

    @SuppressLint("RestrictedApi")
    override fun WidgetScope.Content() {
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        var backgroundModifier = WidgetModifier
            .fillMaxWidth().fillMaxHeight().backgroundColor(Color.White.toArgb())
        if (!isPreview)
            backgroundModifier = backgroundModifier.runCallbackAction(
                context, widgetId?.appWidgetId ?: 0, RamWidgetAction()
            )

        Box(
            modifier = backgroundModifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
            Column(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }) {
                RamIcon()
                RamTitle()
                RamUsageProgress(
                    modifier = WidgetModifier.fillMaxWidth().height(
                        localSize.height.value * 0.2f
                    )
                )
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = RamUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = RamWidgetDataStore

    override fun getLifecycle(): ComponentLifecycle? = null

    override fun requiresAutoLifecycle(): Boolean = false

    private fun WidgetScope.RamIcon() {
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        val iconSize = height * 0.34f
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        Box(modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(), contentProperty = {
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }
        ) {
            Image(
                modifier = WidgetModifier
                    .width(iconSize)
                    .height(iconSize),
                contentProperty = {
                    Provider {
                        drawableResId = R.drawable.ic_memory
                    }
                }
            )
            Image(
                modifier = WidgetModifier
                    .viewId(generateViewId(RamViewIdType.Animation, gridIndex))
                    .partiallyUpdate(false)
                    .width(iconSize)
                    .height(iconSize)
                    .hide(true),
                contentProperty = {
                    Provider {
                        drawableResId = R.layout.memory_cleaning
                        animation = true
                        infiniteLoop = false
                    }
                }
            )
        }
    }

    private fun WidgetScope.RamTitle() {
        Text {
            TextContent {
                text = "RAM"
            }
            fontSize = 12f
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
        }
    }

    private fun WidgetScope.RamUsageProgress(
        modifier: WidgetModifier = WidgetModifier
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val currentRamUsage = getRamValue()
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int

        Box(
            modifier = modifier.padding(horizontal = 8f, vertical = 2f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER_START
            }
        ) {
            val progressWidth = (getLocal(WidgetLocalSize) as DpSize).width - (8.dp * 2)
            Progress(
                modifier = WidgetModifier
                    .width(progressWidth.value).fillMaxHeight()
                    .viewId(generateViewId(RamViewIdType.Progress, gridIndex))
                    .partiallyUpdate(true)
                    .cornerRadius(context.getSystemBackgroundRadius().value),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = currentRamUsage
                    maxValue = 100f
                    ProgressColor {
                        Color {
                            argb = Color(0x808A8A8A).toArgb()
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color(0xFFE3E3E3).toArgb()
                        }
                    }
                }
            )
            Row(
                modifier = WidgetModifier.fillMaxWidth().wrapContentHeight().padding(end = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_END
                }) {
                Text(
                    modifier = WidgetModifier.wrapContentWidth().wrapContentHeight().viewId(
                        generateViewId(RamViewIdType.Text, gridIndex)
                    ).partiallyUpdate(true),
                    contentProperty = {
                        TextContent {
                            text = "${String.format("%.1f", currentRamUsage)}%"
                        }
                        fontSize = 8f
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
    }

    private fun WidgetScope.getRamValue(): Float {
        val currentState = getLocal(WidgetLocalState)
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        if (isPreview) return 66.7f
        return currentState?.let { state ->
            val currentValue = state[RamPreferenceKey.UsagePercent]
            currentValue ?: 0f
        } ?: 0f
    }

    override fun getViewIdTypes(): List<ViewIdType> = RamViewIdType.all()

    fun getRamTextId(gridIndex: Int): Int {
        return generateViewId(RamViewIdType.Text, gridIndex)
    }

    fun getRamProgressId(gridIndex: Int): Int {
        return generateViewId(RamViewIdType.Progress, gridIndex)
    }

    fun getRamAnimationId(gridIndex: Int): Int {
        return generateViewId(RamViewIdType.Animation, gridIndex)
    }
}