package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.color.DynamicThemeColorProviders
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.widgetworld.widgetcomponent.R
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.component.viewid.ViewIdType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.frontend.Image
import com.widgetworld.core.frontend.Progress
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

class DataUsageTrackerWidget : WidgetComponent() {

    override fun getName(): String = "Data Usage"

    override fun getDescription(): String = "Data Usage Tracker"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_STATUS

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "DataUsage"

    @SuppressLint("RestrictedApi")
    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val backgroundColor = theme.surface.getColor(context).toArgb()
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val isPreview = getLocal(WidgetLocalPreview) as Boolean

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor),
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
                DataUsageIcon()
                DataUsageTitle()
                DataUsageProgress(
                    modifier = WidgetModifier.fillMaxWidth().height(
                        localSize.height.value * 0.2f
                    )
                )
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = DataUsageUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = DataUsageDataStore

    private fun WidgetScope.DataUsageIcon() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val iconColor = theme.primary.getColor(context).toArgb()
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        val iconSize = height * 0.34f

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
                        drawableResId = R.drawable.ic_data_usage
                    }
                    TintColor {
                        argb = iconColor
                    }
                }
            )
        }
    }

    private fun WidgetScope.DataUsageTitle() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()

        Text(
            text = "Data Usage",
            fontSize = 12f,
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
            fontColor = Color(textColor)
        )
    }

    private fun WidgetScope.DataUsageProgress(modifier: WidgetModifier = WidgetModifier) {
        val context = getLocal(WidgetLocalContext) as Context
        val dataUsageData = getDataUsageValue()
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int

        Box(
            modifier = modifier.padding(horizontal = 8f, vertical = 2f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER_START
            }
        ) {
            val progressWidth = (getLocal(WidgetLocalSize) as DpSize).width - (8.dp * 2)
            val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
            val progressColor = theme.primary.getColor(context).toArgb()
            val progressBgColor = theme.surfaceVariant.getColor(context).toArgb()

            Progress(
                modifier = WidgetModifier
                    .width(progressWidth.value)
                    .fillMaxHeight()
                    .viewId(generateViewId(DataUsageViewIdType.Progress, gridIndex))
                    .partiallyUpdate(true)
                    .cornerRadius(context.getSystemBackgroundRadius().value),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = dataUsageData.usagePercent
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
                val textColor = theme.onSurface.getColor(context).toArgb()

                Text(
                    modifier = WidgetModifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .viewId(generateViewId(DataUsageViewIdType.Text, gridIndex))
                        .partiallyUpdate(true),
                    text = String.format("%.1f GB / %.1f GB", dataUsageData.currentUsageGb, dataUsageData.dataLimitGb),
                    fontSize = 8f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(textColor)
                )
            }
        }
    }

    private fun WidgetScope.getDataUsageValue(): DataUsageData {
        val currentState = getLocal(WidgetLocalState)
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        
        if (isPreview) {
            // Preview 모드: 샘플 데이터
            return DataUsageData.create(
                currentUsageBytes = 2L * 1024 * 1024 * 1024, // 2GB
                dataLimitBytes = 5L * 1024 * 1024 * 1024 // 5GB
            )
        }

        return currentState?.let { state ->
            val dataLimitBytes = state[DataUsagePreferenceKey.DataLimitBytes]
                ?: (DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024)
            val currentUsageBytes = state[DataUsagePreferenceKey.CurrentUsageBytes] ?: 0L
            
            DataUsageData.create(
                currentUsageBytes = currentUsageBytes,
                dataLimitBytes = dataLimitBytes
            )
        } ?: DataUsageData.getDefaultData()
    }

    override fun getViewIdTypes(): List<ViewIdType> = DataUsageViewIdType.all()

    fun getDataUsageTextId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.Text, gridIndex)
    }

    fun getDataUsageProgressId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.Progress, gridIndex)
    }
}

// Extension function for default data
private fun DataUsageData.Companion.getDefaultData(): DataUsageData {
    return create(
        currentUsageBytes = 0L,
        dataLimitBytes = DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
    )
}

