package com.widgetworld.widgetcomponent.component.devicecare.ram

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.color.DynamicThemeColorProviders
import com.widgetworld.widgetcomponent.R
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.action.runCallbackBroadcastReceiver
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.component.viewid.ViewIdType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.frontend.Image
import com.widgetworld.core.frontend.Progress
import com.widgetworld.core.frontend.Spacer
import com.widgetworld.core.frontend.Text
import com.widgetworld.core.frontend.layout.Box
import com.widgetworld.core.frontend.layout.Column
import com.widgetworld.core.frontend.layout.Row
import com.widgetworld.core.proto.AlignmentType
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.proto.HorizontalAlignment
import com.widgetworld.core.proto.ProgressType
import com.widgetworld.core.proto.VerticalAlignment
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.modifier.backgroundColor
import com.widgetworld.core.proto.modifier.cornerRadius
import com.widgetworld.core.proto.modifier.fillMaxHeight
import com.widgetworld.core.proto.modifier.fillMaxWidth
import com.widgetworld.core.proto.modifier.height
import com.widgetworld.core.proto.modifier.hide
import com.widgetworld.core.proto.modifier.padding
import com.widgetworld.core.proto.modifier.partiallyUpdate
import com.widgetworld.core.proto.modifier.viewId
import com.widgetworld.core.proto.modifier.width
import com.widgetworld.core.proto.modifier.wrapContentHeight
import com.widgetworld.core.proto.modifier.wrapContentWidth
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetworld.widgetcomponent.theme.FontType
import com.widgetworld.widgetcomponent.theme.value

class RamWidget : WidgetComponent() {

    override fun getName(): String = "RAM"

    override fun getDescription(): String = "RAM"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_STATUS

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "RAM"

    @SuppressLint("RestrictedApi")
    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val backgroundColor = theme.surface.getColor(context).toArgb()
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val localSize = getLocal(WidgetLocalSize) as DpSize
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
                Spacer(modifier = WidgetModifier.fillMaxWidth().height(1f))
                RamLabel()
                Spacer(modifier = WidgetModifier.fillMaxWidth().height(1f))
                RamUsageProgress(
                    modifier = WidgetModifier.fillMaxWidth().height(
                        localSize.height.value * 0.23f
                    )
                )
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = RamUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = RamWidgetDataStore

    private fun WidgetScope.RamIcon() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val iconColor = theme.primary.getColor(context).toArgb()
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        val iconSize = height * 0.3f
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
                    TintColor {
                        argb = iconColor
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

    private fun WidgetScope.RamLabel() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()

        Text(
            text = "RAM",
            fontSize = FontType.LabelSmall.value,
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
            val context = getLocal(WidgetLocalContext) as Context
            val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
            val progressColor = theme.primary.getColor(context).copy(0.85f).toArgb()
            val progressBgColor = theme.surfaceVariant.getColor(context).toArgb()

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
                val context = getLocal(WidgetLocalContext) as Context
                val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
                val textColor = theme.onSurface.getColor(context).toArgb()

                Text(
                    modifier = WidgetModifier.wrapContentWidth().wrapContentHeight().viewId(
                        generateViewId(RamViewIdType.Text, gridIndex)
                    ).partiallyUpdate(true),
                    text = "${String.format("%.1f", currentRamUsage)}%",
                    fontSize = FontType.Caption.value,
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
