package com.widgetkit.core.component.devicecare.ram

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import com.widgetkit.core.R
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.action.runCallbackBroadcastReceiver
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.component.datastore.ComponentDataStore
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.component.viewid.ViewIdType
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.Image
import com.widgetkit.dsl.frontend.Progress
import com.widgetkit.dsl.frontend.Text
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.frontend.layout.Column
import com.widgetkit.dsl.frontend.layout.Row
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.ProgressType
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.hide
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.partiallyUpdate
import com.widgetkit.dsl.proto.modifier.viewId
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.modifier.wrapContentHeight
import com.widgetkit.dsl.proto.modifier.wrapContentWidth
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme

class RamWidget : WidgetComponent() {

    override fun getName(): String = "RAM"

    override fun getDescription(): String = "RAM"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "RAM"

    @SuppressLint("RestrictedApi")
    override fun WidgetScope.Content() {
        val theme = getLocal(WidgetLocalTheme)
        val backgroundColor = (theme?.surface as? Int) ?: Color.White.toArgb()
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        var backgroundModifier = WidgetModifier
            .fillMaxWidth().fillMaxHeight().backgroundColor(backgroundColor)
        if (!isPreview) {
            backgroundModifier = backgroundModifier.runCallbackBroadcastReceiver(
                context,
                widgetId?.appWidgetId ?: 0,
                RamWidgetAction()
            )
        }

        Box(
            modifier = backgroundModifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Column(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
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

    override fun requiresAutoLifecycle(): Boolean = false

    private fun WidgetScope.RamIcon() {
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        val iconSize = height * 0.34f
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        Box(
            modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
            contentProperty = {
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
        val theme = getLocal(WidgetLocalTheme)
        val textColor = (theme?.onSurfaceVariant as? Int) ?: Color.Black.toArgb()

        Text(
            text = "RAM",
            fontSize = 12f,
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
            fontColor = Color(textColor)
        )
    }

    private fun WidgetScope.RamUsageProgress(modifier: WidgetModifier = WidgetModifier) {
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
            val theme = getLocal(WidgetLocalTheme)
            val progressColor = (theme?.primary as? Int) ?: Color(0x808A8A8A).toArgb()
            val progressBgColor = (theme?.surfaceVariant as? Int) ?: Color(0xFFE3E3E3).toArgb()

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
                            argb = progressColor
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = progressBgColor
                        }
                    }
                }
            )
            Row(
                modifier = WidgetModifier.fillMaxWidth().wrapContentHeight().padding(end = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_END
                }
            ) {
                val theme = getLocal(WidgetLocalTheme)
                val textColor = (theme?.onSurface as? Int) ?: Color(0xFF000000).toArgb()

                Text(
                    modifier = WidgetModifier.wrapContentWidth().wrapContentHeight().viewId(
                        generateViewId(RamViewIdType.Text, gridIndex)
                    ).partiallyUpdate(true),
                    text = "${String.format("%.1f", currentRamUsage)}%",
                    fontSize = 8f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(textColor)
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
